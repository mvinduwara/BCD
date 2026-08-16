package com.globaltrade.scm.common.dto;

public record InventoryItemDTO(
        Long id,
        String sku,
        String description,
        Integer quantityOnHand,
        Integer reorderThreshold,
        String warehouseLocation
) {}