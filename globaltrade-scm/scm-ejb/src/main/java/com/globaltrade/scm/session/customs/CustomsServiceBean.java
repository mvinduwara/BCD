package com.globaltrade.scm.session.customs;

import com.globaltrade.scm.common.dto.CustomsDocumentDTO;
import com.globaltrade.scm.entity.CustomsDocument;
import com.globaltrade.scm.entity.CustomsDocumentStatus;
import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.exception.CustomsComplianceException;
import com.globaltrade.scm.exception.CustomsDocumentNotFoundException;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.interceptor.Audited;
import com.globaltrade.scm.interceptor.ComplianceChecked;
import com.globaltrade.scm.interceptor.PerformanceMonitored;
import com.globaltrade.scm.timer.CustomsDeadlineTimerBean;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Audited
@PerformanceMonitored
public class CustomsServiceBean implements CustomsService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @EJB
    private CustomsDeadlineTimerBean customsDeadlineTimerBean;

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT"})
    public List<CustomsDocumentDTO> findAll() {
        TypedQuery<CustomsDocument> query = entityManager.createQuery(
                "SELECT c FROM CustomsDocument c ORDER BY c.id", CustomsDocument.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT"})
    public CustomsDocumentDTO findById(Long id) throws CustomsDocumentNotFoundException {
        return toDto(fetch(id));
    }

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT"})
    @ComplianceChecked
    public CustomsDocumentDTO create(CustomsDocumentDTO input) throws ShipmentNotFoundException, CustomsComplianceException {
        Shipment shipment = entityManager.find(Shipment.class, input.shipmentId());
        if (shipment == null) {
            throw new ShipmentNotFoundException(input.shipmentId());
        }
        CustomsDocument document = new CustomsDocument();
        document.setShipment(shipment);
        document.setDocumentType(input.documentType());
        document.setSubmissionDeadline(input.submissionDeadline());
        document.setCountryCode(input.countryCode());
        document.setStatus(CustomsDocumentStatus.PENDING);
        entityManager.persist(document);
        entityManager.flush();
        customsDeadlineTimerBean.scheduleDeadlineAlert(document);
        return toDto(document);
    }

    @Override
    @RolesAllowed({"COORDINATOR", "CUSTOMS_AGENT"})
    public List<CustomsDocumentDTO> findUpcomingDeadlines(int withinDays) {
        LocalDate cutoff = LocalDate.now().plusDays(withinDays);
        TypedQuery<CustomsDocument> query = entityManager.createQuery(
                "SELECT c FROM CustomsDocument c WHERE c.submissionDeadline < :cutoff ORDER BY c.submissionDeadline",
                CustomsDocument.class);
        query.setParameter("cutoff", cutoff);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    private CustomsDocument fetch(Long id) throws CustomsDocumentNotFoundException {
        CustomsDocument document = entityManager.find(CustomsDocument.class, id);
        if (document == null) {
            throw new CustomsDocumentNotFoundException(id);
        }
        return document;
    }

    private CustomsDocumentDTO toDto(CustomsDocument document) {
        return new CustomsDocumentDTO(
                document.getId(),
                document.getShipment().getId(),
                document.getDocumentType(),
                document.getStatus().name(),
                document.getSubmissionDeadline(),
                document.getCountryCode()
        );
    }
}