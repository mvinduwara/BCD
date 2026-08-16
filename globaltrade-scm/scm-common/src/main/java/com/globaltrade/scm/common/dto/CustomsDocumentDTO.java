package com.globaltrade.scm.common.dto;

import java.time.LocalDate;

public record CustomsDocumentDTO(
        Long id,
        Long shipmentId,
        String documentType,
        String status,
        LocalDate submissionDeadline,
        String countryCode
) {}