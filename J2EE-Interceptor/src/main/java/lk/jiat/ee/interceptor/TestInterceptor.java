package lk.jiat.ee.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.interceptor.AroundConstruct;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.AroundTimeout;
import jakarta.interceptor.InvocationContext;

public class TestInterceptor {

    @AroundConstruct
    public void aroundConstruct(InvocationContext ic) throws Exception {
        System.out.println("TestInterceptor aroundConstruct");
        ic.proceed();
        System.out.println("Target : "+ ic.getTarget());
    }

    @PostConstruct
    public void init(InvocationContext ic) throws Exception {
        System.out.println("TestInterceptor init ");
        ic.proceed();
    }

    @AroundInvoke
    public Object m(InvocationContext ic) throws Exception {
        System.out.println("TestInterceptor m() start");

        Object[] parameters = ic.getParameters();
        for (Object parameter : parameters) {
            System.out.println("Parameter : " + parameter);
        }


        parameters[0] = "Supun";
        ic.setParameters(parameters);


        Object proceed = ic.proceed();
        System.out.println("TestInterceptor m() end");
        return proceed;
    }

    @PreDestroy
    public void destroy(InvocationContext ic) throws Exception {
        System.out.println("TestInterceptor destroy");
    }

}
