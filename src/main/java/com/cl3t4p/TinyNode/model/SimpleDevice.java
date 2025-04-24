package com.cl3t4p.TinyNode.model;

import com.cl3t4p.TinyNode.tools.AESTools;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleDevice {

  private String id;
  private String name;
  private String aes_key;

  public SimpleDevice(String id) throws NoSuchAlgorithmException {
    this.id = id;
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256);
    SecretKey secretKey = keyGen.generateKey();
    aes_key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

  public SecretKey getAes_key() {

    return new SecretKeySpec(aes_key.getBytes(), "AES");
  }

  /**
   * Encrypts the given data using the AES algorithm and the device's key.
   *
   * @param data The data to encrypt.
   * @return The encrypted data as a base64 encoded string.
   */
  public String encrypt(String data, IvParameterSpec iv) {
    return AESTools.encryptFromByteToBase64(data.getBytes(),getAes_key(),iv);
  }

  /**
   * Decrypts the given data using the AES algorithm and the device's key.
   *
   * @param data The data to decrypt.
   * @return The decrypted data as a string.
   */
  public String decrypt(String data, IvParameterSpec iv) throws IllegalBlockSizeException {
    return new String(AESTools.decryptFromBase64ToByte(data,getAes_key(),iv));
  }
}
