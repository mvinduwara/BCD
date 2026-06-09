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

@WebServlet("/signup")
public class Signup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        ServletContext context = getServletContext();
        ArrayList<User> users = new ArrayList<>();

        if (context.getAttribute("users") == null) {
            context.setAttribute("users", users);
        } else {
            users = (ArrayList<User>) context.getAttribute("users");
        }

        response.setContentType("text/html");

        if (!name.isEmpty() && !mobile.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    response.getWriter().write("<h3>" + email + " is already in use, please choose another one.</h3><a href='signup.jsp'>Go Back</a>");
                    return;
                }
            }

            User user = new User(name, mobile, email, password);
            users.add(user);

            response.getWriter().write("<h3>Account created successfully!</h3><a href='signin.jsp'>Click here to Sign In</a>");
        } else {
            response.getWriter().write("<h3>Please fill out all fields.</h3><a href='signup.jsp'>Go Back</a>");
        }
    }
}