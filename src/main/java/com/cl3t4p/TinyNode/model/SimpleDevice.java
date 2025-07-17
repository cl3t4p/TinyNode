package com.cl3t4p.TinyNode.model;

import com.cl3t4p.TinyNode.tools.AESTools;
import com.goterl.lazysodium.exceptions.SodiumException;
import javax.crypto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleDevice {

  private String id;
  private String name;
  private byte[] private_key;

  public SimpleDevice(String id) {
    this.id = id;
    private_key = AESTools.generatePrivateKey();
  }

  /**
   * Encrypts the given data using the AES algorithm and the device's key.
   *
   * @param data The data to encrypt.
   * @return The encrypted data as a base64 encoded string.
   */
  public String encrypt(String data) {
    return AESTools.encryptFromByteToBase64(data.getBytes(), private_key);
  }

  /**
   * Decrypts the given data using the AES algorithm and the device's key.
   *
   * @param data The data to decrypt.
   * @return The decrypted data as a string.
   */
  public String decrypt(String data) throws SodiumException {
    return new String(AESTools.decryptFromBase64ToByte(data, private_key));
  }
}
