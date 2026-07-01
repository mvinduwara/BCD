<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login - TechMart</title>
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
    .auth-form .form-input {
      width: 100%;
    }
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

    <c:if test="${not empty sessionScope.authSuccess}">
      <div class="alert alert-success">
          ${sessionScope.authSuccess}
        <% session.removeAttribute("authSuccess"); %>
      </div>
    </c:if>

    <h1 class="auth-title">Sign in</h1>

    <form method="post"
          action="${pageContext.request.contextPath}/auth/login"
          class="auth-form">

      <div class="form-group">
        <label class="form-label">Email address</label>
        <input type="email"
               name="email"
               placeholder="you@email.com"
               class="form-input"
               required
               autofocus/>
      </div>

      <div class="form-group">
        <label class="form-label">Password</label>
        <input type="password"
               name="password"
               placeholder="Enter your password"
               class="form-input"
               required/>
      </div>

      <button type="submit" class="btn-primary auth-submit">
        Sign in
      </button>

    </form>

    <p class="auth-divider">
      Don't have an account?
      <a href="${pageContext.request.contextPath}/auth/register">
        Create one
      </a>
    </p>

  </div>
</div>

</body>
</html>