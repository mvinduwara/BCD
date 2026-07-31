package lk.jiat.ee.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.Interceptors;
import jakarta.interceptor.InvocationContext;
import lk.jiat.ee.annotation.Logged;
import lk.jiat.ee.interceptor.AInterceptor;
import lk.jiat.ee.interceptor.TestInterceptor;

@Logged
@Stateless
//@Interceptors({TestInterceptor.class, AInterceptor.class})
public class UserSessionBean {

//    @PostConstruct
//    public void init(){
//        System.out.println("UserSessionBean init");
//    }

    public String doAction(String name, int age) {
        System.out.println("UserSessionBean doAction start");
        System.out.println("UserSessionBean doAction -> " + name + " : " + age);
        System.out.println("UserSessionBean doAction end");

        return "Success";
    }
}
