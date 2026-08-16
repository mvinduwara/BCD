package com.globaltrade.scm.session.shipment;

import com.globaltrade.scm.common.dto.BatchUpdateResult;
import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.interceptor.Audited;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
@Audited
@TransactionManagement(TransactionManagementType.BEAN)
public class ShipmentBatchServiceBean implements ShipmentBatchService {

    private static final Logger LOGGER = Logger.getLogger(ShipmentBatchServiceBean.class.getName());

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Resource
    private UserTransaction userTransaction;

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public BatchUpdateResult bulkUpdateStatus(Map<Long, ShipmentStatus> updates) {
        List<Long> succeeded = new ArrayList<>();
        List<BatchUpdateResult.BatchFailure> failed = new ArrayList<>();

        for (Map.Entry<Long, ShipmentStatus> entry : updates.entrySet()) {
            Long shipmentId = entry.getKey();
            ShipmentStatus newStatus = entry.getValue();
            try {
                userTransaction.begin();
                Shipment shipment = entityManager.find(Shipment.class, shipmentId);
                if (shipment == null) {
                    userTransaction.rollback();
                    failed.add(new BatchUpdateResult.BatchFailure(shipmentId, "Shipment not found"));
                    continue;
                }
                shipment.setStatus(newStatus);
                userTransaction.commit();
                succeeded.add(shipmentId);
            } catch (Exception e) {
                LOGGER.warning(() -> "Batch update failed for shipment " + shipmentId + ": " + e.getMessage());
                failed.add(new BatchUpdateResult.BatchFailure(shipmentId, e.getMessage()));
                rollbackQuietly();
            }
        }
        return new BatchUpdateResult(succeeded, failed);
    }

    private void rollbackQuietly() {
        try {
            if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                userTransaction.rollback();
            }
        } catch (Exception e) {
            LOGGER.warning(() -> "Failed to roll back an active user transaction during batch cleanup: " + e.getMessage());
        }
    }
}