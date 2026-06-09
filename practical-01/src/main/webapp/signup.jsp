<%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 6/9/2026
  Time: 8:43 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; padding: 50px; }
        .signup-container { width: 300px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; }
        input { width: 100%; padding: 8px; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>

<div class="signup-container">
    <h2>Create Account</h2>
    <form action="signup" method="POST">
        <div class="form-group">
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="mobile">Mobile:</label>
            <input type="tel" id="mobile" name="mobile" required>
        </div>
        <button type="submit">Sign Up</button>
    </form>
</div>

</body>
</html>