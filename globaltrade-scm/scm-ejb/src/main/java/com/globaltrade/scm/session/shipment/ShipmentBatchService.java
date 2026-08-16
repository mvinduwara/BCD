package com.globaltrade.scm.session.shipment;

import com.globaltrade.scm.common.dto.BatchUpdateResult;
import com.globaltrade.scm.entity.ShipmentStatus;

import jakarta.ejb.Local;

import java.util.Map;

@Local
public interface ShipmentBatchService {
    BatchUpdateResult bulkUpdateStatus(Map<Long, ShipmentStatus> updates);
}