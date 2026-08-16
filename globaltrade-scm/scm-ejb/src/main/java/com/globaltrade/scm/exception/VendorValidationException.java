package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class VendorValidationException extends Exception {
    public VendorValidationException(String message) {
        super(message);
    }
}