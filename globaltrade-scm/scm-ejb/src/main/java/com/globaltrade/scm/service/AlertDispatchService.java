package com.globaltrade.scm.service;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.util.logging.Logger;

@Stateless
public class AlertDispatchService {

    private static final Logger LOGGER = Logger.getLogger(AlertDispatchService.class.getName());

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void dispatchCustomsDeadlineAlert(Long documentId) {
        LOGGER.warning(() -> "ESCALATION: customs document " + documentId + " approaching submission deadline");
    }
}