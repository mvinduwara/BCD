package com.globaltrade.scm.session.purchaseorder;

import com.globaltrade.scm.entity.InventoryItem;
import com.globaltrade.scm.entity.Personnel;
import com.globaltrade.scm.entity.PersonnelRole;
import com.globaltrade.scm.entity.PurchaseOrder;
import com.globaltrade.scm.entity.PurchaseOrderStatus;
import com.globaltrade.scm.entity.Vendor;
import com.globaltrade.scm.exception.AccessDeniedException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ejb.SessionContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private SessionContext sessionContext;

    @InjectMocks
    private PurchaseOrderServiceBean purchaseOrderService;

    @Test
    void confirmDeniedWhenVendorRepDoesNotOwnTheOrder() {
        Vendor orderVendor = new Vendor();
        setId(orderVendor, 1L);

        Vendor callerVendor = new Vendor();
        setId(callerVendor, 2L);

        InventoryItem item = new InventoryItem();
        setId(item, 1L);

        PurchaseOrder order = new PurchaseOrder();
        setId(order, 10L);
        order.setVendor(orderVendor);
        order.setInventoryItem(item);
        order.setQuantity(50);
        order.setOrderDate(LocalDate.now());
        order.setStatus(PurchaseOrderStatus.PLACED);
        when(entityManager.find(PurchaseOrder.class, 10L)).thenReturn(order);

        when(sessionContext.isCallerInRole("VENDOR_REPRESENTATIVE")).thenReturn(true);
        when(sessionContext.isCallerInRole("COORDINATOR")).thenReturn(false);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("vendor1");
        when(sessionContext.getCallerPrincipal()).thenReturn(principal);

        Personnel caller = new Personnel();
        caller.setUsername("vendor1");
        caller.setRole(PersonnelRole.VENDOR_REPRESENTATIVE);
        caller.setVendor(callerVendor);

        TypedQuery<Personnel> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Personnel.class))).thenReturn(query);
        when(query.setParameter(anyString(), eq("vendor1"))).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(caller));

        assertThrows(AccessDeniedException.class, () -> purchaseOrderService.confirm(10L));
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