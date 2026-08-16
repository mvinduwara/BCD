package com.globaltrade.scm.timer;

import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class ShipmentStatusTimerBean {

    private static final Logger LOGGER = Logger.getLogger(ShipmentStatusTimerBean.class.getName());

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Schedule(minute = "*/30", hour = "*", persistent = false, info = "shipment-status-sweep")
    public void sweepOverdueShipments() {
        TypedQuery<Shipment> query = entityManager.createQuery(
                "SELECT s FROM Shipment s WHERE s.status = :status AND s.estimatedArrival < :now",
                Shipment.class);
        query.setParameter("status", ShipmentStatus.IN_TRANSIT);
        query.setParameter("now", LocalDateTime.now());
        List<Shipment> overdue = query.getResultList();
        for (Shipment shipment : overdue) {
            shipment.setStatus(ShipmentStatus.DELAYED);
            LOGGER.warning(() -> "Shipment " + shipment.getTrackingNumber() + " marked DELAYED, past ETA");
        }
    }
}