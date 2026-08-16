package com.globaltrade.scm.session.vendor;

import com.globaltrade.scm.common.dto.VendorDTO;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.exception.VendorValidationException;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface VendorService {
    List<VendorDTO> findAll();
    VendorDTO findById(Long id) throws VendorNotFoundException;
    VendorDTO create(VendorDTO input) throws VendorValidationException;
}