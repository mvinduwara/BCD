package com.globaltrade.scm.interceptor;

import com.globaltrade.scm.service.AuditLogService;

import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Audited
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 10)
public class AuditLoggingInterceptor {

    @EJB
    private AuditLogService auditLogService;

    @AroundInvoke
    public Object logInvocation(InvocationContext context) throws Exception {
        String component = context.getTarget().getClass().getSimpleName();
        String methodName = context.getMethod().getName();
        try {
            Object result = context.proceed();
            auditLogService.record(component, methodName, "SUCCESS", null);
            return result;
        } catch (Exception e) {
            auditLogService.record(component, methodName, "FAILURE", e.getClass().getSimpleName() + ": " + e.getMessage());
            throw e;
        }
    }
}