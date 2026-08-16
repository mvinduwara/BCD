package com.globaltrade.scm.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = digest(salt, rawPassword);
        return HexFormat.of().formatHex(salt) + ":" + HexFormat.of().formatHex(hash);
    }

    public static boolean verify(String rawPassword, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = HexFormat.of().parseHex(parts[0]);
        byte[] expected = HexFormat.of().parseHex(parts[1]);
        byte[] actual = digest(salt, rawPassword);
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] digest(byte[] salt, String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            return messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}