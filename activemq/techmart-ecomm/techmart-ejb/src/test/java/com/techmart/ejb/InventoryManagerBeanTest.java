package com.techmart.ejb;

import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryManagerBean Tests - Singleton EJB")
class InventoryManagerBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Product> query;

    private InventoryManagerBean inventoryManager;

    private Product laptop;
    private Product mouse;

    @BeforeEach
    void setUp() throws Exception {
        inventoryManager = new InventoryManagerBean();

        Field emField = InventoryManagerBean.class
                .getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(inventoryManager, em);

        laptop = new Product(
                "Laptop Pro 15",
                "High-performance laptop",
                1299.99, 50, "Electronics");
        laptop.setId(1L);

        mouse = new Product(
                "Wireless Mouse",
                "Ergonomic mouse",
                29.99, 200, "Electronics");
        mouse.setId(2L);

        List<Product> products = Arrays.asList(laptop, mouse);

        when(em.createQuery(
                "SELECT p FROM Product p", Product.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(products);

        inventoryManager.init();
    }

    @Test
    @DisplayName("TC01 - Cache loads correct number of products on startup")
    void testCacheInitializationSize() {
        assertEquals(2, inventoryManager.getCacheSize(),
                "Cache must contain 2 products after @PostConstruct");
    }

    @Test
    @DisplayName("TC02 - getStock() returns correct stock for product 1")
    void testGetStockProductOne() {
        assertEquals(50, inventoryManager.getStock(1L),
                "Laptop stock must be 50");
    }

    @Test
    @DisplayName("TC03 - getStock() returns correct stock for product 2")
    void testGetStockProductTwo() {
        assertEquals(200, inventoryManager.getStock(2L),
                "Mouse stock must be 200");
    }

    @Test
    @DisplayName("TC04 - getStock() returns 0 for unknown product")
    void testGetStockUnknownProduct() {
        when(em.find(Product.class, 999L)).thenReturn(null);
        assertEquals(0, inventoryManager.getStock(999L),
                "Unknown product must return 0 stock");
    }

    @Test
    @DisplayName("TC05 - reserveStock() reduces stock by correct amount")
    void testReserveStockReducesStock() {
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.merge(laptop)).thenReturn(laptop);

        boolean result = inventoryManager.reserveStock(1L, 10);

        assertTrue(result, "Reservation must succeed");
        assertEquals(40, inventoryManager.getStock(1L),
                "Stock must reduce from 50 to 40");
    }

    @Test
    @DisplayName("TC06 - reserveStock() fails when stock insufficient")
    void testReserveStockFailsWhenInsufficient() {
        boolean result = inventoryManager.reserveStock(1L, 999);

        assertFalse(result,
                "Reservation must fail when quantity exceeds stock");
        assertEquals(50, inventoryManager.getStock(1L),
                "Stock must remain unchanged after failed reservation");
    }

    @Test
    @DisplayName("TC07 - restoreStock() increases stock correctly")
    void testRestoreStockIncreasesStock() {
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.merge(laptop)).thenReturn(laptop);

        inventoryManager.restoreStock(1L, 25);

        assertEquals(75, inventoryManager.getStock(1L),
                "Stock must increase from 50 to 75");
    }

    @Test
    @DisplayName("TC08 - Cache hits tracked correctly on repeated access")
    void testCacheHitTracking() {
        int hitsBefore = inventoryManager.getTotalCacheHits();

        inventoryManager.getStock(1L);
        inventoryManager.getStock(1L);
        inventoryManager.getStock(2L);

        assertTrue(
                inventoryManager.getTotalCacheHits() >= hitsBefore + 3,
                "Each cache access must increment hit counter");
    }

    @Test
    @DisplayName("TC09 - getFullInventorySnapshot() returns correct copy")
    void testInventorySnapshot() {
        Map<Long, Integer> snapshot =
                inventoryManager.getFullInventorySnapshot();

        assertNotNull(snapshot, "Snapshot must not be null");
        assertEquals(2, snapshot.size(),
                "Snapshot must contain all products");
        assertEquals(50, snapshot.get(1L),
                "Laptop stock in snapshot must be 50");
        assertEquals(200, snapshot.get(2L),
                "Mouse stock in snapshot must be 200");
    }

    @Test
    @DisplayName("TC10 - Cache init time is recorded after startup")
    void testCacheInitTimeRecorded() {
        assertTrue(inventoryManager.getCacheInitTimeMs() >= 0,
                "Cache init time must be a non-negative value");
    }
}