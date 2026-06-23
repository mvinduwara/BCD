<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.Product" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">TechMart Online</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products/" class="active">Products</a>
        <a href="${pageContext.request.contextPath}/cart/">Cart</a>
        <a href="${pageContext.request.contextPath}/orders/">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>Products</h1>
        <c:if test="${not empty queryTimeMs}">
            <span class="perf-badge">Query: ${queryTimeMs}ms</span>
        </c:if>
    </div>

    <div class="toolbar">
        <form method="get" action="${pageContext.request.contextPath}/products/search"
              class="search-form">
            <input type="text" name="q" placeholder="Search products..."
                   value="${searchKeyword}" class="search-input"/>
            <button type="submit" class="btn-primary">Search</button>
        </form>

        <div class="category-filters">
            <a href="${pageContext.request.contextPath}/products/" class="btn-filter">All</a>
            <a href="${pageContext.request.contextPath}/products/category/Electronics"
               class="btn-filter">Electronics</a>
            <a href="${pageContext.request.contextPath}/products/category/Clothing"
               class="btn-filter">Clothing</a>
            <a href="${pageContext.request.contextPath}/products/category/Books"
               class="btn-filter">Books</a>
            <a href="${pageContext.request.contextPath}/products/category/Home"
               class="btn-filter">Home</a>
        </div>
    </div>

    <c:if test="${not empty searchKeyword}">
        <p class="search-info">
            Results for "<strong>${searchKeyword}</strong>" — ${products.size()} found
        </p>
    </c:if>

    <c:if test="${not empty selectedCategory}">
        <p class="search-info">
            Category: <strong>${selectedCategory}</strong> — ${products.size()} products
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
                                $<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                            </span>
                            <span class="stock-badge">
                                Stock: ${inventoryManager.getStock(product.id)}
                            </span>
                        </div>
                        <form method="post"
                              action="${pageContext.request.contextPath}/cart/">
                            <input type="hidden" name="productId" value="${product.id}"/>
                            <div class="qty-row">
                                <input type="number" name="quantity" value="1" min="1"
                                       max="${inventoryManager.getStock(product.id)}"
                                       class="qty-input"/>
                                <button type="submit" class="btn-primary"
                                    ${inventoryManager.getStock(product.id) == 0 ? 'disabled' : ''}>
                                        ${inventoryManager.getStock(product.id) == 0 ? 'Out of Stock' : 'Add to Cart'}
                                </button>
                            </div>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <p>No products found.</p>
                <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
                    View All Products
                </a>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="section">
        <h2>Add New Product</h2>
        <form method="post" action="${pageContext.request.contextPath}/products/" class="product-form">
            <div class="form-row">
                <input type="text"   name="name"          placeholder="Product Name"   class="form-input" required/>
                <input type="text"   name="category"      placeholder="Category"       class="form-input" required/>
            </div>
            <div class="form-row">
                <input type="number" name="price"         placeholder="Price"          class="form-input"
                       step="0.01" min="0" required/>
                <input type="number" name="stockQuantity" placeholder="Stock Quantity" class="form-input"
                       min="0" required/>
            </div>
            <textarea name="description" placeholder="Product Description"
                      class="form-input form-textarea" required></textarea>
            <button type="submit" class="btn-primary">Add Product</button>
        </form>
    </div>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE Platform &mdash; Payara 6</p>
</footer>

</body>
</html>