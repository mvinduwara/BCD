package com.globaltrade.scm.exception;

public class ShipmentNotFoundException extends ResourceNotFoundException {
    public ShipmentNotFoundException(Long id) {
        super("No shipment found with id " + id);
    }
}