package com.globaltrade.scm.security;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ScmLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    private String username;
    private String resolvedRole;
    private boolean loginSucceeded;
    private boolean commitSucceeded;

    private ScmPrincipal userPrincipal;
    private ScmPrincipal rolePrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Unable to collect credentials: " + e.getMessage());
        }

        username = nameCallback.getName();
        char[] passwordChars = passwordCallback.getPassword();
        String password = passwordChars == null ? "" : new String(passwordChars);
        passwordCallback.clearPassword();

        if (username == null || username.isBlank() || password.isBlank()) {
            throw new FailedLoginException("Username and password are required");
        }

        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("jdbc/scmDS");
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT password_hash, role FROM personnel WHERE username = ?")) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new FailedLoginException("No such user: " + username);
                    }
                    String storedHash = resultSet.getString("password_hash");
                    if (!PasswordHasher.verify(password, storedHash)) {
                        throw new FailedLoginException("Incorrect password for user: " + username);
                    }
                    resolvedRole = resultSet.getString("role");
                }
            }
        } catch (NamingException | SQLException e) {
            throw new LoginException("Authentication backend unavailable: " + e.getMessage());
        }

        loginSucceeded = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        userPrincipal = new ScmPrincipal(username);
        rolePrincipal = new ScmPrincipal(resolvedRole);
        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(rolePrincipal);
        commitSucceeded = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        if (commitSucceeded) {
            logout();
        } else {
            loginSucceeded = false;
            username = null;
            resolvedRole = null;
        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        if (userPrincipal != null) {
            subject.getPrincipals().remove(userPrincipal);
        }
        if (rolePrincipal != null) {
            subject.getPrincipals().remove(rolePrincipal);
        }
        loginSucceeded = false;
        commitSucceeded = false;
        username = null;
        resolvedRole = null;
        userPrincipal = null;
        rolePrincipal = null;
        return true;
    }
}