package com.globaltrade.scm.common.dto;

public record PlacePurchaseOrderRequest(Long vendorId, Long inventoryItemId, Integer quantity) {}