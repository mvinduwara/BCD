package com.globaltrade.scm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 30)
    private String trackingNumber;

    @Column(nullable = false, length = 150)
    private String origin;

    @Column(nullable = false, length = 150)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "estimated_departure")
    private LocalDateTime estimatedDeparture;

    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "carrier_id")
    private Long carrierId;

    public Shipment() {
    }

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public void setEstimatedDeparture(LocalDateTime estimatedDeparture) {
        this.estimatedDeparture = estimatedDeparture;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipment)) return false;
        Shipment shipment = (Shipment) o;
        return id != null && id.equals(shipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}