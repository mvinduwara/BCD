package com.techmart.ejb.mdb;

import com.techmart.ejb.singleton.InventoryManagerBean;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import java.util.logging.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "jms/InventoryTopic"),
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "acknowledgeMode",
                        propertyValue = "Auto-acknowledge"),
                @ActivationConfigProperty(
                        propertyName = "subscriptionDurability",
                        propertyValue = "Durable"),
                @ActivationConfigProperty(
                        propertyName = "clientId",
                        propertyValue = "TechMartInventoryClient"),
                @ActivationConfigProperty(
                        propertyName = "subscriptionName",
                        propertyValue = "InventoryUpdateSubscription")
        }
)
public class InventoryUpdateMDB implements MessageListener {

    private static final Logger logger = Logger.getLogger(InventoryUpdateMDB.class.getName());

    @EJB
    private InventoryManagerBean inventoryManager;

    private static int updateCount = 0;

    @Override
    public void onMessage(Message message) {
        long start = System.currentTimeMillis();
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String content = textMessage.getText();
                logger.info("[MDB-InventoryTopic] Received inventory update: " + content);

                String productIdStr = message.getStringProperty("productId");
                String action       = message.getStringProperty("action");
                String quantityStr  = message.getStringProperty("quantity");

                if (productIdStr != null && action != null && quantityStr != null) {
                    Long productId = Long.parseLong(productIdStr);
                    int quantity   = Integer.parseInt(quantityStr);
                    processInventoryUpdate(productId, action, quantity);
                }

                long elapsed = System.currentTimeMillis() - start;
                updateCount++;
                logger.info("[MDB-InventoryTopic] Inventory update processed in "
                        + elapsed + "ms. Total updates: " + updateCount);
            }
        } catch (Exception e) {
            logger.severe("[MDB-InventoryTopic] Error processing inventory update: " + e.getMessage());
        }
    }

    private void processInventoryUpdate(Long productId, String action, int quantity) {
        String actionUpper = action.toUpperCase();
        if ("RESTOCK".equals(actionUpper)) {
            inventoryManager.restoreStock(productId, quantity);
            logger.info("[MDB-InventoryTopic] Restocked product " + productId
                    + " by " + quantity + " units.");
        } else if ("REFRESH".equals(actionUpper)) {
            inventoryManager.refreshCache();
            logger.info("[MDB-InventoryTopic] Cache refreshed.");
        } else {
            logger.warning("[MDB-InventoryTopic] Unknown action: " + action);
        }
    }

    public static int getUpdateCount() { return updateCount; }
}