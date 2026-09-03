package com.globaltrade.scm.interceptor;

import com.globaltrade.scm.common.dto.CustomsDocumentDTO;
import com.globaltrade.scm.exception.CustomsComplianceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.interceptor.InvocationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplianceCheckInterceptorTest {

    @Mock
    private InvocationContext context;

    private final ComplianceCheckInterceptor interceptor = new ComplianceCheckInterceptor();

    @Test
    void pastDeadlineThrowsComplianceException() {
        CustomsDocumentDTO expiredDocument = new CustomsDocumentDTO(
                null, 1L, "BILL_OF_LADING", "PENDING", LocalDate.now().minusDays(1), "NL");
        when(context.getParameters()).thenReturn(new Object[]{ expiredDocument });

        assertThrows(CustomsComplianceException.class, () -> interceptor.checkCompliance(context));
    }

    @Test
    void validDocumentProceedsToInvokedMethod() throws Exception {
        CustomsDocumentDTO validDocument = new CustomsDocumentDTO(
                null, 1L, "BILL_OF_LADING", "PENDING", LocalDate.now().plusDays(30), "NL");
        when(context.getParameters()).thenReturn(new Object[]{ validDocument });

        interceptor.checkCompliance(context);

        verify(context).proceed();
    }
}