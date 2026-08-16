package com.globaltrade.scm.exception;

public class PurchaseOrderNotFoundException extends ResourceNotFoundException {
    public PurchaseOrderNotFoundException(Long id) {
        super("No purchase order found with id " + id);
    }
}