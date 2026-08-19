package com.globaltrade.scm.exception;

public class CustomsDocumentNotFoundException extends ScmNotFoundException {
    public CustomsDocumentNotFoundException(Long id) {
        super("No customs document found with id " + id);
    }
}