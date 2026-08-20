package com.globaltrade.scm.security;

import com.globaltrade.scm.entity.Personnel;
import com.globaltrade.scm.entity.PersonnelRole;
import com.globaltrade.scm.entity.Vendor;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class PersonnelSeeder {

    private static final Logger LOGGER = Logger.getLogger(PersonnelSeeder.class.getName());
    private static final String DEMO_PASSWORD = "password123";

    @PersistenceContext(unitName = "scmPU")
    private EntityManager entityManager;

    @PostConstruct
    public void seedDemoAccounts() {
        Long existing = entityManager.createQuery("SELECT COUNT(p) FROM Personnel p", Long.class).getSingleResult();
        if (existing > 0) {
            return;
        }
        Vendor pacificRim = findVendorByName("Pacific Rim Freight");
        createAccount("coordinator1", "Priya Nakamura", PersonnelRole.COORDINATOR, "priya.nakamura@globaltradelogistics.com", null);
        createAccount("customs1", "Elena Marchetti", PersonnelRole.CUSTOMS_AGENT, "elena.marchetti@globaltradelogistics.com", null);
        createAccount("warehouse1", "Tobias Reinholt", PersonnelRole.WAREHOUSE_MANAGER, "tobias.reinholt@globaltradelogistics.com", null);
        createAccount("vendor1", "Amara Osei", PersonnelRole.VENDOR_REPRESENTATIVE, "amara.osei@pacificrimfreight.com", pacificRim);
    }

    private Vendor findVendorByName(String name) {
        List<Vendor> matches = entityManager.createQuery("SELECT v FROM Vendor v WHERE v.name = :name", Vendor.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        if (matches.isEmpty()) {
            LOGGER.warning(() -> "No vendor named '" + name + "' — seeding vendor1 with no linked vendor.");
            return null;
        }
        return matches.get(0);
    }

    private void createAccount(String username, String fullName, PersonnelRole role, String email, Vendor vendor) {
        Personnel personnel = new Personnel();
        personnel.setUsername(username);
        personnel.setPasswordHash(digest(DEMO_PASSWORD));
        personnel.setFullName(fullName);
        personnel.setRole(role);
        personnel.setEmail(email);
        personnel.setVendor(vendor);
        entityManager.persist(personnel);
    }

    private String digest(String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}