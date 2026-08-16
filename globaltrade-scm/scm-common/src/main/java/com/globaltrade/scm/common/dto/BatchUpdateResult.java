package com.globaltrade.scm.common.dto;

import java.util.List;

public record BatchUpdateResult(List<Long> succeeded, List<BatchFailure> failed) {
    public record BatchFailure(Long id, String reason) {
    }
}