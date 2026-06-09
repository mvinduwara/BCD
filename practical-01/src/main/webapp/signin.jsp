<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign In</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .signin-container { background: white; width: 100%; max-width: 380px; padding: 40px 30px; border-radius: 12px; box-shadow: 0 15px 35px rgba(0,0,0,0.2); }
        h2 { text-align: center; color: #333; margin-bottom: 20px; font-weight: 600; font-size: 28px; }
        .error-msg { color: #d9534f; background: #fdf7f7; border: 1px solid #d9534f; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 20px; font-size: 14px; }
        .success-msg { color: #28a745; background: #f0fdf4; border: 1px solid #28a745; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 20px; font-size: 14px; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; color: #555; font-size: 14px; font-weight: 500; }
        input { width: 100%; padding: 12px 15px; border: 2px solid #e1e5ee; border-radius: 8px; font-size: 15px; color: #333; transition: border-color 0.3s ease; }
        input:focus { border-color: #667eea; outline: none; }
        button { width: 100%; padding: 14px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; transition: transform 0.2s ease; margin-top: 10px; }
        button:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102,126,234,0.4); }
        .links { text-align: center; margin-top: 25px; font-size: 14px; }
        .links a { color: #667eea; text-decoration: none; font-weight: 600; }
        .links a:hover { color: #764ba2; text-decoration: underline; }
    </style>
</head>
<body>

<div class="signin-container">
    <h2>Welcome Back</h2>

    <%-- Displays errors from Signin.java --%>
    <% if(request.getAttribute("error") != null) { %>
    <div class="error-msg"><%= request.getAttribute("error") %></div>
    <% } %>

    <%-- Displays success message after coming from Signup.java --%>
    <% if("true".equals(request.getParameter("success"))) { %>
    <div class="success-msg">Account created successfully! Please sign in.</div>
    <% } %>

    <form action="signin" method="POST">
        <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" id="email" name="email" placeholder="Enter your email" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Enter your password" required>
        </div>
        <button type="submit">Sign In</button>
    </form>
    <div class="links">
        <p>Don't have an account? <a href="signup.jsp">Sign up here</a></p>
    </div>
</div>

</body>
</html>