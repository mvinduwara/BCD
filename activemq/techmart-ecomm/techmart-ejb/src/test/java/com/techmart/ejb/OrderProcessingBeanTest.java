package com.techmart.ejb;

import com.techmart.ejb.async.NotificationService;
import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.ejb.stateless.OrderProcessingBean;
import com.techmart.model.Order;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderProcessingBean Tests - Stateless EJB")
class OrderProcessingBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private InventoryManagerBean inventoryManager;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TypedQuery<Order> orderQuery;

    private OrderProcessingBean orderBean;

    private Product laptop;
    private Product mouse;

    @BeforeEach
    void setUp() throws Exception {
        orderBean = new OrderProcessingBean();

        injectField("em",                  em);
        injectField("inventoryManager",    inventoryManager);
        injectField("notificationService", notificationService);

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
    }

    private void injectField(String name, Object value)
            throws Exception {
        Field f = OrderProcessingBean.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(orderBean, value);
    }

    @Test
    @DisplayName("TC01 - findById() returns order when found")
    void testFindByIdFound() {
        Order order = new Order("CUST-001", "test@test.com");
        order.setId(1L);
        when(em.find(Order.class, 1L)).thenReturn(order);

        Order result = orderBean.findById(1L);

        assertNotNull(result, "Order must not be null when found");
        assertEquals(1L, result.getId(),
                "Order ID must match");
        assertEquals("CUST-001", result.getCustomerId(),
                "Customer ID must match");
    }

    @Test
    @DisplayName("TC02 - findById() returns null when not found")
    void testFindByIdNotFound() {
        when(em.find(Order.class, 999L)).thenReturn(null);

        Order result = orderBean.findById(999L);

        assertNull(result,
                "findById() must return null for missing order");
    }

    @Test
    @DisplayName("TC03 - findOrdersByCustomer() returns customer orders")
    void testFindOrdersByCustomer() {
        List<Order> mockOrders = Arrays.asList(
                new Order("CUST-001", "a@a.com"),
                new Order("CUST-001", "a@a.com")
        );
        when(em.createQuery(anyString(), eq(Order.class)))
                .thenReturn(orderQuery);
        when(orderQuery.setParameter(anyString(), any()))
                .thenReturn(orderQuery);
        when(orderQuery.getResultList()).thenReturn(mockOrders);

        List<Order> result =
                orderBean.findOrdersByCustomer("CUST-001");

        assertNotNull(result, "Result must not be null");
        assertEquals(2, result.size(),
                "Must return 2 orders for customer");
    }

    @Test
    @DisplayName("TC04 - findOrdersByCustomer() returns empty for new customer")
    void testFindOrdersByCustomerEmpty() {
        when(em.createQuery(anyString(), eq(Order.class)))
                .thenReturn(orderQuery);
        when(orderQuery.setParameter(anyString(), any()))
                .thenReturn(orderQuery);
        when(orderQuery.getResultList()).thenReturn(List.of());

        List<Order> result =
                orderBean.findOrdersByCustomer("NEW-CUSTOMER");

        assertNotNull(result, "Result must not be null");
        assertTrue(result.isEmpty(),
                "New customer must have no orders");
    }

    @Test
    @DisplayName("TC05 - findAllOrders() returns all orders in system")
    void testFindAllOrders() {
        List<Order> mockOrders = Arrays.asList(
                new Order("CUST-001", "a@a.com"),
                new Order("CUST-002", "b@b.com"),
                new Order("CUST-003", "c@c.com")
        );
        when(em.createQuery(anyString(), eq(Order.class)))
                .thenReturn(orderQuery);
        when(orderQuery.getResultList()).thenReturn(mockOrders);

        List<Order> result = orderBean.findAllOrders();

        assertEquals(3, result.size(),
                "findAllOrders() must return all 3 orders");
    }

    @Test
    @DisplayName("TC06 - cancelOrder() throws when order not found")
    void testCancelOrderNotFound() {
        when(em.find(Order.class, 999L)).thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> orderBean.cancelOrder(999L),
                "Must throw RuntimeException for missing order");

        assertTrue(ex.getMessage().contains("Order not found"),
                "Exception must mention order not found");
    }

    @Test
    @DisplayName("TC07 - cancelOrder() throws for SHIPPED order")
    void testCancelShippedOrder() {
        Order shipped = new Order("CUST-001", "a@a.com");
        shipped.setId(1L);
        shipped.setStatus(Order.Status.SHIPPED);
        when(em.find(Order.class, 1L)).thenReturn(shipped);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> orderBean.cancelOrder(1L),
                "Must throw for SHIPPED order");

        assertTrue(ex.getMessage().contains("Cannot cancel"),
                "Exception must mention cannot cancel");
    }

    @Test
    @DisplayName("TC08 - cancelOrder() throws for DELIVERED order")
    void testCancelDeliveredOrder() {
        Order delivered = new Order("CUST-001", "a@a.com");
        delivered.setId(1L);
        delivered.setStatus(Order.Status.DELIVERED);
        when(em.find(Order.class, 1L)).thenReturn(delivered);

        assertThrows(RuntimeException.class,
                () -> orderBean.cancelOrder(1L),
                "Must throw for DELIVERED order");
    }

    @Test
    @DisplayName("TC09 - placeOrder() throws when stock reservation fails")
    void testPlaceOrderStockReservationFails() {
        when(inventoryManager.reserveStock(1L, 5))
                .thenReturn(false);

        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 5);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> orderBean.placeOrder(
                        "CUST-001", "test@test.com", items),
                "Must throw when stock reservation fails");

        assertTrue(
                ex.getMessage().contains("Insufficient stock"),
                "Exception must mention insufficient stock");
    }

    @Test
    @DisplayName("TC10 - New Order defaults to PENDING status")
    void testNewOrderDefaultStatus() {
        Order order = new Order("CUST-001", "test@test.com");

        assertEquals(Order.Status.PENDING, order.getStatus(),
                "New order must default to PENDING");
        assertNotNull(order.getCreatedAt(),
                "New order must have a creation timestamp");
    }

    @Test
    @DisplayName("TC11 - Order stores customer details correctly")
    void testOrderCustomerDetails() {
        Order order = new Order("CUST-001", "customer@email.com");

        assertEquals("CUST-001", order.getCustomerId());
        assertEquals("customer@email.com", order.getCustomerEmail());
    }

    @Test
    @DisplayName("TC12 - Order total amount is set correctly")
    void testOrderTotalAmount() {
        Order order = new Order("CUST-001", "test@test.com");
        order.setTotalAmount(2689.96);

        assertEquals(2689.96, order.getTotalAmount(), 0.01,
                "Order total must match set value");
    }

    @Test
    @DisplayName("TC13 - Order status transitions work correctly")
    void testOrderStatusTransitions() {
        Order order = new Order("CUST-001", "test@test.com");

        assertEquals(Order.Status.PENDING, order.getStatus());
        order.setStatus(Order.Status.CONFIRMED);
        assertEquals(Order.Status.CONFIRMED, order.getStatus());
        order.setStatus(Order.Status.PROCESSING);
        assertEquals(Order.Status.PROCESSING, order.getStatus());
        order.setStatus(Order.Status.SHIPPED);
        assertEquals(Order.Status.SHIPPED, order.getStatus());
        order.setStatus(Order.Status.DELIVERED);
        assertEquals(Order.Status.DELIVERED, order.getStatus());
    }

    @Test
    @DisplayName("TC14 - All 6 Order status values exist")
    void testOrderStatusValues() {
        List<Order.Status> statuses =
                Arrays.asList(Order.Status.values());

        assertTrue(statuses.contains(Order.Status.PENDING));
        assertTrue(statuses.contains(Order.Status.CONFIRMED));
        assertTrue(statuses.contains(Order.Status.PROCESSING));
        assertTrue(statuses.contains(Order.Status.SHIPPED));
        assertTrue(statuses.contains(Order.Status.DELIVERED));
        assertTrue(statuses.contains(Order.Status.CANCELLED));
        assertEquals(6, statuses.size(),
                "Order must have exactly 6 status values");
    }
}