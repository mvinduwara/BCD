package lk.jiat.ee.interceptor;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lk.jiat.ee.annotation.Logged;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 2)
public class DBLoggingInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        System.out.println("DBLoggingInterceptor.intercept logging start");
        Object result = ctx.proceed();
        System.out.println("DBLoggingInterceptor.intercept logging end");
        return result;
    }
}
