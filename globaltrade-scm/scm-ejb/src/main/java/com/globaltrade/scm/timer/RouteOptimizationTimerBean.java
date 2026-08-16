package com.globaltrade.scm.timer;

import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
@Startup
public class RouteOptimizationTimerBean {

    private static final Logger LOGGER = Logger.getLogger(RouteOptimizationTimerBean.class.getName());

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Schedule(hour = "*/6", minute = "15", persistent = true, info = "route-consolidation-scan")
    public void scanForConsolidationOpportunities() {
        TypedQuery<Shipment> query = entityManager.createQuery(
                "SELECT s FROM Shipment s WHERE s.status IN :activeStatuses",
                Shipment.class);
        query.setParameter("activeStatuses", List.of(ShipmentStatus.PENDING, ShipmentStatus.IN_TRANSIT));
        List<Shipment> active = query.getResultList();
        Map<String, List<Shipment>> byRoute = active.stream()
                .collect(Collectors.groupingBy(s -> s.getOrigin() + " -> " + s.getDestination()));
        byRoute.forEach((route, shipments) -> {
            if (shipments.size() > 1) {
                LOGGER.info(() -> shipments.size() + " active shipments on route " + route + " — consolidation candidate");
            }
        });
    }
}