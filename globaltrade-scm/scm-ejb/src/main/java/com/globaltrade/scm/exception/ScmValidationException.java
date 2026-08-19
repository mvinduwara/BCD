package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public abstract class ScmValidationException extends ScmException {
    protected ScmValidationException(String message) {
        super(message);
    }
}