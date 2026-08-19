package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public abstract class ScmNotFoundException extends ScmException {
    protected ScmNotFoundException(String message) {
        super(message);
    }
}