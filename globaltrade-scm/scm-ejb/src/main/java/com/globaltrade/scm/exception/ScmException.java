package com.globaltrade.scm.exception;

public abstract class ScmException extends Exception {
    protected ScmException(String message) {
        super(message);
    }
}