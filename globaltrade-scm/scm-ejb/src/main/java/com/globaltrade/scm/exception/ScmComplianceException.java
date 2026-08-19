package com.globaltrade.scm.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public abstract class ScmComplianceException extends ScmException {
    protected ScmComplianceException(String message) {
        super(message);
    }
}