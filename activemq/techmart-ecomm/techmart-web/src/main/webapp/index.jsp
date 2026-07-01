<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>TechMart Online</title>
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

    <div class="hero">
        <h1>Welcome to TechMart Online</h1>
        <p>Enterprise-grade e-commerce powered by Jakarta EE on Payara 6</p>
        <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
            Browse Products
        </a>
    </div>

    <div class="section">
        <h2>Platform Overview</h2>
        <div class="info-grid">
            <div class="info-card">
                <div class="info-icon">&#9881;</div>
                <h3>Singleton Bean</h3>
                <p>InventoryManagerBean loads on startup and maintains a shared
                    inventory cache using @Lock annotations for thread safety.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128260;</div>
                <h3>Stateless Bean</h3>
                <p>OrderProcessingBean handles order placement with full JTA
                    transaction support and JMS message dispatch per order.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128722;</div>
                <h3>Stateful Bean</h3>
                <p>ShoppingCartBean maintains per-user cart state across requests
                    with @StatefulTimeout of 30 minutes and @Remove on checkout.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128233;</div>
                <h3>Message-Driven Bean</h3>
                <p>OrderNotificationMDB consumes from jms/OrderQueue and
                    InventoryUpdateMDB subscribes to jms/InventoryTopic.</p>
            </div>
        </div>
    </div>

</div>

<footer class="footer">
    <p>TechMart Online</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>