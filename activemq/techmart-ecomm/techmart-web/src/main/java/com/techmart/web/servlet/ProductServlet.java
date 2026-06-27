package com.techmart.web.servlet;

import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.ejb.stateless.ProductCatalogBean;
import com.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ProductServlet.class.getName());

    @EJB
    private ProductCatalogBean productCatalogBean;

    @EJB
    private InventoryManagerBean inventoryManagerBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start    = System.currentTimeMillis();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleListProducts(req, resp, start);
            return;
        } else if (pathInfo.startsWith("/category/")) {
            handleByCategory(req, resp, pathInfo, start);
            return;
        } else if (pathInfo.startsWith("/search")) {
            handleSearch(req, resp, start);
            return;
        } else {
            handleGetProduct(req, resp, pathInfo, start);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleCreateProduct(req, resp);
            return;
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private void handleListProducts(HttpServletRequest req,
                                    HttpServletResponse resp, long start)
            throws ServletException, IOException {

        List<Product> products = productCatalogBean.findAll();
        long elapsed           = System.currentTimeMillis() - start;

        req.setAttribute("products",         products);
        req.setAttribute("queryTimeMs",      elapsed);
        req.setAttribute("inventoryManager", inventoryManagerBean);

        logger.info("Listed " + products.size() + " products in " + elapsed + "ms");
        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void handleByCategory(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  String pathInfo, long start)
            throws ServletException, IOException {

        String category        = pathInfo.substring("/category/".length());
        List<Product> products = productCatalogBean.findByCategory(category);
        long elapsed           = System.currentTimeMillis() - start;

        req.setAttribute("products",          products);
        req.setAttribute("queryTimeMs",       elapsed);
        req.setAttribute("selectedCategory",  category);
        req.setAttribute("inventoryManager",  inventoryManagerBean);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void handleSearch(HttpServletRequest req,
                              HttpServletResponse resp, long start)
            throws ServletException, IOException {

        String keyword = req.getParameter("q");
        if (keyword == null || keyword.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/products/");
            return;
        }

        List<Product> products = productCatalogBean.searchByName(keyword);
        long elapsed           = System.currentTimeMillis() - start;

        req.setAttribute("products",         products);
        req.setAttribute("queryTimeMs",      elapsed);
        req.setAttribute("searchKeyword",    keyword);
        req.setAttribute("inventoryManager", inventoryManagerBean);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void handleGetProduct(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  String pathInfo, long start)
            throws ServletException, IOException {

        try {
            Long productId  = Long.parseLong(pathInfo.substring(1));
            Product product = productCatalogBean.findById(productId);
            long elapsed    = System.currentTimeMillis() - start;

            if (product == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Product not found: " + productId);
                return;
            }

            int stock = inventoryManagerBean.getStock(productId);
            req.setAttribute("product",      product);
            req.setAttribute("stock",        stock);
            req.setAttribute("queryTimeMs",  elapsed);

            req.getRequestDispatcher("/products.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        }
    }

    private void handleCreateProduct(HttpServletRequest req,
                                     HttpServletResponse resp)
            throws IOException {

        try {
            String name    = req.getParameter("name");
            String desc    = req.getParameter("description");
            double price   = Double.parseDouble(req.getParameter("price"));
            int stock      = Integer.parseInt(req.getParameter("stockQuantity"));
            String category = req.getParameter("category");

            Product product = new Product(name, desc, price, stock, category);
            productCatalogBean.save(product);

            logger.info("Product created: " + name);
            resp.sendRedirect(req.getContextPath() + "/products/");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product data");
        }
    }
}