package com.globaltrade.scm.session.inventory;

import com.globaltrade.scm.common.dto.InventoryItemDTO;
import com.globaltrade.scm.entity.InventoryItem;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private InventoryServiceBean inventoryService;

    @Test
    void updateQuantityChangesManagedEntityWithoutExplicitPersist() throws InventoryItemNotFoundException {
        InventoryItem item = new InventoryItem();
        setId(item, 1L);
        item.setSku("SKU-TEST");
        item.setDescription("Test item");
        item.setQuantityOnHand(50);
        item.setReorderThreshold(100);
        item.setWarehouseLocation("WH-TEST");
        when(entityManager.find(InventoryItem.class, 1L)).thenReturn(item);

        inventoryService.updateQuantity(1L, 75);

        assertEquals(75, item.getQuantityOnHand());
    }

    @Test
    void updateQuantityThrowsWhenItemMissing() {
        when(entityManager.find(InventoryItem.class, 999L)).thenReturn(null);

        assertThrows(InventoryItemNotFoundException.class, () -> inventoryService.updateQuantity(999L, 10));
    }

    @Test
    void findLowStockReturnsOnlyItemsBelowThreshold() {
        InventoryItem belowThreshold = new InventoryItem();
        setId(belowThreshold, 1L);
        belowThreshold.setSku("SKU-LOW");
        belowThreshold.setDescription("Low stock item");
        belowThreshold.setQuantityOnHand(10);
        belowThreshold.setReorderThreshold(50);
        belowThreshold.setWarehouseLocation("WH-A");

        TypedQuery<InventoryItem> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(InventoryItem.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(belowThreshold));

        List<InventoryItemDTO> result = inventoryService.findLowStock();

        assertEquals(1, result.size());
        assertEquals("SKU-LOW", result.get(0).sku());
    }

    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}