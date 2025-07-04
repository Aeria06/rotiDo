package com.rotido.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    @Value("${app.encryption.key}")
    private String companyKey;

    private byte[] deriveKey(String inputKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(inputKey.getBytes(StandardCharsets.UTF_8));
    }

    public String encrypt(String src) throws Exception {
        if (src == null) throw new IllegalArgumentException("Cannot encrypt null value");

        byte[] keyBytes = deriveKey(companyKey);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));

        byte[] ivAndCipher = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, ivAndCipher, 0, iv.length);
        System.arraycopy(encrypted, 0, ivAndCipher, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(ivAndCipher);
    }

    public String decrypt(String src) throws Exception {
        if (src == null) throw new IllegalArgumentException("Cannot decrypt null value");

        byte[] keyBytes = deriveKey(companyKey);
        byte[] data = Base64.getDecoder().decode(src);

        byte[] iv = new byte[16];
        System.arraycopy(data, 0, iv, 0, 16);
        byte[] ciphertext = new byte[data.length - 16];
        System.arraycopy(data, 16, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(ciphertext);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
