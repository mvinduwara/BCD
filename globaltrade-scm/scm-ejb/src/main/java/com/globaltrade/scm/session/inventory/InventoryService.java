package com.globaltrade.scm.session.inventory;

import com.globaltrade.scm.common.dto.InventoryItemDTO;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface InventoryService {
    List<InventoryItemDTO> findAll();
    InventoryItemDTO findById(Long id) throws InventoryItemNotFoundException;
    InventoryItemDTO updateQuantity(Long id, int quantityOnHand) throws InventoryItemNotFoundException;
    List<InventoryItemDTO> findLowStock();
}