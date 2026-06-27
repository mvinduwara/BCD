<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Performance Metrics - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <span class="perf-badge">Live refresh: 10s</span>
            <span class="perf-badge" id="live-timestamp">${timestamp}</span>
            <a href="${pageContext.request.contextPath}/metrics/json"
               class="btn-secondary btn-sm" target="_blank">
                JSON API
            </a>
        </div>
    </div>

    <div class="metrics-grid">

        <div class="metric-card">
            <h3>Inventory Cache — Singleton Bean</h3>
            <div class="metric-row">
                <span class="metric-label">Cache Size</span>
                <span class="metric-value" id="live-cache-size">
                    ${cacheSize} products
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Hits</span>
                <span class="metric-value metric-good" id="live-cache-hits">
                    ${cacheHits}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Misses</span>
                <span class="metric-value metric-warn" id="live-cache-misses">
                    ${cacheMisses}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Cache Init Time</span>
                <span class="metric-value" id="live-cache-init">
                    ${cacheInitTimeMs}ms
                </span>
            </div>
            <div class="metric-bar-label">Cache Hit Rate</div>
            <c:set var="hitRate"
                   value="${(cacheHits + cacheMisses) == 0 ? 0 :
                           (cacheHits * 100) / (cacheHits + cacheMisses)}"/>
            <div class="metric-bar-track">
                <div class="metric-bar-fill"
                     id="live-hit-rate-bar"
                     style="width:${hitRate}%"></div>
            </div>
            <div class="metric-bar-pct" id="live-hit-rate-pct">
                <fmt:formatNumber value="${hitRate}" pattern="#,##0"/>%
            </div>
        </div>

        <div class="metric-card">
            <h3>JMS Messaging — MDBs</h3>
            <div class="metric-row">
                <span class="metric-label">Orders Processed</span>
                <span class="metric-value" id="live-orders-processed">
                    ${ordersProcessed}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Inventory Updates</span>
                <span class="metric-value" id="live-inventory-updates">
                    ${inventoryUpdates}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Async Notifications Sent</span>
                <span class="metric-value" id="live-notifications-sent">
                    ${notificationsSent}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Notification Time</span>
                <span class="metric-value" id="live-avg-notification">
                    <fmt:formatNumber value="${avgNotificationTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>Web Layer — Servlet Performance</h3>
            <div class="metric-row">
                <span class="metric-label">Total Requests</span>
                <span class="metric-value" id="live-total-requests">
                    ${totalRequests}
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Avg Response Time</span>
                <span class="metric-value" id="live-avg-response">
                    <fmt:formatNumber value="${avgResponseTimeMs}" pattern="#,##0.00"/>ms
                </span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Uptime</span>
                <span class="metric-value metric-good" id="live-uptime">
                    ${uptimeSeconds}s
                </span>
            </div>
        </div>

        <div class="metric-card">
            <h3>System Configuration</h3>
            <div class="metric-row">
                <span class="metric-label">Platform</span>
                <span class="metric-value">Jakarta EE 10</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Application Server</span>
                <span class="metric-value">Payara 6</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Database</span>
                <span class="metric-value">MySQL 8</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Build Tool</span>
                <span class="metric-value">Maven</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Java Version</span>
                <span class="metric-value">17</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">Session Bean Types</span>
                <span class="metric-value">Stateless, Stateful, Singleton</span>
            </div>
            <div class="metric-row">
                <span class="metric-label">JMS Patterns</span>
                <span class="metric-value">Queue + Topic</span>
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