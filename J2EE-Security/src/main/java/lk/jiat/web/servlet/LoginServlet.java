package lk.jiat.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();

        // Check with DB
        if (username.equals("user") && password.equals("1234")) {

            session.setAttribute("username", username);
            session.setAttribute("role", "USER");

            //request.getRequestDispatcher("home.jsp").forward(request, response);
            response.sendRedirect("home.jsp");

        } else if (username.equals("admin") && password.equals("1234")) {
            session.setAttribute("username", username);
            session.setAttribute("role", "ADMIN");

            //request.getRequestDispatcher("admin/home.jsp").forward(request, response);
            response.sendRedirect("admin/home.jsp");

        } else {
            request.setAttribute("message", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }


    }
}
