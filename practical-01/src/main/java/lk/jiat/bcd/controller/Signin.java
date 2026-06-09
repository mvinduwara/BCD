package lk.jiat.bcd.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.bcd.model.User;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/signin")
public class Signin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.getWriter().write("<h3>Please enter both email and password.</h3><a href='signin.jsp'>Go Back</a>");
            return;
        }

        ServletContext context = getServletContext();

        if (context.getAttribute("users") == null) {
            response.getWriter().write("<h3>No accounts found. Please sign up first!</h3><a href='signup.jsp'>Go to Sign Up</a>");
            return;
        }

        ArrayList<User> users = (ArrayList<User>) context.getAttribute("users");
        User matchedUser = null;

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                matchedUser = user;
                break;
            }
        }

        if (matchedUser != null) {
            request.getSession().setAttribute("loggedInUser", matchedUser);
            response.sendRedirect("profile.jsp");
        } else {
            response.getWriter().write("<h3>Invalid email or password. Please try again.</h3><a href='signin.jsp'>Go Back</a>");
        }
    }
}