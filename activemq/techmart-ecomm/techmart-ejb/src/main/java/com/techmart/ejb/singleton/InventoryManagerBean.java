package com.techmart.ejb.singleton;

import com.techmart.model.Product;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class InventoryManagerBean {

    private static final Logger logger = Logger.getLogger(InventoryManagerBean.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    private Map<Long, Integer> inventoryCache = new ConcurrentHashMap<>();

    private long cacheInitTimeMs;
    private int totalCacheHits = 0;
    private int totalCacheMisses = 0;

    @PostConstruct
    public void init() {
        long start = System.currentTimeMillis();
        logger.info("InventoryManagerBean initializing - loading inventory cache...");

        try {
            List<Product> products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
            for (Product p : products) {
                inventoryCache.put(p.getId(), p.getStockQuantity());
            }
            cacheInitTimeMs = System.currentTimeMillis() - start;
            logger.info("Inventory cache loaded: " + inventoryCache.size() + " products in " + cacheInitTimeMs + "ms");
        } catch (Exception e) {
            logger.severe("Failed to initialize inventory cache: " + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("InventoryManagerBean shutting down. Total cache hits: "
                + totalCacheHits + ", misses: " + totalCacheMisses);
        inventoryCache.clear();
    }

    @Lock(LockType.READ)
    public int getStock(Long productId) {
        if (inventoryCache.containsKey(productId)) {
            totalCacheHits++;
            return inventoryCache.get(productId);
        }
        totalCacheMisses++;
        Product product = em.find(Product.class, productId);
        if (product != null) {
            inventoryCache.put(productId, product.getStockQuantity());
            return product.getStockQuantity();
        }
        return 0;
    }

    @Lock(LockType.WRITE)
    public boolean reserveStock(Long productId, int quantity) {
        int current = getStock(productId);
        if (current < quantity) {
            logger.warning("Insufficient stock for product " + productId
                    + ". Requested: " + quantity + ", Available: " + current);
            return false;
        }
        int updated = current - quantity;
        inventoryCache.put(productId, updated);

        Product product = em.find(Product.class, productId);
        if (product != null) {
            product.setStockQuantity(updated);
            em.merge(product);
        }
        logger.info("Stock reserved for product " + productId
                + ". Remaining: " + updated);
        return true;
    }

    @Lock(LockType.WRITE)
    public void restoreStock(Long productId, int quantity) {
        int current = inventoryCache.getOrDefault(productId, 0);
        int updated = current + quantity;
        inventoryCache.put(productId, updated);

        Product product = em.find(Product.class, productId);
        if (product != null) {
            product.setStockQuantity(updated);
            em.merge(product);
        }
        logger.info("Stock restored for product " + productId
                + ". New quantity: " + updated);
    }

    @Lock(LockType.WRITE)
    public void refreshCache() {
        inventoryCache.clear();
        init();
        logger.info("Inventory cache manually refreshed.");
    }

    @Lock(LockType.READ)
    public Map<Long, Integer> getFullInventorySnapshot() {
        return Map.copyOf(inventoryCache);
    }

    @Lock(LockType.READ)
    public long getCacheInitTimeMs() { return cacheInitTimeMs; }

    @Lock(LockType.READ)
    public int getTotalCacheHits() { return totalCacheHits; }

    @Lock(LockType.READ)
    public int getTotalCacheMisses() { return totalCacheMisses; }

    @Lock(LockType.READ)
    public int getCacheSize() { return inventoryCache.size(); }
}