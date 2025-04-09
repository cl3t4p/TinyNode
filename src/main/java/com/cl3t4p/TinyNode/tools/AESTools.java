package com.cl3t4p.TinyNode.tools;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESTools {

    public static String encrypt(String data,byte[] encodedKey) {
        try {
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes());
            return Base64.getEncoder()
                    .encodeToString(cipherText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String data,String base64key) {
        try {
            byte[] encodedKey = Base64.getDecoder().decode(base64key);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes());
            return Base64.getEncoder()
                    .encodeToString(cipherText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String data,SecretKey originalKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes());
            return Base64.getEncoder()
                    .encodeToString(cipherText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }



    public static String decrypt(String data,byte[] encodedKey) {
        try {
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(data));
            return new String(plainText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String data,String base64key) {
        try {
            byte[] encodedKey = Base64.getDecoder().decode(base64key);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(data));
            return new String(plainText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String data,SecretKey originalKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(data));
            return new String(plainText);

        } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
