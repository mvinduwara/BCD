package com.globaltrade.scm.session.inventory;

import com.globaltrade.scm.entity.InventoryItem;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
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