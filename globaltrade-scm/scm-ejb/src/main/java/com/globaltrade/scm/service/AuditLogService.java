package com.globaltrade.scm.service;

import com.globaltrade.scm.entity.AuditLogEntry;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;

@Stateless
public class AuditLogService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void record(String component, String methodName, String outcome, String detail) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setComponent(component);
        entry.setMethodName(methodName);
        entry.setOutcome(outcome);
        entry.setDetail(detail);
        entry.setRecordedAt(LocalDateTime.now());
        entityManager.persist(entry);
    }
}