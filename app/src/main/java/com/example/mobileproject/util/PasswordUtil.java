package com.example.mobileproject.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class PasswordUtil {

    private static final String AES = "AES";
    private static final byte[] key = new byte[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' };

    public static String encrypt(String password) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedPassword) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedValue = Base64.decode(encryptedPassword, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(decodedValue);
        return new String(decrypted);
    }
}
