package com.globaltrade.scm.session.shipment;

import com.globaltrade.scm.common.dto.ShipmentDTO;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ShipmentService {
    List<ShipmentDTO> findAll();
    ShipmentDTO findById(Long id) throws ShipmentNotFoundException;
    ShipmentDTO create(ShipmentDTO input) throws VendorNotFoundException;
    ShipmentDTO updateStatus(Long id, ShipmentStatus status) throws ShipmentNotFoundException;
}