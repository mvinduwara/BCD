package com.globaltrade.scm.common.dto;

public record PersonnelDTO(
        Long id,
        String username,
        String fullName,
        String role,
        String email
) {}