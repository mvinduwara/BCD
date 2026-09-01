package com.globaltrade.scm.interceptor;

import com.globaltrade.scm.common.dto.VendorDTO;
import com.globaltrade.scm.exception.VendorValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.interceptor.InvocationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorDataValidationInterceptorTest {

    @Mock
    private InvocationContext context;

    private final VendorDataValidationInterceptor interceptor = new VendorDataValidationInterceptor();

    @Test
    void invalidEmailThrowsValidationException() {
        VendorDTO invalidVendor = new VendorDTO(null, "Test Vendor", "not-an-email", "SG", 0.0, "PENDING_REVIEW");
        when(context.getParameters()).thenReturn(new Object[]{ invalidVendor });

        assertThrows(VendorValidationException.class, () -> interceptor.validate(context));
    }

    @Test
    void validVendorProceedsToInvokedMethod() throws Exception {
        VendorDTO validVendor = new VendorDTO(null, "Pacific Rim Freight", "contact@pacificrimfreight.com", "SG", 0.0, "PENDING_REVIEW");
        when(context.getParameters()).thenReturn(new Object[]{ validVendor });

        interceptor.validate(context);

        verify(context).proceed();
    }
}