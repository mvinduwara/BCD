package com.techmart.web.servlet;

import com.techmart.ejb.async.NotificationService;
import com.techmart.ejb.mdb.InventoryUpdateMDB;
import com.techmart.ejb.mdb.OrderNotificationMDB;
import com.techmart.ejb.singleton.InventoryManagerBean;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@WebServlet("/metrics/*")
public class MetricsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MetricsServlet.class.getName());

    @EJB
    private InventoryManagerBean inventoryManagerBean;

    private static long totalRequests = 0;
    private static long totalResponseTimeMs = 0;
    private static final long startupTime = System.currentTimeMillis();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start    = System.currentTimeMillis();
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/json")) {
            serveJson(req, resp, start);
        } else {
            serveDashboard(req, resp, start);
        }
    }

    private void serveDashboard(HttpServletRequest req,
                                HttpServletResponse resp, long start)
            throws ServletException, IOException {

        long elapsed = System.currentTimeMillis() - start;
        recordRequest(elapsed);

        req.setAttribute("cacheSize",               inventoryManagerBean.getCacheSize());
        req.setAttribute("cacheHits",               inventoryManagerBean.getTotalCacheHits());
        req.setAttribute("cacheMisses",             inventoryManagerBean.getTotalCacheMisses());
        req.setAttribute("cacheInitTimeMs",         inventoryManagerBean.getCacheInitTimeMs());
        req.setAttribute("ordersProcessed",         OrderNotificationMDB.getProcessedCount());
        req.setAttribute("inventoryUpdates",        InventoryUpdateMDB.getUpdateCount());
        req.setAttribute("notificationsSent",       NotificationService.getTotalNotificationsSent());
        req.setAttribute("avgNotificationTimeMs",   NotificationService.getAverageNotificationTimeMs());
        req.setAttribute("totalRequests",           totalRequests);
        req.setAttribute("avgResponseTimeMs",       getAverageResponseTime());
        req.setAttribute("uptimeSeconds",           getUptimeSeconds());
        req.setAttribute("timestamp",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        req.getRequestDispatcher("/metrics.jsp").forward(req, resp);
    }

    private void serveJson(HttpServletRequest req,
                           HttpServletResponse resp, long start)
            throws IOException {

        long elapsed = System.currentTimeMillis() - start;
        recordRequest(elapsed);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.println("{");
        out.println("  \"timestamp\": \""
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\",");
        out.println("  \"inventory\": {");
        out.println("    \"cacheSize\": "       + inventoryManagerBean.getCacheSize()       + ",");
        out.println("    \"cacheHits\": "       + inventoryManagerBean.getTotalCacheHits()  + ",");
        out.println("    \"cacheMisses\": "     + inventoryManagerBean.getTotalCacheMisses()+ ",");
        out.println("    \"cacheInitTimeMs\": " + inventoryManagerBean.getCacheInitTimeMs() );
        out.println("  },");
        out.println("  \"messaging\": {");
        out.println("    \"ordersProcessed\": "     + OrderNotificationMDB.getProcessedCount()              + ",");
        out.println("    \"inventoryUpdates\": "    + InventoryUpdateMDB.getUpdateCount()                   + ",");
        out.println("    \"notificationsSent\": "   + NotificationService.getTotalNotificationsSent()       + ",");
        out.println("    \"avgNotificationMs\": "   + NotificationService.getAverageNotificationTimeMs()    );
        out.println("  },");
        out.println("  \"performance\": {");
        out.println("    \"totalRequests\": "   + totalRequests                 + ",");
        out.println("    \"avgResponseMs\": "   + getAverageResponseTime()      + ",");
        out.println("    \"uptimeSeconds\": "   + getUptimeSeconds()            );
        out.println("  }");
        out.println("}");
    }

    private static synchronized void recordRequest(long elapsedMs) {
        totalRequests++;
        totalResponseTimeMs += elapsedMs;
    }

    private static double getAverageResponseTime() {
        return totalRequests == 0 ? 0 : (double) totalResponseTimeMs / totalRequests;
    }

    private static long getUptimeSeconds() {
        return (System.currentTimeMillis() - startupTime) / 1000;
    }
}