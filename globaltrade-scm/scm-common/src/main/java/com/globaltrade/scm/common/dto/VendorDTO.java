package com.globaltrade.scm.common.dto;

public record VendorDTO(
        Long id,
        String name,
        String contactEmail,
        String country,
        Double performanceScore,
        String status
) {}