package com.techmart.ejb;

import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.ejb.stateful.ShoppingCartBean;
import com.techmart.model.CartItem;
import com.techmart.model.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShoppingCartBean Tests - Stateful EJB")
class ShoppingCartBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private InventoryManagerBean inventoryManager;

    private ShoppingCartBean cart;

    private Product laptop;
    private Product mouse;
    private Product keyboard;

    @BeforeEach
    void setUp() throws Exception {
        cart = new ShoppingCartBean();

        Field emField = ShoppingCartBean.class
                .getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(cart, em);

        Field imField = ShoppingCartBean.class
                .getDeclaredField("inventoryManager");
        imField.setAccessible(true);
        imField.set(cart, inventoryManager);

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

        keyboard = new Product(
                "Mechanical Keyboard",
                "Tactile keyboard",
                89.99, 150, "Electronics");
        keyboard.setId(3L);

        cart.initCart("CUSTOMER-TEST-001");
    }

    @Test
    @DisplayName("TC01 - Cart is empty after initialization")
    void testCartEmptyAfterInit() {
        assertTrue(cart.isEmpty(),
                "Cart must be empty after initialization");
        assertEquals(0, cart.getItemCount(),
                "Item count must be 0");
        assertEquals(0.0, cart.getTotal(), 0.001,
                "Total must be 0.0");
    }

    @Test
    @DisplayName("TC02 - initCart() sets customer ID correctly")
    void testCustomerIdSetCorrectly() {
        assertEquals("CUSTOMER-TEST-001", cart.getCustomerId(),
                "Customer ID must match initialization value");
    }

    @Test
    @DisplayName("TC03 - addItem() adds product successfully")
    void testAddItemSuccess() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        boolean result = cart.addItem(1L, 2);

        assertTrue(result, "addItem() must return true on success");
        assertFalse(cart.isEmpty(),
                "Cart must not be empty after adding item");
        assertEquals(2, cart.getItemCount(),
                "Item count must be 2");
    }

    @Test
    @DisplayName("TC04 - addItem() fails when stock is insufficient")
    void testAddItemFailsInsufficientStock() {
        when(inventoryManager.getStock(1L)).thenReturn(3);

        boolean result = cart.addItem(1L, 10);

        assertFalse(result,
                "addItem() must return false when stock insufficient");
        assertTrue(cart.isEmpty(),
                "Cart must remain empty after failed add");
    }

    @Test
    @DisplayName("TC05 - addItem() same product twice accumulates quantity")
    void testAddSameProductAccumulates() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        cart.addItem(1L, 2);
        cart.addItem(1L, 3);

        assertEquals(5, cart.getItemCount(),
                "Quantities must accumulate for same product");
        assertEquals(1, cart.getItems().size(),
                "Must remain 1 distinct product");
    }

    @Test
    @DisplayName("TC06 - getTotal() calculates correct cart total")
    void testGetTotalCalculation() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(inventoryManager.getStock(2L)).thenReturn(200);
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.find(Product.class, 2L)).thenReturn(mouse);

        cart.addItem(1L, 2);
        cart.addItem(2L, 3);

        double expected = (1299.99 * 2) + (29.99 * 3);
        assertEquals(expected, cart.getTotal(), 0.01,
                "Total must equal sum of all item subtotals");
    }

    @Test
    @DisplayName("TC07 - removeItem() removes product from cart")
    void testRemoveItemSuccess() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        cart.addItem(1L, 2);
        boolean removed = cart.removeItem(1L);

        assertTrue(removed,
                "removeItem() must return true on success");
        assertTrue(cart.isEmpty(),
                "Cart must be empty after removing only item");
    }

    @Test
    @DisplayName("TC08 - removeItem() on non-existent product returns false")
    void testRemoveNonExistentItem() {
        boolean result = cart.removeItem(999L);
        assertFalse(result,
                "removeItem() must return false for non-existent product");
    }

    @Test
    @DisplayName("TC09 - updateQuantity() updates item quantity correctly")
    void testUpdateQuantitySuccess() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        cart.addItem(1L, 1);
        boolean updated = cart.updateQuantity(1L, 5);

        assertTrue(updated,
                "updateQuantity() must return true on success");
        assertEquals(5, cart.getItemCount(),
                "Item count must reflect updated quantity");
    }

    @Test
    @DisplayName("TC10 - updateQuantity() to 0 removes the item")
    void testUpdateQuantityToZeroRemovesItem() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        cart.addItem(1L, 3);
        cart.updateQuantity(1L, 0);

        assertTrue(cart.isEmpty(),
                "Cart must be empty after updating quantity to 0");
    }

    @Test
    @DisplayName("TC11 - clearCart() removes all items")
    void testClearCart() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(inventoryManager.getStock(2L)).thenReturn(200);
        when(inventoryManager.getStock(3L)).thenReturn(150);
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.find(Product.class, 2L)).thenReturn(mouse);
        when(em.find(Product.class, 3L)).thenReturn(keyboard);

        cart.addItem(1L, 1);
        cart.addItem(2L, 2);
        cart.addItem(3L, 3);

        assertFalse(cart.isEmpty(),
                "Cart must have items before clear");

        cart.clearCart();

        assertTrue(cart.isEmpty(),
                "Cart must be empty after clearCart()");
        assertEquals(0.0, cart.getTotal(), 0.001,
                "Total must be 0 after clearCart()");
        assertEquals(0, cart.getItemCount(),
                "Item count must be 0 after clearCart()");
    }

    @Test
    @DisplayName("TC12 - getItems() returns correct CartItem details")
    void testGetItemsDetails() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(em.find(Product.class, 1L)).thenReturn(laptop);

        cart.addItem(1L, 3);
        List<CartItem> items = cart.getItems();

        assertEquals(1, items.size(),
                "Must have 1 cart item");
        assertEquals("Laptop Pro 15",
                items.get(0).getProductName());
        assertEquals(1299.99,
                items.get(0).getUnitPrice(), 0.01);
        assertEquals(3, items.get(0).getQuantity());
        assertEquals(1299.99 * 3,
                items.get(0).getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("TC13 - getProductQuantityMap() returns correct map")
    void testGetProductQuantityMap() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(inventoryManager.getStock(2L)).thenReturn(200);
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.find(Product.class, 2L)).thenReturn(mouse);

        cart.addItem(1L, 3);
        cart.addItem(2L, 5);

        Map<Long, Integer> map = cart.getProductQuantityMap();

        assertEquals(2, map.size(),
                "Map must have 2 entries");
        assertEquals(3, map.get(1L),
                "Laptop quantity must be 3");
        assertEquals(5, map.get(2L),
                "Mouse quantity must be 5");
    }

    @Test
    @DisplayName("TC14 - Multiple products total calculated correctly")
    void testMultipleProductsTotal() {
        when(inventoryManager.getStock(1L)).thenReturn(50);
        when(inventoryManager.getStock(2L)).thenReturn(200);
        when(inventoryManager.getStock(3L)).thenReturn(150);
        when(em.find(Product.class, 1L)).thenReturn(laptop);
        when(em.find(Product.class, 2L)).thenReturn(mouse);
        when(em.find(Product.class, 3L)).thenReturn(keyboard);

        cart.addItem(1L, 1);
        cart.addItem(2L, 2);
        cart.addItem(3L, 1);

        double expected = 1299.99 + (29.99 * 2) + 89.99;
        assertEquals(expected, cart.getTotal(), 0.01,
                "Total must be correct for multiple products");
        assertEquals(4, cart.getItemCount(),
                "Total item count must be 4");
    }
}