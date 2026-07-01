<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - TechMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .auth-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--grey-50);
            padding: 1.5rem;
        }
        .auth-card {
            background: var(--white);
            border: 1px solid var(--grey-200);
            border-radius: 16px;
            padding: 2.5rem 2rem;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.08);
        }
        .auth-logo {
            font-size: 1.3rem;
            font-weight: 800;
            letter-spacing: -0.5px;
            text-align: center;
            margin-bottom: 0.25rem;
            color: var(--black);
        }
        .auth-subtitle {
            text-align: center;
            color: var(--grey-600);
            font-size: 0.875rem;
            margin-bottom: 2rem;
        }
        .auth-title {
            font-size: 1.4rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
            letter-spacing: -0.3px;
        }
        .auth-form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .auth-form .form-group {
            display: flex;
            flex-direction: column;
            gap: 0.4rem;
        }
        .auth-form .form-label {
            font-size: 0.82rem;
            font-weight: 600;
            color: var(--grey-800);
        }
        .auth-form .form-input { width: 100%; }
        .auth-submit {
            width: 100%;
            padding: 0.75rem;
            margin-top: 0.5rem;
            font-size: 0.95rem;
        }
        .auth-divider {
            text-align: center;
            color: var(--grey-400);
            font-size: 0.82rem;
            margin: 1rem 0 0;
        }
        .auth-divider a {
            color: var(--black);
            font-weight: 600;
            text-decoration: none;
        }
        .auth-divider a:hover { text-decoration: underline; }
        .password-hint {
            font-size: 0.75rem;
            color: var(--grey-400);
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card">

        <div class="auth-logo">TECHMART</div>
        <div class="auth-subtitle">Enterprise E-Commerce Platform</div>

        <c:if test="${not empty sessionScope.authError}">
            <div class="alert alert-error">
                    ${sessionScope.authError}
                <% session.removeAttribute("authError"); %>
            </div>
        </c:if>

        <h1 class="auth-title">Create account</h1>

        <form method="post"
              action="${pageContext.request.contextPath}/auth/register"
              class="auth-form">

            <div class="form-group">
                <label class="form-label">Full Name</label>
                <input type="text"
                       name="name"
                       placeholder="Your full name"
                       class="form-input"
                       required
                       autofocus/>
            </div>

            <div class="form-group">
                <label class="form-label">Email address</label>
                <input type="email"
                       name="email"
                       placeholder="you@email.com"
                       class="form-input"
                       required/>
            </div>

            <div class="form-group">
                <label class="form-label">Password</label>
                <input type="password"
                       name="password"
                       placeholder="Minimum 6 characters"
                       class="form-input"
                       required
                       minlength="6"/>
                <span class="password-hint">
                    At least 6 characters
                </span>
            </div>

            <div class="form-group">
                <label class="form-label">Confirm Password</label>
                <input type="password"
                       name="confirmPassword"
                       placeholder="Repeat your password"
                       class="form-input"
                       required/>
            </div>

            <button type="submit" class="btn-primary auth-submit">
                Create Account
            </button>

        </form>

        <p class="auth-divider">
            Already have an account?
            <a href="${pageContext.request.contextPath}/auth/login">
                Sign in
            </a>
        </p>

    </div>
</div>

</body>
</html>