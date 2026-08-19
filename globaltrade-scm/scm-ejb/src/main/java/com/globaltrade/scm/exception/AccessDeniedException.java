package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class AccessDeniedException extends ScmException {
    public AccessDeniedException(String message) {
        super(message);
    }
}