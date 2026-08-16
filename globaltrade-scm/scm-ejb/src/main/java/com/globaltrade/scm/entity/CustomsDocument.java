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

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "customs_document")
public class CustomsDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomsDocumentStatus status = CustomsDocumentStatus.PENDING;

    @Column(name = "submission_deadline", nullable = false)
    private LocalDate submissionDeadline;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    public CustomsDocument() {
    }

    public Long getId() {
        return id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public CustomsDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(CustomsDocumentStatus status) {
        this.status = status;
    }

    public LocalDate getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(LocalDate submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomsDocument)) return false;
        CustomsDocument document = (CustomsDocument) o;
        return id != null && id.equals(document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}