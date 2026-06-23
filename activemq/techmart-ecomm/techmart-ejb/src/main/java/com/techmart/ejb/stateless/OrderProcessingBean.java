package com.techmart.ejb.stateless;

import com.techmart.ejb.async.NotificationService;
import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.model.Order;
import com.techmart.model.OrderItem;
import com.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.jms.*;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
public class OrderProcessingBean {

    private static final Logger logger = Logger.getLogger(OrderProcessingBean.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    @EJB
    private InventoryManagerBean inventoryManager;

    @EJB
    private NotificationService notificationService;

    @Resource(lookup = "jms/TechMartConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/OrderQueue")
    private Queue orderQueue;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Order placeOrder(String customerId, String customerEmail, Map<Long, Integer> productQuantities) {
        long start = System.currentTimeMillis();
        logger.info("Placing order for customer: " + customerId);

        Order order = new Order(customerId, customerEmail);
        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            boolean reserved = inventoryManager.reserveStock(productId, quantity);
            if (!reserved) {
                throw new RuntimeException("Insufficient stock for product: " + productId);
            }

            Product product = em.find(Product.class, productId);
            if (product == null) {
                throw new RuntimeException("Product not found: " + productId);
            }

            OrderItem item = new OrderItem(order, product, quantity);
            order.getItems().add(item);
            total += item.getSubtotal();
        }

        order.setTotalAmount(total);
        order.setStatus(Order.Status.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        em.persist(order);
        em.flush();

        long processingTime = System.currentTimeMillis() - start;
        order.setProcessingTimeMs(processingTime);
        em.merge(order);

        sendOrderToQueue(order);
        notificationService.sendOrderConfirmationAsync(order.getId(), customerEmail);

        logger.info("Order " + order.getId() + " placed successfully in " + processingTime + "ms");
        return order;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Order cancelOrder(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }
        if (order.getStatus() == Order.Status.SHIPPED || order.getStatus() == Order.Status.DELIVERED) {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }

        for (OrderItem item : order.getItems()) {
            inventoryManager.restoreStock(item.getProduct().getId(), item.getQuantity());
        }

        order.setStatus(Order.Status.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        em.merge(order);

        notificationService.sendOrderCancellationAsync(orderId, order.getCustomerEmail());
        logger.info("Order " + orderId + " cancelled.");
        return order;
    }

    public Order findById(Long orderId) {
        return em.find(Order.class, orderId);
    }

    public List<Order> findOrdersByCustomer(String customerId) {
        TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC",
                Order.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public List<Order> findAllOrders() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.createdAt DESC", Order.class)
                .getResultList();
    }

    private void sendOrderToQueue(Order order) {
        try (Connection conn = connectionFactory.createConnection();
             Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            MessageProducer producer = session.createProducer(orderQueue);
            TextMessage message = session.createTextMessage();
            message.setText("ORDER_ID:" + order.getId()
                    + "|CUSTOMER:" + order.getCustomerId()
                    + "|AMOUNT:" + order.getTotalAmount()
                    + "|STATUS:" + order.getStatus());
            message.setStringProperty("orderId", String.valueOf(order.getId()));
            message.setStringProperty("customerEmail", order.getCustomerEmail());
            producer.send(message);
            logger.info("Order " + order.getId() + " sent to OrderQueue.");
        } catch (JMSException e) {
            logger.severe("Failed to send order to queue: " + e.getMessage());
        }
    }
}