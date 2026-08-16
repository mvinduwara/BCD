package com.globaltrade.scm.timer;

import com.globaltrade.scm.entity.InventoryItem;

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
public class InventoryMonitorTimerBean {

    private static final Logger LOGGER = Logger.getLogger(InventoryMonitorTimerBean.class.getName());

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Schedule(hour = "*", minute = "0", persistent = true, info = "inventory-low-stock-check")
    public void checkLowStock() {
        TypedQuery<InventoryItem> query = entityManager.createQuery(
                "SELECT i FROM InventoryItem i WHERE i.quantityOnHand < i.reorderThreshold",
                InventoryItem.class);
        List<InventoryItem> lowStock = query.getResultList();
        for (InventoryItem item : lowStock) {
            LOGGER.warning(() -> "Low stock: " + item.getSku() + " at " + item.getQuantityOnHand()
                    + " units, below threshold " + item.getReorderThreshold());
        }
    }
}