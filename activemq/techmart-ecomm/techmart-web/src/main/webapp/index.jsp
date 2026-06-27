<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.Order" %>
<%@ page import="java.util.List" %>
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
        <a href="${pageContext.request.contextPath}/orders/" class="active">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty sessionScope.orderSuccess}">
        <div class="alert alert-success">
                ${sessionScope.orderSuccess}
            <% session.removeAttribute("orderSuccess"); %>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.orderError}">
        <div class="alert alert-error">
                ${sessionScope.orderError}
            <% session.removeAttribute("orderError"); %>
        </div>
    </c:if>

    <div class="hero">
        <h1>Welcome to TechMart Online</h1>
        <p>Enterprise-grade e-commerce powered by Jakarta EE on Payara 6</p>
        <a href="${pageContext.request.contextPath}/products/" class="btn-primary">Browse Products</a>
    </div>

    <div class="section">
        <h2>Recent Orders</h2>
        <c:choose>
            <c:when test="${not empty orders}">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>Total</th>
                        <th>Date</th>
                        <th>Processing Time</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>#${order.id}</td>
                            <td>${order.customerId}</td>
                            <td>${order.customerEmail}</td>
                            <td>
                                    <span class="badge badge-${order.status.toString().toLowerCase()}">
                                            ${order.status}
                                    </span>
                            </td>
                            <td>
                                $<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/>
                            </td>
                            <td>${order.createdAt}</td>
                            <td>${order.processingTimeMs}ms</td>
                            <td>
                                <c:if test="${order.status != 'SHIPPED'
                                               && order.status != 'DELIVERED'
                                               && order.status != 'CANCELLED'}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/orders/cancel/${order.id}"
                                          style="display:inline">
                                        <button type="submit" class="btn-danger btn-sm">
                                            Cancel
                                        </button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <p>No orders yet.</p>
                    <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
                        Start Shopping
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="section">
        <h2>Platform Overview</h2>
        <div class="info-grid">
            <div class="info-card">
                <div class="info-icon">&#9881;</div>
                <h3>Singleton Bean</h3>
                <p>InventoryManagerBean loads on startup and maintains a shared inventory cache
                    across all requests using @Lock annotations.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128260;</div>
                <h3>Stateless Bean</h3>
                <p>OrderProcessingBean handles order placement with full transaction support
                    and JMS message dispatch per order.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128722;</div>
                <h3>Stateful Bean</h3>
                <p>ShoppingCartBean maintains per-user cart state across multiple HTTP requests
                    with @StatefulTimeout of 30 minutes.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128233;</div>
                <h3>Message-Driven Bean</h3>
                <p>OrderNotificationMDB consumes from jms/OrderQueue and InventoryUpdateMDB
                    subscribes to jms/InventoryTopic.</p>
            </div>
        </div>
    </div>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE 10 &mdash; Payara 6 &mdash; MySQL</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html><%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.Order" %>
<%@ page import="java.util.List" %>
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
        <a href="${pageContext.request.contextPath}/orders/" class="active">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
    </div>
</nav>

<div class="container">

    <c:if test="${not empty sessionScope.orderSuccess}">
        <div class="alert alert-success">
                ${sessionScope.orderSuccess}
            <% session.removeAttribute("orderSuccess"); %>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.orderError}">
        <div class="alert alert-error">
                ${sessionScope.orderError}
            <% session.removeAttribute("orderError"); %>
        </div>
    </c:if>

    <div class="hero">
        <h1>Welcome to TechMart Online</h1>
        <p>Enterprise-grade e-commerce powered by Jakarta EE on Payara 6</p>
        <a href="${pageContext.request.contextPath}/products/" class="btn-primary">Browse Products</a>
    </div>

    <div class="section">
        <h2>Recent Orders</h2>
        <c:choose>
            <c:when test="${not empty orders}">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>Total</th>
                        <th>Date</th>
                        <th>Processing Time</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>#${order.id}</td>
                            <td>${order.customerId}</td>
                            <td>${order.customerEmail}</td>
                            <td>
                                    <span class="badge badge-${order.status.toString().toLowerCase()}">
                                            ${order.status}
                                    </span>
                            </td>
                            <td>
                                $<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/>
                            </td>
                            <td>${order.createdAt}</td>
                            <td>${order.processingTimeMs}ms</td>
                            <td>
                                <c:if test="${order.status != 'SHIPPED'
                                               && order.status != 'DELIVERED'
                                               && order.status != 'CANCELLED'}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/orders/cancel/${order.id}"
                                          style="display:inline">
                                        <button type="submit" class="btn-danger btn-sm">
                                            Cancel
                                        </button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <p>No orders yet.</p>
                    <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
                        Start Shopping
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="section">
        <h2>Platform Overview</h2>
        <div class="info-grid">
            <div class="info-card">
                <div class="info-icon">&#9881;</div>
                <h3>Singleton Bean</h3>
                <p>InventoryManagerBean loads on startup and maintains a shared inventory cache
                    across all requests using @Lock annotations.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128260;</div>
                <h3>Stateless Bean</h3>
                <p>OrderProcessingBean handles order placement with full transaction support
                    and JMS message dispatch per order.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128722;</div>
                <h3>Stateful Bean</h3>
                <p>ShoppingCartBean maintains per-user cart state across multiple HTTP requests
                    with @StatefulTimeout of 30 minutes.</p>
            </div>
            <div class="info-card">
                <div class="info-icon">&#128233;</div>
                <h3>Message-Driven Bean</h3>
                <p>OrderNotificationMDB consumes from jms/OrderQueue and InventoryUpdateMDB
                    subscribes to jms/InventoryTopic.</p>
            </div>
        </div>
    </div>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE 10 &mdash; Payara 6 &mdash; MySQL</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>