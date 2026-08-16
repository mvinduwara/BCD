package com.globaltrade.scm.interceptor;

import com.globaltrade.scm.common.dto.VendorDTO;
import com.globaltrade.scm.exception.VendorValidationException;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.regex.Pattern;

@ValidatedVendorData
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 30)
public class VendorDataValidationInterceptor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    @AroundInvoke
    public Object validate(InvocationContext context) throws Exception {
        for (Object parameter : context.getParameters()) {
            if (parameter instanceof VendorDTO vendor) {
                if (vendor.name() == null || vendor.name().isBlank()) {
                    throw new VendorValidationException("Vendor name is required");
                }
                if (vendor.contactEmail() == null || !EMAIL_PATTERN.matcher(vendor.contactEmail()).matches()) {
                    throw new VendorValidationException("Vendor contact email is invalid: " + vendor.contactEmail());
                }
                if (vendor.country() == null || !COUNTRY_CODE_PATTERN.matcher(vendor.country()).matches()) {
                    throw new VendorValidationException("Vendor country must be a 2-letter ISO code: " + vendor.country());
                }
            }
        }
        return context.proceed();
    }
}