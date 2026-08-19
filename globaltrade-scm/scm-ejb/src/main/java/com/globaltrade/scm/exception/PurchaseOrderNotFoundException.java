package com.globaltrade.scm.exception;

public class PurchaseOrderNotFoundException extends ScmNotFoundException {
    public PurchaseOrderNotFoundException(Long id) {
        super("No purchase order found with id " + id);
    }
}