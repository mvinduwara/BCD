package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class CustomsComplianceException extends Exception {
    public CustomsComplianceException(String message) {
        super(message);
    }
}