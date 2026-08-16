package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public abstract class ResourceNotFoundException extends Exception {
    protected ResourceNotFoundException(String message) {
        super(message);
    }
}