package com.techmart.ejb.async;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
public class NotificationService {

    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    private static long totalNotificationsSent = 0;
    private static long totalNotificationTimeMs = 0;

    @Asynchronous
    public Future<String> sendOrderConfirmationAsync(Long orderId, String email) {
        long start = System.currentTimeMillis();
        logger.info("[ASYNC] Sending order confirmation for order " + orderId + " to " + email);

        try {
            Thread.sleep(100);
            String message = "Order #" + orderId + " confirmed. Confirmation sent to " + email;
            long elapsed = System.currentTimeMillis() - start;
            recordMetric(elapsed);
            logger.info("[ASYNC] Order confirmation sent in " + elapsed + "ms for order " + orderId);
            return new AsyncResult<>(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("[ASYNC] Notification interrupted for order " + orderId);
            return new AsyncResult<>("FAILED: " + e.getMessage());
        }
    }

    @Asynchronous
    public Future<String> sendOrderCancellationAsync(Long orderId, String email) {
        long start = System.currentTimeMillis();
        logger.info("[ASYNC] Sending cancellation for order " + orderId + " to " + email);

        try {
            Thread.sleep(80);
            String message = "Order #" + orderId + " cancelled. Notification sent to " + email;
            long elapsed = System.currentTimeMillis() - start;
            recordMetric(elapsed);
            logger.info("[ASYNC] Cancellation sent in " + elapsed + "ms for order " + orderId);
            return new AsyncResult<>(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("[ASYNC] Cancellation notification interrupted for order " + orderId);
            return new AsyncResult<>("FAILED: " + e.getMessage());
        }
    }

    @Asynchronous
    public Future<String> sendInventoryAlertAsync(Long productId, int remainingStock) {
        long start = System.currentTimeMillis();
        logger.info("[ASYNC] Sending inventory alert for product " + productId
                + ". Remaining stock: " + remainingStock);

        try {
            Thread.sleep(50);
            String message = "LOW STOCK ALERT: Product #" + productId
                    + " has only " + remainingStock + " units remaining.";
            long elapsed = System.currentTimeMillis() - start;
            recordMetric(elapsed);
            logger.info("[ASYNC] Inventory alert sent in " + elapsed + "ms");
            return new AsyncResult<>(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new AsyncResult<>("FAILED: " + e.getMessage());
        }
    }

    private synchronized void recordMetric(long elapsedMs) {
        totalNotificationsSent++;
        totalNotificationTimeMs += elapsedMs;
    }

    public static long getTotalNotificationsSent() { return totalNotificationsSent; }

    public static double getAverageNotificationTimeMs() {
        return totalNotificationsSent == 0 ? 0
                : (double) totalNotificationTimeMs / totalNotificationsSent;
    }
}