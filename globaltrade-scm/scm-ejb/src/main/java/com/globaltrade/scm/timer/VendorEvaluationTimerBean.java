package com.globaltrade.scm.timer;

import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.entity.Vendor;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class VendorEvaluationTimerBean {

    private static final Logger LOGGER = Logger.getLogger(VendorEvaluationTimerBean.class.getName());

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Schedule(hour = "3", minute = "0", persistent = true, info = "vendor-performance-recalculation")
    public void recalculateVendorScores() {
        TypedQuery<Vendor> vendorQuery = entityManager.createQuery("SELECT v FROM Vendor v", Vendor.class);
        List<Vendor> vendors = vendorQuery.getResultList();
        for (Vendor vendor : vendors) {
            TypedQuery<Shipment> shipmentQuery = entityManager.createQuery(
                    "SELECT s FROM Shipment s WHERE s.vendor = :vendor AND s.status IN :terminalStatuses",
                    Shipment.class);
            shipmentQuery.setParameter("vendor", vendor);
            shipmentQuery.setParameter("terminalStatuses",
                    List.of(ShipmentStatus.DELIVERED, ShipmentStatus.DELAYED, ShipmentStatus.CANCELLED));
            List<Shipment> completedShipments = shipmentQuery.getResultList();
            if (completedShipments.isEmpty()) {
                continue;
            }
            long onTime = completedShipments.stream().filter(s -> s.getStatus() == ShipmentStatus.DELIVERED).count();
            double score = Math.round((double) onTime / completedShipments.size() * 5.0 * 10.0) / 10.0;
            vendor.setPerformanceScore(score);
            LOGGER.info(() -> "Recalculated score for " + vendor.getName() + ": " + vendor.getPerformanceScore());
        }
    }
}