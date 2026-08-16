package com.globaltrade.scm.session.shipment;

import com.globaltrade.scm.common.dto.ShipmentDTO;
import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.entity.Vendor;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.interceptor.Audited;
import com.globaltrade.scm.interceptor.PerformanceMonitored;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Audited
@PerformanceMonitored
public class ShipmentServiceBean implements ShipmentService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"})
    public List<ShipmentDTO> findAll() {
        TypedQuery<Shipment> query = entityManager.createQuery("SELECT s FROM Shipment s ORDER BY s.id", Shipment.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"})
    public ShipmentDTO findById(Long id) throws ShipmentNotFoundException {
        return toDto(fetch(id));
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public ShipmentDTO create(ShipmentDTO input) throws VendorNotFoundException {
        Vendor vendor = entityManager.find(Vendor.class, input.vendorId());
        if (vendor == null) {
            throw new VendorNotFoundException(input.vendorId());
        }
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(input.trackingNumber());
        shipment.setOrigin(input.origin());
        shipment.setDestination(input.destination());
        shipment.setEstimatedDeparture(input.estimatedDeparture());
        shipment.setEstimatedArrival(input.estimatedArrival());
        shipment.setCarrierId(input.carrierId());
        shipment.setVendor(vendor);
        shipment.setStatus(ShipmentStatus.PENDING);
        entityManager.persist(shipment);
        entityManager.flush();
        return toDto(shipment);
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER"})
    public ShipmentDTO updateStatus(Long id, ShipmentStatus status) throws ShipmentNotFoundException {
        Shipment shipment = fetch(id);
        shipment.setStatus(status);
        return toDto(shipment);
    }

    private Shipment fetch(Long id) throws ShipmentNotFoundException {
        Shipment shipment = entityManager.find(Shipment.class, id);
        if (shipment == null) {
            throw new ShipmentNotFoundException(id);
        }
        return shipment;
    }

    private ShipmentDTO toDto(Shipment shipment) {
        return new ShipmentDTO(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getStatus().name(),
                shipment.getEstimatedDeparture(),
                shipment.getEstimatedArrival(),
                shipment.getVendor().getId(),
                shipment.getCarrierId()
        );
    }
}