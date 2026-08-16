package com.globaltrade.scm.interceptor;

import com.globaltrade.scm.common.dto.CustomsDocumentDTO;
import com.globaltrade.scm.exception.CustomsComplianceException;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.time.LocalDate;
import java.util.regex.Pattern;

@ComplianceChecked
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 30)
public class ComplianceCheckInterceptor {

    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    @AroundInvoke
    public Object checkCompliance(InvocationContext context) throws Exception {
        for (Object parameter : context.getParameters()) {
            if (parameter instanceof CustomsDocumentDTO document) {
                if (document.submissionDeadline() == null || document.submissionDeadline().isBefore(LocalDate.now())) {
                    throw new CustomsComplianceException(
                            "Submission deadline " + document.submissionDeadline() + " has already passed");
                }
                if (document.countryCode() == null || !COUNTRY_CODE_PATTERN.matcher(document.countryCode()).matches()) {
                    throw new CustomsComplianceException(
                            "Country code must be a 2-letter ISO code: " + document.countryCode());
                }
            }
        }
        return context.proceed();
    }
}