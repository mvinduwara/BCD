<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.Product" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Products - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">TechMart Online</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products/">Products</a>
        <a href="${pageContext.request.contextPath}/cart/">Cart</a>
        <a href="${pageContext.request.contextPath}/orders/">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
        <c:choose>
            <c:when test="${not empty sessionScope.userName}">
                <span class="nav-user">
                    &#128100; ${sessionScope.userName}
                </span>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <span class="nav-role-badge">ADMIN</span>
                </c:if>
                <a href="${pageContext.request.contextPath}/auth/logout"
                   class="btn-secondary btn-sm nav-logout">
                    Logout
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/auth/login"
                   class="btn-secondary btn-sm">Login</a>
                <a href="${pageContext.request.contextPath}/auth/register"
                   class="btn-primary btn-sm">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty sessionScope.cartSuccess}">
        <div class="alert alert-success">
                ${sessionScope.cartSuccess}
            <% session.removeAttribute("cartSuccess"); %>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-error">
                ${sessionScope.cartError}
            <% session.removeAttribute("cartError"); %>
        </div>
    </c:if>

    <div class="page-header">
        <h1>
            <c:choose>
                <c:when test="${not empty searchKeyword}">
                    Search: "${searchKeyword}"
                </c:when>
                <c:when test="${not empty selectedCategory}">
                    ${selectedCategory}
                </c:when>
                <c:otherwise>
                    All Products
                </c:otherwise>
            </c:choose>
        </h1>
        <div style="display:flex; align-items:center; gap:0.75rem;">
            <c:if test="${not empty queryTimeMs}">
                <span class="perf-badge">Query: ${queryTimeMs}ms</span>
            </c:if>
            <button class="btn-primary" id="openAddProductModal">
                + Add Product
            </button>
        </div>
    </div>

    <div class="toolbar">
        <form method="get"
              action="${pageContext.request.contextPath}/products/search"
              class="search-form">
            <input type="text"
                   name="q"
                   placeholder="Search products..."
                   value="${searchKeyword}"
                   class="search-input"/>
            <button type="submit" class="btn-primary">Search</button>
        </form>
        <div class="category-filters">
            <a href="${pageContext.request.contextPath}/products/"
               class="btn-filter">All</a>
            <a href="${pageContext.request.contextPath}/products/category/Electronics"
               class="btn-filter">Electronics</a>
            <a href="${pageContext.request.contextPath}/products/category/Books"
               class="btn-filter">Books</a>
            <a href="${pageContext.request.contextPath}/products/category/Home"
               class="btn-filter">Home</a>
            <a href="${pageContext.request.contextPath}/products/category/Clothing"
               class="btn-filter">Clothing</a>
        </div>
    </div>

    <c:if test="${not empty searchKeyword}">
        <p class="search-info">
            <strong>${products.size()}</strong> result(s) for
            "<strong>${searchKeyword}</strong>"
        </p>
    </c:if>

    <c:choose>
        <c:when test="${not empty products}">
            <div class="product-grid">
                <c:forEach var="product" items="${products}">
                    <div class="product-card">
                        <div class="product-category">${product.category}</div>
                        <h3 class="product-name">${product.name}</h3>
                        <p class="product-desc">${product.description}</p>
                        <div class="product-footer">
                            <span class="product-price">
                                $<fmt:formatNumber value="${product.price}"
                                                   pattern="#,##0.00"/>
                            </span>
                            <span class="stock-badge">
                                Stock: ${inventoryManager.getStock(product.id)}
                            </span>
                        </div>
                        <form method="post"
                              action="${pageContext.request.contextPath}/cart/">
                            <input type="hidden" name="productId" value="${product.id}"/>
                            <div class="qty-row">
                                <input type="number"
                                       name="quantity"
                                       value="1"
                                       min="1"
                                       max="${inventoryManager.getStock(product.id)}"
                                       class="qty-input"/>
                                <c:choose>
                                    <c:when test="${inventoryManager.getStock(product.id) == 0}">
                                        <button type="submit" class="btn-primary" disabled>
                                            Out of Stock
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="btn-primary">
                                            Add to Cart
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <p>No products found.</p>
                <a href="${pageContext.request.contextPath}/products/"
                   class="btn-primary">View All Products</a>
            </div>
        </c:otherwise>
    </c:choose>

</div>

<!-- ── Add Product Modal ────────────────────────────────────────── -->
<div class="modal-overlay" id="addProductModal">
    <div class="modal">

        <div class="modal-header">
            <h2 class="modal-title">Add New Product</h2>
            <button class="modal-close" id="closeAddProductModal"
                    aria-label="Close modal">&#10005;</button>
        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/products/"
              class="modal-form">

            <div class="modal-body">

                <div class="form-group">
                    <label class="form-label">Product Name *</label>
                    <input type="text"
                           name="name"
                           placeholder="e.g. Laptop Pro 15"
                           class="form-input"
                           required
                           autofocus/>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Category *</label>
                        <select name="category" class="form-input" required>
                            <option value="" disabled selected>
                                Select category
                            </option>
                            <option value="Electronics">Electronics</option>
                            <option value="Books">Books</option>
                            <option value="Home">Home</option>
                            <option value="Clothing">Clothing</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Stock Quantity *</label>
                        <input type="number"
                               name="stockQuantity"
                               placeholder="e.g. 100"
                               class="form-input"
                               min="0"
                               required/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Price (USD) *</label>
                    <div class="price-input-wrapper">
                        <span class="price-prefix">$</span>
                        <input type="number"
                               name="price"
                               placeholder="0.00"
                               class="form-input price-input"
                               step="0.01"
                               min="0"
                               required/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Description *</label>
                    <textarea name="description"
                              placeholder="Describe the product..."
                              class="form-input form-textarea"
                              required></textarea>
                </div>

            </div>

            <div class="modal-footer">
                <button type="button"
                        class="btn-secondary"
                        id="cancelAddProduct">
                    Cancel
                </button>
                <button type="submit" class="btn-primary">
                    Add Product
                </button>
            </div>

        </form>
    </div>
</div>
<!-- ── End Modal ────────────────────────────────────────────────── -->

<footer class="footer">
    <p>TechMart Online</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>