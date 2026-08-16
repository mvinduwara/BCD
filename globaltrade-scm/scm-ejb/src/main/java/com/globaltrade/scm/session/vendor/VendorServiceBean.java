package com.globaltrade.scm.session.vendor;

import com.globaltrade.scm.common.dto.VendorDTO;
import com.globaltrade.scm.entity.Vendor;
import com.globaltrade.scm.entity.VendorStatus;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.exception.VendorValidationException;
import com.globaltrade.scm.interceptor.Audited;
import com.globaltrade.scm.interceptor.PerformanceMonitored;
import com.globaltrade.scm.interceptor.ValidatedVendorData;

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
public class VendorServiceBean implements VendorService {

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"})
    public List<VendorDTO> findAll() {
        TypedQuery<Vendor> query = entityManager.createQuery("SELECT v FROM Vendor v ORDER BY v.id", Vendor.class);
        return query.getResultList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"COORDINATOR", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"})
    public VendorDTO findById(Long id) throws VendorNotFoundException {
        return toDto(fetch(id));
    }

    @Override
    @RolesAllowed("COORDINATOR")
    @ValidatedVendorData
    public VendorDTO create(VendorDTO input) throws VendorValidationException {
        Vendor vendor = new Vendor();
        vendor.setName(input.name());
        vendor.setContactEmail(input.contactEmail());
        vendor.setCountry(input.country());
        vendor.setPerformanceScore(0.0);
        vendor.setStatus(VendorStatus.PENDING_REVIEW);
        entityManager.persist(vendor);
        entityManager.flush();
        return toDto(vendor);
    }

    private Vendor fetch(Long id) throws VendorNotFoundException {
        Vendor vendor = entityManager.find(Vendor.class, id);
        if (vendor == null) {
            throw new VendorNotFoundException(id);
        }
        return vendor;
    }

    private VendorDTO toDto(Vendor vendor) {
        return new VendorDTO(
                vendor.getId(),
                vendor.getName(),
                vendor.getContactEmail(),
                vendor.getCountry(),
                vendor.getPerformanceScore(),
                vendor.getStatus().name()
        );
    }
}