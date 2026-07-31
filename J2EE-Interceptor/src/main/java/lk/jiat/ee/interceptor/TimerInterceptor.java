package lk.jiat.ee.interceptor;

import jakarta.ejb.Timer;
import jakarta.interceptor.AroundTimeout;
import jakarta.interceptor.InvocationContext;


public class TimerInterceptor {

    @AroundTimeout
    public Object aroundTimeout(InvocationContext ic) throws Exception {
        System.out.println("TimerInterceptor aroundTimeout start");

        Timer timer= (Timer) ic.getTimer();
        System.out.println("Timer info: " + timer.getInfo());
        System.out.println("Timer Timeout: " + timer.getNextTimeout());


        ///return ic.proceed();
        return ic.proceed();
    }
}
