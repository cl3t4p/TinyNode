package com.cl3t4p.TinyNode.tools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.jetbrains.annotations.NotNull;

/**
 * AESTools - A utility class for AES encryption and decryption.
 *
 * <p>This class provides methods to encrypt and decrypt data using AES algorithm with a given key.
 * It supports encryption and decryption using byte arrays, base64 encoded keys, and SecretKey
 * objects.
 */
public class AESTools {

  public static String encrypt(String data, byte[] encodedKey) {
    try {
      SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, originalKey);
      byte[] cipherText = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(cipherText);

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | NoSuchPaddingException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encryptToBase64FromByte(byte[] data, String base64key) {
    try {
      byte[] encodedKey = Base64.getDecoder().decode(base64key);
      SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, originalKey);
      byte[] cipherText = cipher.doFinal(data);
      return Base64.getEncoder().encodeToString(cipherText);

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | NoSuchPaddingException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encrypt(String data, SecretKey originalKey) {
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, originalKey);
      byte[] cipherText = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(cipherText);

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | NoSuchPaddingException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String decrypt(String data, byte[] encodedKey) {
    try {
      SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, originalKey);
      byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(data));
      return new String(plainText);

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String decrypt(String data, @NotNull String base64key) {
    try {
      byte[] encodedKey = Base64.getDecoder().decode(base64key);
      SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, originalKey);
      byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(data));
      return new String(plainText);

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] decryptToByteFromBase64(String data, @NotNull SecretKey originalKey)
      throws IllegalBlockSizeException {
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, originalKey);
      byte[] decoded_data = cipher.doFinal(Base64.getDecoder().decode(data));
      return decoded_data;

    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | BadPaddingException
        | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }
}
