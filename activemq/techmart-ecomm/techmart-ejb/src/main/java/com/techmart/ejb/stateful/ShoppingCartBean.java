package com.techmart.ejb.stateful;

import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.model.CartItem;
import com.techmart.model.Product;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.logging.Logger;

@Stateful
@StatefulTimeout(value = 30, unit = java.util.concurrent.TimeUnit.MINUTES)
public class ShoppingCartBean {

    private static final Logger logger = Logger.getLogger(ShoppingCartBean.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    @EJB
    private InventoryManagerBean inventoryManager;

    private String customerId;
    private Map<Long, CartItem> cartItems = new LinkedHashMap<>();
    private long createdAt;
    private long lastAccessedAt;

    @PostActivate
    public void onActivate() {
        logger.info("ShoppingCartBean activated for customer: " + customerId);
        lastAccessedAt = System.currentTimeMillis();
    }

    @PrePassivate
    public void onPassivate() {
        logger.info("ShoppingCartBean passivating for customer: " + customerId);
    }

    public void initCart(String customerId) {
        this.customerId = customerId;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = this.createdAt;
        logger.info("Cart initialized for customer: " + customerId);
    }

    public boolean addItem(Long productId, int quantity) {
        lastAccessedAt = System.currentTimeMillis();
        int availableStock = inventoryManager.getStock(productId);

        int currentQty = cartItems.containsKey(productId)
                ? cartItems.get(productId).getQuantity() : 0;

        if (availableStock < currentQty + quantity) {
            logger.warning("Cannot add " + quantity + " of product " + productId
                    + ". Available: " + availableStock + ", In cart: " + currentQty);
            return false;
        }

        Product product = em.find(Product.class, productId);
        if (product == null) {
            logger.warning("Product not found: " + productId);
            return false;
        }

        if (cartItems.containsKey(productId)) {
            CartItem existing = cartItems.get(productId);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            cartItems.put(productId, new CartItem(
                    productId, product.getName(), product.getPrice(), quantity));
        }

        logger.info("Added " + quantity + "x " + product.getName() + " to cart for " + customerId);
        return true;
    }

    public boolean removeItem(Long productId) {
        lastAccessedAt = System.currentTimeMillis();
        if (cartItems.remove(productId) != null) {
            logger.info("Removed product " + productId + " from cart for " + customerId);
            return true;
        }
        return false;
    }

    public boolean updateQuantity(Long productId, int newQuantity) {
        lastAccessedAt = System.currentTimeMillis();
        if (newQuantity <= 0) {
            return removeItem(productId);
        }
        int availableStock = inventoryManager.getStock(productId);
        if (availableStock < newQuantity) {
            return false;
        }
        CartItem item = cartItems.get(productId);
        if (item != null) {
            item.setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    public void clearCart() {
        cartItems.clear();
        lastAccessedAt = System.currentTimeMillis();
        logger.info("Cart cleared for customer: " + customerId);
    }

    public List<CartItem> getItems() {
        lastAccessedAt = System.currentTimeMillis();
        return new ArrayList<>(cartItems.values());
    }

    public double getTotal() {
        return cartItems.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public int getItemCount() {
        return cartItems.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public Map<Long, Integer> getProductQuantityMap() {
        Map<Long, Integer> result = new LinkedHashMap<>();
        cartItems.forEach((id, item) -> result.put(id, item.getQuantity()));
        return result;
    }

    public String getCustomerId() { return customerId; }
    public long getCreatedAt() { return createdAt; }
    public long getLastAccessedAt() { return lastAccessedAt; }
    public boolean isEmpty() { return cartItems.isEmpty(); }

    @Remove
    public void checkout() {
        logger.info("Cart checked out and removed for customer: " + customerId);
    }

    @Remove
    public void abandon() {
        logger.info("Cart abandoned for customer: " + customerId);
        cartItems.clear();
    }
}