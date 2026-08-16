package com.globaltrade.scm.common.dto;

import java.time.LocalDate;

public record PurchaseOrderDTO(
        Long id,
        Long vendorId,
        Long inventoryItemId,
        Integer quantity,
        String status,
        LocalDate orderDate
) {}