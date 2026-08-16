package com.globaltrade.scm.session.customs;

import com.globaltrade.scm.common.dto.CustomsDocumentDTO;
import com.globaltrade.scm.exception.CustomsComplianceException;
import com.globaltrade.scm.exception.CustomsDocumentNotFoundException;
import com.globaltrade.scm.exception.ShipmentNotFoundException;

import jakarta.ejb.Local;

import java.util.List;

@Local
public interface CustomsService {
    List<CustomsDocumentDTO> findAll();
    CustomsDocumentDTO findById(Long id) throws CustomsDocumentNotFoundException;
    CustomsDocumentDTO create(CustomsDocumentDTO input) throws ShipmentNotFoundException, CustomsComplianceException;
    List<CustomsDocumentDTO> findUpcomingDeadlines(int withinDays);
}