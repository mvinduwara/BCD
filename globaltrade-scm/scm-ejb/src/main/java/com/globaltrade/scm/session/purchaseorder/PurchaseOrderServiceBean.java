package com.globaltrade.scm.session.purchaseorder;

import com.globaltrade.scm.common.dto.PurchaseOrderDTO;
import com.globaltrade.scm.entity.InventoryItem;
import com.globaltrade.scm.entity.Personnel;
import com.globaltrade.scm.entity.PurchaseOrder;
import com.globaltrade.scm.entity.PurchaseOrderStatus;
import com.globaltrade.scm.entity.Vendor;
import com.globaltrade.scm.exception.AccessDeniedException;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;
import com.globaltrade.scm.exception.PurchaseOrderNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.interceptor.Audited;
import com.globaltrade.scm.interceptor.PerformanceMonitored;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Audited
@PerformanceMonitored
public class PurchaseOrderServiceBean implements PurchaseOrderService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Resource
    private SessionContext sessionContext;

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"})
    public List<PurchaseOrderDTO> findAll() {
        TypedQuery<PurchaseOrder> query = entityManager.createQuery("SELECT p FROM PurchaseOrder p ORDER BY p.id", PurchaseOrder.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public PurchaseOrderDTO place(Long vendorId, Long inventoryItemId, int quantity) throws VendorNotFoundException, InventoryItemNotFoundException {
        Vendor vendor = entityManager.find(Vendor.class, vendorId);
        if (vendor == null) {
            throw new VendorNotFoundException(vendorId);
        }
        InventoryItem item = entityManager.find(InventoryItem.class, inventoryItemId);
        if (item == null) {
            throw new InventoryItemNotFoundException(inventoryItemId);
        }
        PurchaseOrder order = new PurchaseOrder();
        order.setVendor(vendor);
        order.setInventoryItem(item);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDate.now());
        order.setStatus(PurchaseOrderStatus.PLACED);
        entityManager.persist(order);
        entityManager.flush();
        return toDto(order);
    }

    @Override
    @RolesAllowed({"COORDINATOR", "VENDOR_REPRESENTATIVE"})
    public PurchaseOrderDTO confirm(Long orderId) throws PurchaseOrderNotFoundException, AccessDeniedException {
        PurchaseOrder order = fetch(orderId);
        if (sessionContext.isCallerInRole("VENDOR_REPRESENTATIVE") && !sessionContext.isCallerInRole("COORDINATOR")) {
            String callerUsername = sessionContext.getCallerPrincipal().getName();
            Personnel caller = findPersonnelByUsername(callerUsername);
            if (caller == null || caller.getVendor() == null || !caller.getVendor().getId().equals(order.getVendor().getId())) {
                throw new AccessDeniedException("You can only confirm purchase orders for your own vendor");
            }
        }
        order.setStatus(PurchaseOrderStatus.CONFIRMED);
        return toDto(order);
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public PurchaseOrderDTO fulfill(Long orderId) throws PurchaseOrderNotFoundException {
        PurchaseOrder order = fetch(orderId);
        InventoryItem item = order.getInventoryItem();
        item.setQuantityOnHand(item.getQuantityOnHand() + order.getQuantity());
        order.setStatus(PurchaseOrderStatus.FULFILLED);
        return toDto(order);
    }

    private Personnel findPersonnelByUsername(String username) {
        List<Personnel> matches = entityManager.createQuery("SELECT p FROM Personnel p WHERE p.username = :username", Personnel.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList();
        return matches.isEmpty() ? null : matches.get(0);
    }

    private PurchaseOrder fetch(Long orderId) throws PurchaseOrderNotFoundException {
        PurchaseOrder order = entityManager.find(PurchaseOrder.class, orderId);
        if (order == null) {
            throw new PurchaseOrderNotFoundException(orderId);
        }
        return order;
    }

    private PurchaseOrderDTO toDto(PurchaseOrder order) {
        return new PurchaseOrderDTO(
                order.getId(),
                order.getVendor().getId(),
                order.getInventoryItem().getId(),
                order.getQuantity(),
                order.getStatus().name(),
                order.getOrderDate()
        );
    }
}