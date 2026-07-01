package com.techmart.web.servlet;

import com.techmart.ejb.singleton.InventoryManagerBean;
import com.techmart.ejb.stateless.ProductCatalogBean;
import com.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet("/products/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize       = 5 * 1024 * 1024,
        maxRequestSize    = 10 * 1024 * 1024
)
public class ProductServlet extends HttpServlet {

    private static final Logger logger =
            Logger.getLogger(ProductServlet.class.getName());

    private static final String UPLOAD_DIR = "product-images";

    @EJB
    private ProductCatalogBean productCatalogBean;

    @EJB
    private InventoryManagerBean inventoryManagerBean;

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
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
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required");
            return;
        }

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
                                    HttpServletResponse resp,
                                    long start)
            throws ServletException, IOException {

        List<Product> products = productCatalogBean.findAll();
        long elapsed           = System.currentTimeMillis() - start;

        req.setAttribute("products",         products);
        req.setAttribute("queryTimeMs",      elapsed);
        req.setAttribute("inventoryManager", inventoryManagerBean);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void handleByCategory(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  String pathInfo, long start)
            throws ServletException, IOException {

        String category        = pathInfo.substring("/category/".length());
        List<Product> products = productCatalogBean.findByCategory(category);
        long elapsed           = System.currentTimeMillis() - start;

        req.setAttribute("products",         products);
        req.setAttribute("queryTimeMs",      elapsed);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("inventoryManager", inventoryManagerBean);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void handleSearch(HttpServletRequest req,
                              HttpServletResponse resp,
                              long start)
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
            req.setAttribute("product",     product);
            req.setAttribute("stock",       stock);
            req.setAttribute("queryTimeMs", elapsed);

            req.getRequestDispatcher("/products.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID");
        }
    }

    private void handleCreateProduct(HttpServletRequest req,
                                     HttpServletResponse resp)
            throws IOException, ServletException {

        try {
            String name     = req.getParameter("name");
            String desc     = req.getParameter("description");
            double price    = Double.parseDouble(req.getParameter("price"));
            int stock       = Integer.parseInt(
                    req.getParameter("stockQuantity"));
            String category = req.getParameter("category");

            String imageUrl = null;
            Part imagePart  = req.getPart("productImage");

            if (imagePart != null && imagePart.getSize() > 0) {
                imageUrl = saveImage(req, imagePart);
            }

            Product product = new Product(
                    name, desc, price, stock, category);
            product.setImageUrl(imageUrl);
            productCatalogBean.save(product);

            logger.info("Product created: " + name
                    + (imageUrl != null ? " with image" : ""));

            req.getSession().setAttribute("cartSuccess",
                    "Product '" + name + "' added successfully.");
            resp.sendRedirect(req.getContextPath() + "/products/");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product data");
        }
    }

    private String saveImage(HttpServletRequest req, Part part)
            throws IOException {

        String appPath  = req.getServletContext().getRealPath("");
        String uploadPath = appPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String originalName   = Paths.get(
                part.getSubmittedFileName()).getFileName().toString();
        String extension      = "";
        int dotIndex          = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex).toLowerCase();
        }

        if (!extension.matches("\\.(jpg|jpeg|png|webp|gif)")) {
            extension = ".jpg";
        }

        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = uploadPath + File.separator + fileName;

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, Paths.get(filePath),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return req.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        return "ADMIN".equals(session.getAttribute("userRole"));
    }
}