package lk.jiat.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

@WebServlet("/logout")
public class Logout extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Principal principal = req.getUserPrincipal();
        System.out.println(principal.getName());

        System.out.println(req.getRemoteUser());

        System.out.println(req.isUserInRole("USER"));


        req.logout(); // clear container authentication
        req.getSession().invalidate(); // destroy session with all attributes


        resp.sendRedirect(req.getContextPath() + "/user");

    }
}
