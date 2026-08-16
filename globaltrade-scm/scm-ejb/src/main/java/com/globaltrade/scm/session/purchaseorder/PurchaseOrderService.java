package com.globaltrade.scm.session.purchaseorder;

import com.globaltrade.scm.common.dto.PurchaseOrderDTO;
import com.globaltrade.scm.exception.AccessDeniedException;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;
import com.globaltrade.scm.exception.PurchaseOrderNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface PurchaseOrderService {
    List<PurchaseOrderDTO> findAll();
    PurchaseOrderDTO place(Long vendorId, Long inventoryItemId, int quantity) throws VendorNotFoundException, InventoryItemNotFoundException;
    PurchaseOrderDTO confirm(Long orderId) throws PurchaseOrderNotFoundException, AccessDeniedException;
    PurchaseOrderDTO fulfill(Long orderId) throws PurchaseOrderNotFoundException;
}