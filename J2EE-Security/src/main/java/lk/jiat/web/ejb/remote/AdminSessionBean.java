package lk.jiat.web.ejb.remote;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;

@Stateless
@RolesAllowed("ADMIN")
public class AdminSessionBean {


    public void doAction(){
        System.out.println("UserSessionBean: doAction");
    }
}
