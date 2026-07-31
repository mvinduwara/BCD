package lk.jiat.ee.ejb;

import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.ee.interceptor.TimerInterceptor;

@Stateless
public class TimerSessionBean {

    @Schedule(hour = "*", minute = "*", info = "Generating Report")
    @Interceptors({TimerInterceptor.class})
    public void generateReport(){
        System.out.println("TimerSessionBean: Generating Report...");
    }
}
