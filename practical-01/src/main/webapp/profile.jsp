<%@ page import="lk.jiat.bcd.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Logout logic: If user clicks the "Sign Out" button which passes ?logout=true
    if("true".equals(request.getParameter("logout"))) {
        session.invalidate(); // Destroys the session
        response.sendRedirect("signin.jsp");
        return;
    }

    User loggedInUser = (User) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        // If not logged in, force navigation back to signin
        response.sendRedirect("signin.jsp");
        return;
    }
%>
<html>
<head>
    <title>User Profile</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f4f7fb; }
        .profile-card { background: white; width: 100%; max-width: 400px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); overflow: hidden; }
        .profile-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; text-align: center; color: white; }
        .avatar { width: 80px; height: 80px; background: rgba(255,255,255,0.2); border: 3px solid white; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-size: 32px; font-weight: bold; margin: 0 auto 15px auto; }
        .profile-header h2 { font-size: 24px; font-weight: 600; margin-bottom: 5px; }
        .profile-header p { font-size: 14px; opacity: 0.9; }
        .profile-body { padding: 30px; }
        .detail-row { background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 15px; display: flex; flex-direction: column; border-left: 4px solid #667eea; }
        .detail-label { font-size: 12px; color: #777; text-transform: uppercase; font-weight: 600; letter-spacing: 0.5px; }
        .detail-value { font-size: 16px; color: #333; margin-top: 4px; font-weight: 500; }
        .logout-btn { display: block; width: 100%; padding: 14px; text-align: center; background: #ff4757; color: white; text-decoration: none; border-radius: 8px; font-weight: bold; margin-top: 25px; transition: background 0.3s ease; }
        .logout-btn:hover { background: #ff6b81; }
    </style>
</head>
<body>

<div class="profile-card">
    <div class="profile-header">
        <div class="avatar"><%= loggedInUser.getName().substring(0, 1).toUpperCase() %></div>
        <h2><%= loggedInUser.getName() %></h2>
        <p>Active Member</p>
    </div>

    <div class="profile-body">
        <div class="detail-row">
            <span class="detail-label">Full Name</span>
            <span class="detail-value"><%= loggedInUser.getName() %></span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Email Address</span>
            <span class="detail-value"><%= loggedInUser.getEmail() %></span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Mobile Number</span>
            <span class="detail-value"><%= loggedInUser.getMobile() %></span>
        </div>

        <%-- Secure signout link --%>
        <a href="profile.jsp?logout=true" class="logout-btn">Sign Out</a>
    </div>
</div>

</body>
</html>