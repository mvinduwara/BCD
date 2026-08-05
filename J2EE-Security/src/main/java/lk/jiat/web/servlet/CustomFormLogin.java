package lk.jiat.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/custom_auth")
public class CustomFormLogin extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // DB auth check
        // Check user type


       // request.getSession().setAttribute("username", username);

        request.login("user", "1234");
        response.sendRedirect(request.getContextPath() + "/user");

    }
}
