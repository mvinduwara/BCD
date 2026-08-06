package lk.jiat.web.ejb.remote;

import jakarta.annotation.security.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
@DeclareRoles({"MANAGER"})
//@RolesAllowed("USER")
@PermitAll
public class UserSessionBean {

    @EJB
    private AdminSessionBean adminSessionBean;

    public void method1(){
        System.out.println("UserSessionBean: method1");
        adminSessionBean.doAction();
    }

    @RolesAllowed({"ADMIN","MANAGER"})
    public void method2(){
        System.out.println("UserSessionBean: method2");
    }

    @DenyAll
    public void method3(){
        System.out.println("UserSessionBean: method3");
    }
}
