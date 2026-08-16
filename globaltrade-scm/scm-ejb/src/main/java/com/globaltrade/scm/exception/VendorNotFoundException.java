package com.globaltrade.scm.exception;

public class VendorNotFoundException extends ResourceNotFoundException {
    public VendorNotFoundException(Long id) {
        super("No vendor found with id " + id);
    }
}