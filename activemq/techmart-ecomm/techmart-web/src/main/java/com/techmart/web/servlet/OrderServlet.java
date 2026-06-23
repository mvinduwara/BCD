package com.techmart.web.servlet;

import com.techmart.ejb.stateful.ShoppingCartBean;
import com.techmart.ejb.stateless.OrderProcessingBean;
import com.techmart.model.Order;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OrderServlet.class.getName());

    @EJB
    private OrderProcessingBean orderProcessingBean;

    @EJB
    private ShoppingCartBean shoppingCartBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleListOrders(req, resp);
        } else if (pathInfo.equals("/checkout")) {
            handleCheckoutPage(req, resp);
        } else {
            handleGetOrder(req, resp, pathInfo);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/place")) {
            handlePlaceOrder(req, resp);
        } else if (pathInfo != null && pathInfo.startsWith("/cancel/")) {
            handleCancelOrder(req, resp, pathInfo);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleListOrders(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start        = System.currentTimeMillis();
        HttpSession session = req.getSession(true);
        String customerId = (String) session.getAttribute("customerId");

        List<Order> orders;
        if (customerId != null) {
            orders = orderProcessingBean.findOrdersByCustomer(customerId);
        } else {
            orders = orderProcessingBean.findAllOrders();
        }

        long elapsed = System.currentTimeMillis() - start;
        req.setAttribute("orders",      orders);
        req.setAttribute("queryTimeMs", elapsed);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void handleCheckoutPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(true);
        String customerId   = (String) session.getAttribute("customerId");

        if (customerId == null) {
            resp.sendRedirect(req.getContextPath() + "/cart/");
            return;
        }

        req.setAttribute("cartItems",  shoppingCartBean.getItems());
        req.setAttribute("cartTotal",  shoppingCartBean.getTotal());
        req.setAttribute("customerId", customerId);

        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    private void handleGetOrder(HttpServletRequest req, HttpServletResponse resp,
                                String pathInfo)
            throws ServletException, IOException {

        try {
            Long orderId  = Long.parseLong(pathInfo.substring(1));
            Order order   = orderProcessingBean.findById(orderId);

            if (order == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found: " + orderId);
                return;
            }

            req.setAttribute("order", order);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
        }
    }

    private void handlePlaceOrder(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        long start          = System.currentTimeMillis();
        HttpSession session = req.getSession(true);
        String customerId   = (String) session.getAttribute("customerId");
        String email        = req.getParameter("email");

        if (customerId == null || email == null || email.isBlank()) {
            session.setAttribute("orderError", "Missing customer information.");
            resp.sendRedirect(req.getContextPath() + "/orders/checkout");
            return;
        }

        try {
            Map<Long, Integer> productQuantities = shoppingCartBean.getProductQuantityMap();

            if (productQuantities.isEmpty()) {
                session.setAttribute("orderError", "Your cart is empty.");
                resp.sendRedirect(req.getContextPath() + "/cart/");
                return;
            }

            Order order     = orderProcessingBean.placeOrder(customerId, email, productQuantities);
            long elapsed    = System.currentTimeMillis() - start;

            shoppingCartBean.checkout();
            session.removeAttribute("cartInitialized");

            logger.info("Order " + order.getId() + " placed in " + elapsed + "ms");
            session.setAttribute("lastOrderId",      order.getId());
            session.setAttribute("lastOrderTotal",   order.getTotalAmount());
            session.setAttribute("orderSuccess",     "Order #" + order.getId() + " placed successfully!");

            resp.sendRedirect(req.getContextPath() + "/orders/");

        } catch (Exception e) {
            logger.severe("Order placement failed: " + e.getMessage());
            session.setAttribute("orderError", "Order failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/orders/checkout");
        }
    }

    private void handleCancelOrder(HttpServletRequest req, HttpServletResponse resp,
                                   String pathInfo)
            throws IOException {

        try {
            Long orderId = Long.parseLong(pathInfo.substring("/cancel/".length()));
            orderProcessingBean.cancelOrder(orderId);

            req.getSession().setAttribute("orderSuccess", "Order #" + orderId + " cancelled.");
            resp.sendRedirect(req.getContextPath() + "/orders/");

        } catch (Exception e) {
            logger.severe("Order cancellation failed: " + e.getMessage());
            req.getSession().setAttribute("orderError", "Cancellation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/orders/");
        }
    }
}