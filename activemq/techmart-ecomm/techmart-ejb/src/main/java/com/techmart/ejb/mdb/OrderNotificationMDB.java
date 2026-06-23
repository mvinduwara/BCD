package com.techmart.ejb.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import java.util.logging.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "jms/OrderQueue"),
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(
                        propertyName = "acknowledgeMode",
                        propertyValue = "Auto-acknowledge"),
                @ActivationConfigProperty(
                        propertyName = "maxSession",
                        propertyValue = "5")
        }
)
public class OrderNotificationMDB implements MessageListener {

    private static final Logger logger = Logger.getLogger(OrderNotificationMDB.class.getName());
    private static int processedCount = 0;

    @Override
    public void onMessage(Message message) {
        long start = System.currentTimeMillis();
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String content      = textMessage.getText();
                String orderId      = message.getStringProperty("orderId");
                String customerEmail = message.getStringProperty("customerEmail");

                logger.info("[MDB-OrderQueue] Received message for order: " + orderId);
                logger.info("[MDB-OrderQueue] Message content: " + content);

                processOrder(orderId, customerEmail, content);

                long elapsed = System.currentTimeMillis() - start;
                processedCount++;
                logger.info("[MDB-OrderQueue] Order " + orderId
                        + " processed in " + elapsed + "ms. Total processed: " + processedCount);
            }
        } catch (Exception e) {
            logger.severe("[MDB-OrderQueue] Error processing message: " + e.getMessage());
        }
    }

    private void processOrder(String orderId, String customerEmail, String content) {
        logger.info("[MDB-OrderQueue] Processing order workflow for order: " + orderId);
        logger.info("[MDB-OrderQueue] Would send email to: " + customerEmail);
        logger.info("[MDB-OrderQueue] Would trigger warehouse notification for order: " + orderId);
    }

    public static int getProcessedCount() { return processedCount; }
}