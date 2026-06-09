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

        if (name == null || name.isEmpty() || mobile == null || mobile.isEmpty() ||
                email == null || email.isEmpty() || password == null || password.isEmpty()) {

            request.setAttribute("error", "Please fill out all fields.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }

        ServletContext context = getServletContext();
        ArrayList<User> users = (ArrayList<User>) context.getAttribute("users");

        if (users == null) {
            users = new ArrayList<>();
            context.setAttribute("users", users);
        }

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                request.setAttribute("error", "Email is already in use. Please choose another.");
                request.getRequestDispatcher("signup.jsp").forward(request, response);
                return;
            }
        }

        User user = new User(name, mobile, email, password);
        users.add(user);

        response.sendRedirect("signin.jsp?success=true");
    }
}