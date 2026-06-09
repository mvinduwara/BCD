package lk.jiat.bcd.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.bcd.model.User;

import java.io.IOException;
import java.util.ArrayList;

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
            users = (ArrayList<User>) context.getAttribute("users");
        } else {
            context.setAttribute("users", users);
        }

        if (!name.isEmpty() && !mobile.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    response.getWriter().write(String.format(email + "is already in use, please choose another one."));
                    return;
                }
            }

            User user = new User(name, mobile, email, password);
            users.add(user);
        } else {

        }
    }
}
