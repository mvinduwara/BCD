<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <meta http-equiv="refresh" content="10">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">TechMart Online</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products/">Products</a>
        <a href="${pageContext.request.contextPath}/cart/">Cart</a>
        <a href="${pageContext.request.contextPath}/orders/">Orders</a>
        <a href="${pageContext.request.contextPath}/metrics/" class="active">Metrics</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h1>Performance Metrics Dashboard</h1>
        <div>
            <span class="perf-badge">Auto-refresh: 10s</span>
            <span class="perf-badge">Last updated: ${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm">JSON API</a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache (Singleton Bean)</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value">${cacheSize} products</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good">${cacheHits}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn">${cacheMisses}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Init Time</span>
                <span class="metric-value">${cacheInitTimeMs}ms</span>
            </div>
            <div class="metric-bar-label">Hit Rate</div>
            <c:set var="hitRate"
                   value="${cacheHits + cacheMisses == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill" style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct">${hitRate}%</div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging (MDBs)</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value">${ordersProcessed}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value">${inventoryUpdates}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Notifications Sent</span>
                <span class="metric-value">${notificationsSent}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value">${totalRequests}</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good">${uptimeSeconds}s</span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Info</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">11</span>
            </div>
        </div>

    </div>

</div>

<footer class="footer">
    <p>TechMart Online &mdash; Jakarta EE Platform &mdash; Payara 6</p>
</footer>

</body>
</html>