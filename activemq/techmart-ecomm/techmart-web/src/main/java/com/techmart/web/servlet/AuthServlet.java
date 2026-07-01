package com.techmart.web.servlet;

import com.techmart.ejb.stateless.UserBean;
import com.techmart.model.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private static final Logger logger =
            Logger.getLogger(AuthServlet.class.getName());

    @EJB
    private UserBean userBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        switch (pathInfo) {
            case "/login":
                showLoginPage(req, resp);
                return;
            case "/register":
                showRegisterPage(req, resp);
                return;
            case "/logout":
                handleLogout(req, resp);
                return;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(req, resp);
                return;
            case "/register":
                handleRegister(req, resp);
                return;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showLoginPage(HttpServletRequest req,
                               HttpServletResponse resp)
            throws ServletException, IOException {
        if (isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/products/");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    private void showRegisterPage(HttpServletRequest req,
                                  HttpServletResponse resp)
            throws ServletException, IOException {
        if (isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/products/");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    private void handleLogin(HttpServletRequest req,
                             HttpServletResponse resp)
            throws IOException {

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            req.getSession().setAttribute("authError",
                    "Email and password are required.");
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User user = userBean.authenticate(email, password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("customerId",
                    "USER-" + user.getId());
            session.setAttribute("userName",   user.getName());
            session.setAttribute("userEmail",  user.getEmail());
            session.setAttribute("userRole",   user.getRole().name());
            session.removeAttribute("cartInitialized");

            logger.info("User logged in: " + email);

            String redirectTo = (String) session.getAttribute("redirectAfterLogin");
            if (redirectTo != null) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectTo);
            } else {
                resp.sendRedirect(req.getContextPath() + "/products/");
            }
        } else {
            req.getSession().setAttribute("authError",
                    "Invalid email or password. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    private void handleRegister(HttpServletRequest req,
                                HttpServletResponse resp)
            throws IOException {

        String name            = req.getParameter("name");
        String email           = req.getParameter("email");
        String password        = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            req.getSession().setAttribute("authError",
                    "All fields are required.");
            resp.sendRedirect(req.getContextPath() + "/auth/register");
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.getSession().setAttribute("authError",
                    "Passwords do not match.");
            resp.sendRedirect(req.getContextPath() + "/auth/register");
            return;
        }

        if (password.length() < 6) {
            req.getSession().setAttribute("authError",
                    "Password must be at least 6 characters.");
            resp.sendRedirect(req.getContextPath() + "/auth/register");
            return;
        }

        try {
            User user = userBean.register(name, email, password);
            req.getSession().setAttribute("authSuccess",
                    "Account created successfully! Please log in.");
            logger.info("New user registered: " + email);
            resp.sendRedirect(req.getContextPath() + "/auth/login");

        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("authError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/auth/register");
        } catch (Exception e) {
            logger.severe("Registration error: " + e.getMessage());
            req.getSession().setAttribute("authError",
                    "Registration failed. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/auth/register");
        }
    }

    private void handleLogout(HttpServletRequest req,
                              HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String email = (String) session.getAttribute("userEmail");
            session.invalidate();
            logger.info("User logged out: " + email);
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login");
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null
                && session.getAttribute("loggedInUser") != null;
    }
}