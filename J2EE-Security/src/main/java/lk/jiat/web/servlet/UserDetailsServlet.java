package lk.jiat.web.servlet;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.web.ejb.remote.UserSessionBean;

import java.io.IOException;

@WebServlet("/user_details")
public class UserDetailsServlet extends HttpServlet {

    @EJB
    private UserSessionBean userSessionBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            userSessionBean.method1();
        }catch (EJBAccessException | AccessLocalException e){
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
