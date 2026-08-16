package com.globaltrade.scm.common.dto;

import java.time.LocalDateTime;

public record ShipmentDTO(
        Long id,
        String trackingNumber,
        String origin,
        String destination,
        String status,
        LocalDateTime estimatedDeparture,
        LocalDateTime estimatedArrival,
        Long vendorId,
        Long carrierId
) {}