package com.cl3t4p.TinyNode.tools;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.jetbrains.annotations.NotNull;

/**
 * AESTools - A utility class for AES encryption and decryption.
 *
 * <p>This class provides methods to encrypt and decrypt data using AES algorithm with a given key.
 * It supports encryption and decryption using byte arrays, base64 encoded keys, and SecretKey
 * objects.
 */
public class AESTools {


  public static String encryptFromByteToBase64(byte[] data,@NotNull SecretKey key, IvParameterSpec ivParam) {
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      cipher.init(Cipher.ENCRYPT_MODE, key,ivParam);
      byte[] cipherText = cipher.doFinal(data);
      byte[] full_msg = new byte[data.length + ivParam.getIV().length];
      //Add iv to the first
      System.arraycopy(ivParam.getIV(), 0, full_msg, 0, ivParam.getIV().length);
      //Add encrypted data
      System.arraycopy(cipherText, 0, full_msg, ivParam.getIV().length, cipherText.length);
      return Base64.getEncoder().encodeToString(full_msg);

    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
      //TODO Handle stuff here
      throw new RuntimeException(e);
    }
  }


  public static byte[] decryptFromBase64ToByte(String data, @NotNull SecretKey originalKey,IvParameterSpec ivParam)
          throws IllegalBlockSizeException
  {
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      cipher.init(Cipher.DECRYPT_MODE, originalKey ,ivParam);
      System.out.println(data);
      return cipher.doFinal(Base64.getDecoder().decode(data));

    } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException |
             InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }


  public static IvParameterSpec getRandomIv() {
    SecureRandom randomSecureRandom = new SecureRandom();
    byte[] iv = new byte[16];
    randomSecureRandom.nextBytes(iv);
    return new IvParameterSpec(iv);
  }
}
