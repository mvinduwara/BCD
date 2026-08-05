<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>J2EE Security | Login</title>
</head>
<body>
    <h1>J2EE Security Login</h1>

<%--    <p style="color:red">--%>
<%--        ${requestScope.message}--%>
<%--    </p>--%>
    <form method="POST" action="j_security_check">
        <table>
            <tr>
                <th>Username:</th>
                <td><input type="text" name="j_username" /></td>
            </tr>
            <tr>
                <th>Password:</th>
                <td><input type="password" name="j_password" /></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Login" /></td>
            </tr>
        </table>
    </form>

    <h1>Custom Form Auth</h1>
    <form method="POST" action="${pageContext.request.contextPath}/custom_auth">
        <table>
            <tr>
                <th>Username:</th>
                <td><input type="text" name="username" /></td>
            </tr>
            <tr>
                <th>Password:</th>
                <td><input type="password" name="password" /></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Login" /></td>
            </tr>
        </table>
    </form>
</body>
</html>
