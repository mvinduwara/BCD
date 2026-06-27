package com.techmart.web.servlet;

import com.techmart.ejb.stateful.ShoppingCartBean;
import com.techmart.model.CartItem;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CartServlet.class.getName());

    @EJB
    private ShoppingCartBean shoppingCartBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleViewCart(req, resp);
            return;
        } else if (pathInfo.equals("/clear")) {
            handleClearCart(req, resp);
            return;
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleAddItem(req, resp);
            return;
        } else if (pathInfo.equals("/update")) {
            handleUpdateItem(req, resp);
            return;
        } else if (pathInfo.equals("/remove")) {
            handleRemoveItem(req, resp);
            return;
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private void handleViewCart(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        ensureCartInitialized(req);

        List<CartItem> items = shoppingCartBean.getItems();
        double total         = shoppingCartBean.getTotal();
        int itemCount        = shoppingCartBean.getItemCount();
        long elapsed         = System.currentTimeMillis() - start;

        req.setAttribute("cartItems",   items);
        req.setAttribute("cartTotal",   total);
        req.setAttribute("itemCount",   itemCount);
        req.setAttribute("queryTimeMs", elapsed);

        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    private void handleAddItem(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ensureCartInitialized(req);

        try {
            Long productId = Long.parseLong(req.getParameter("productId"));
            int quantity   = Integer.parseInt(req.getParameter("quantity"));

            boolean added = shoppingCartBean.addItem(productId, quantity);

            if (added) {
                logger.info("Added product " + productId + " x" + quantity + " to cart");
                req.getSession().setAttribute("cartSuccess", "Item added to cart successfully.");
            } else {
                req.getSession().setAttribute("cartError", "Insufficient stock for requested quantity.");
            }
            resp.sendRedirect(req.getContextPath() + "/cart/");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product data");
        }
    }

    private void handleUpdateItem(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ensureCartInitialized(req);

        try {
            Long productId  = Long.parseLong(req.getParameter("productId"));
            int newQuantity = Integer.parseInt(req.getParameter("quantity"));

            boolean updated = shoppingCartBean.updateQuantity(productId, newQuantity);

            if (!updated) {
                req.getSession().setAttribute("cartError",
                        "Could not update quantity. Check available stock.");
            }
            resp.sendRedirect(req.getContextPath() + "/cart/");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
        }
    }

    private void handleRemoveItem(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ensureCartInitialized(req);

        try {
            Long productId = Long.parseLong(req.getParameter("productId"));
            shoppingCartBean.removeItem(productId);
            resp.sendRedirect(req.getContextPath() + "/cart/");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        }
    }

    private void handleClearCart(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ensureCartInitialized(req);
        shoppingCartBean.clearCart();
        resp.sendRedirect(req.getContextPath() + "/cart/");
    }

    private void ensureCartInitialized(HttpServletRequest req) {
        HttpSession session   = req.getSession(true);
        String customerId     = (String) session.getAttribute("customerId");

        if (customerId == null) {
            customerId = "GUEST-" + session.getId().substring(0, 8).toUpperCase();
            session.setAttribute("customerId", customerId);
        }

        String cartInitialized = (String) session.getAttribute("cartInitialized");
        if (cartInitialized == null) {
            shoppingCartBean.initCart(customerId);
            session.setAttribute("cartInitialized", "true");
            logger.info("Cart initialized for session: " + customerId);
        }
    }
}