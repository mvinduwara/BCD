package com.globaltrade.scm.exception;

public class ShipmentNotFoundException extends ScmNotFoundException {
    public ShipmentNotFoundException(Long id) {
        super("No shipment found with id " + id);
    }
}