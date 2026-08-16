package com.globaltrade.scm.session.inventory;

import com.globaltrade.scm.common.dto.InventoryItemDTO;
import com.globaltrade.scm.entity.InventoryItem;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;
import com.globaltrade.scm.interceptor.Audited;
import com.globaltrade.scm.interceptor.PerformanceMonitored;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Audited
@PerformanceMonitored
public class InventoryServiceBean implements InventoryService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public List<InventoryItemDTO> findAll() {
        TypedQuery<InventoryItem> query = entityManager.createQuery("SELECT i FROM InventoryItem i ORDER BY i.id", InventoryItem.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public InventoryItemDTO findById(Long id) throws InventoryItemNotFoundException {
        return toDto(fetch(id));
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public InventoryItemDTO updateQuantity(Long id, int quantityOnHand) throws InventoryItemNotFoundException {
        InventoryItem item = fetch(id);
        item.setQuantityOnHand(quantityOnHand);
        return toDto(item);
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public List<InventoryItemDTO> findLowStock() {
        TypedQuery<InventoryItem> query = entityManager.createQuery(
                "SELECT i FROM InventoryItem i WHERE i.quantityOnHand < i.reorderThreshold ORDER BY i.id",
                InventoryItem.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    private InventoryItem fetch(Long id) throws InventoryItemNotFoundException {
        InventoryItem item = entityManager.find(InventoryItem.class, id);
        if (item == null) {
            throw new InventoryItemNotFoundException(id);
        }
        return item;
    }

    private InventoryItemDTO toDto(InventoryItem item) {
        return new InventoryItemDTO(
                item.getId(),
                item.getSku(),
                item.getDescription(),
                item.getQuantityOnHand(),
                item.getReorderThreshold(),
                item.getWarehouseLocation()
        );
    }
}