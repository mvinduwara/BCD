package lk.jiat.ee.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.interceptor.AroundConstruct;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class AInterceptor {

    @AroundConstruct
    public void aroundConstruct(InvocationContext ic) throws Exception {
        System.out.println("AInterceptor aroundConstruct");
        ic.proceed();
    }

    @PostConstruct
    public void init(InvocationContext ic) throws Exception {
        System.out.println("AInterceptor init");
        ic.proceed();
    }

    @AroundInvoke
    public Object m(InvocationContext ic) throws Exception {
        System.out.println("AInterceptor m() start");
        Object proceed = ic.proceed();
        System.out.println("AInterceptor m() end");
        return proceed;
    }
}
