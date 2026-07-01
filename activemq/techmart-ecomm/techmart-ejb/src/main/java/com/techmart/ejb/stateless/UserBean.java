package com.techmart.ejb.stateless;

import com.techmart.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UserBean {

    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    public User register(String name, String email, String rawPassword)
            throws Exception {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");
        if (rawPassword == null || rawPassword.length() < 6)
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters");

        if (emailExists(email))
            throw new IllegalArgumentException(
                    "An account with this email already exists");

        String hashedPassword = hashPassword(rawPassword);
        User user = new User(name.trim(), email.trim().toLowerCase(),
                hashedPassword, User.Role.CUSTOMER);
        em.persist(user);
        logger.info("New user registered: " + email);
        return user;
    }

    public User authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) return null;

        try {
            String hashedPassword = hashPassword(rawPassword);
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email " +
                            "AND u.password = :password",
                    User.class);
            query.setParameter("email",    email.trim().toLowerCase());
            query.setParameter("password", hashedPassword);
            return query.getSingleResult();

        } catch (NoResultException e) {
            logger.warning("Failed login attempt for: " + email);
            return null;
        } catch (Exception e) {
            logger.severe("Authentication error: " + e.getMessage());
            return null;
        }
    }

    public boolean emailExists(String email) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                    Long.class);
            query.setParameter("email", email.trim().toLowerCase());
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public User findByEmail(String email) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email",
                    User.class);
            query.setParameter("email", email.trim().toLowerCase());
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.createdAt DESC",
                User.class).getResultList();
    }

    public boolean changePassword(Long userId,
                                  String oldPassword,
                                  String newPassword) {
        try {
            User user = em.find(User.class, userId);
            if (user == null) return false;

            String oldHash = hashPassword(oldPassword);
            if (!oldHash.equals(user.getPassword())) return false;

            if (newPassword == null || newPassword.length() < 6) return false;

            user.setPassword(hashPassword(newPassword));
            em.merge(user);
            return true;

        } catch (Exception e) {
            logger.severe("Password change error: " + e.getMessage());
            return false;
        }
    }

    public static String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}