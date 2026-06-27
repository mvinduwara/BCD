<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.CartItem" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Cart - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">TechMart Online</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products/">Products</a>
        <a href="${pageContext.request.contextPath}/cart/" class="active">Cart</a>
        <a href="${pageContext.request.contextPath}/orders/">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>Shopping Cart</h1>
        <c:if test="${not empty queryTimeMs}">
            <span class="perf-badge">Loaded in ${queryTimeMs}ms</span>
        </c:if>
    </div>

    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-error">
                ${sessionScope.cartError}
            <% session.removeAttribute("cartError"); %>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.cartSuccess}">
        <div class="alert alert-success">
                ${sessionScope.cartSuccess}
            <% session.removeAttribute("cartSuccess"); %>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty cartItems}">

            <table class="data-table">
                <thead>
                <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartItems}">
                    <tr>
                        <td>${item.productName}</td>
                        <td>
                            $<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                        </td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/cart/update"
                                  class="inline-form">
                                <input type="hidden"
                                       name="productId"
                                       value="${item.productId}"/>
                                <input type="number"
                                       name="quantity"
                                       value="${item.quantity}"
                                       min="1"
                                       class="qty-input-sm"/>
                                <button type="submit" class="btn-secondary btn-sm">
                                    Update
                                </button>
                            </form>
                        </td>
                        <td>
                            $<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                        </td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/cart/remove"
                                  class="inline-form">
                                <input type="hidden"
                                       name="productId"
                                       value="${item.productId}"/>
                                <button type="submit" class="btn-danger btn-sm">
                                    Remove
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
                <tfoot>
                <tr>
                    <td colspan="3" class="total-label">
                        Total (${itemCount} items)
                    </td>
                    <td colspan="2" class="total-value">
                        $<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/>
                    </td>
                </tr>
                </tfoot>
            </table>

            <div class="cart-actions">
                <a href="${pageContext.request.contextPath}/cart/clear"
                   class="btn-danger">
                    Clear Cart
                </a>
                <a href="${pageContext.request.contextPath}/products/"
                   class="btn-secondary">
                    Continue Shopping
                </a>
                <button id="checkout-toggle-btn" class="btn-primary">
                    Proceed to Checkout
                </button>
            </div>

            <div id="checkoutForm" class="checkout-form" style="display:none;">
                <h2>Checkout</h2>
                <form method="post"
                      action="${pageContext.request.contextPath}/orders/place">
                    <div class="form-row">
                        <input type="email"
                               name="email"
                               placeholder="Your Email Address"
                               class="form-input"
                               required/>
                    </div>
                    <div class="order-summary">
                        <p>Items: <strong>${itemCount}</strong></p>
                        <p>Total:
                            <strong>
                                $<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/>
                            </strong>
                        </p>
                        <p style="font-size:0.8rem; color:#888; margin-top:0.5rem;">
                            Your order will be processed asynchronously via JMS.
                            You will receive a confirmation notification.
                        </p>
                    </div>
                    <button type="submit" class="btn-primary">Confirm Order</button>
                </form>
            </div>

        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <p>Your cart is empty.</p>
                <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
                    Browse Products
                </a>
            </div>
        </c:otherwise>
    </c:choose>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE 10 &mdash; Payara 6 &mdash; MySQL</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html><%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.techmart.model.CartItem" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Cart - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">TechMart Online</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products/">Products</a>
        <a href="${pageContext.request.contextPath}/cart/" class="active">Cart</a>
        <a href="${pageContext.request.contextPath}/orders/">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/">Metrics</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>Shopping Cart</h1>
        <c:if test="${not empty queryTimeMs}">
            <span class="perf-badge">Loaded in ${queryTimeMs}ms</span>
        </c:if>
    </div>

    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-error">
                ${sessionScope.cartError}
            <% session.removeAttribute("cartError"); %>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.cartSuccess}">
        <div class="alert alert-success">
                ${sessionScope.cartSuccess}
            <% session.removeAttribute("cartSuccess"); %>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty cartItems}">

            <table class="data-table">
                <thead>
                <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartItems}">
                    <tr>
                        <td>${item.productName}</td>
                        <td>
                            $<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                        </td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/cart/update"
                                  class="inline-form">
                                <input type="hidden"
                                       name="productId"
                                       value="${item.productId}"/>
                                <input type="number"
                                       name="quantity"
                                       value="${item.quantity}"
                                       min="1"
                                       class="qty-input-sm"/>
                                <button type="submit" class="btn-secondary btn-sm">
                                    Update
                                </button>
                            </form>
                        </td>
                        <td>
                            $<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                        </td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/cart/remove"
                                  class="inline-form">
                                <input type="hidden"
                                       name="productId"
                                       value="${item.productId}"/>
                                <button type="submit" class="btn-danger btn-sm">
                                    Remove
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
                <tfoot>
                <tr>
                    <td colspan="3" class="total-label">
                        Total (${itemCount} items)
                    </td>
                    <td colspan="2" class="total-value">
                        $<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/>
                    </td>
                </tr>
                </tfoot>
            </table>

            <div class="cart-actions">
                <a href="${pageContext.request.contextPath}/cart/clear"
                   class="btn-danger">
                    Clear Cart
                </a>
                <a href="${pageContext.request.contextPath}/products/"
                   class="btn-secondary">
                    Continue Shopping
                </a>
                <button id="checkout-toggle-btn" class="btn-primary">
                    Proceed to Checkout
                </button>
            </div>

            <div id="checkoutForm" class="checkout-form" style="display:none;">
                <h2>Checkout</h2>
                <form method="post"
                      action="${pageContext.request.contextPath}/orders/place">
                    <div class="form-row">
                        <input type="email"
                               name="email"
                               placeholder="Your Email Address"
                               class="form-input"
                               required/>
                    </div>
                    <div class="order-summary">
                        <p>Items: <strong>${itemCount}</strong></p>
                        <p>Total:
                            <strong>
                                $<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/>
                            </strong>
                        </p>
                        <p style="font-size:0.8rem; color:#888; margin-top:0.5rem;">
                            Your order will be processed asynchronously via JMS.
                            You will receive a confirmation notification.
                        </p>
                    </div>
                    <button type="submit" class="btn-primary">Confirm Order</button>
                </form>
            </div>

        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <p>Your cart is empty.</p>
                <a href="${pageContext.request.contextPath}/products/" class="btn-primary">
                    Browse Products
                </a>
            </div>
        </c:otherwise>
    </c:choose>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE 10 &mdash; Payara 6 &mdash; MySQL</p>
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>