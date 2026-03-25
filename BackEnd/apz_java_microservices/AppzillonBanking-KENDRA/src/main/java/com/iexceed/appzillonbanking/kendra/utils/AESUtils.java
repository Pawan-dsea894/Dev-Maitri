package com.iexceed.appzillonbanking.kendra.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private AESUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * Generate 256-bit AES key from a secret
     */
    private static SecretKeySpec getKeyFromSecret(String secret) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Encrypt a JSON payload using AES/CBC/PKCS5Padding
     */
    public static String encrypt(String jsonPayload, String secretKey) throws Exception {
        SecretKeySpec keySpec = getKeyFromSecret(secretKey);

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Generate random IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));

        // Combine IV + Encrypted Data
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypt AES encrypted payload
     */
    public static String decrypt(String encryptedPayload, String secretKey) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPayload);

        // Extract IV
        byte[] iv = new byte[16];
        System.arraycopy(decodedBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract encrypted data
        byte[] encryptedBytes = new byte[decodedBytes.length - 16];
        System.arraycopy(decodedBytes, 16, encryptedBytes, 0, encryptedBytes.length);

        SecretKeySpec keySpec = getKeyFromSecret(secretKey);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
