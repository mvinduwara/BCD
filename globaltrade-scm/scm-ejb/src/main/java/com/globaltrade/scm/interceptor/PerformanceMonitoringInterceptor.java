package com.globaltrade.scm.interceptor;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.logging.Logger;

@PerformanceMonitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 20)
public class PerformanceMonitoringInterceptor {

    private static final Logger LOGGER = Logger.getLogger("com.globaltrade.scm.performance");
    private static final long SLOW_THRESHOLD_MS = 200;

    @AroundInvoke
    public Object measureExecution(InvocationContext context) throws Exception {
        long start = System.nanoTime();
        try {
            return context.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            String signature = context.getTarget().getClass().getSimpleName() + "." + context.getMethod().getName();
            if (elapsedMs > SLOW_THRESHOLD_MS) {
                LOGGER.warning(() -> signature + " took " + elapsedMs + "ms — exceeds " + SLOW_THRESHOLD_MS + "ms threshold");
            } else {
                LOGGER.fine(() -> signature + " took " + elapsedMs + "ms");
            }
        }
    }
}