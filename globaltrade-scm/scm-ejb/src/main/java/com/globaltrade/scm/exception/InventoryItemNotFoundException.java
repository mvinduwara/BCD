package com.globaltrade.scm.exception;

public class InventoryItemNotFoundException extends ScmNotFoundException {
    public InventoryItemNotFoundException(Long id) {
        super("No inventory item found with id " + id);
    }
}