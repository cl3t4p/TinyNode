package com.cl3t4p.TinyNode.devices;

import com.cl3t4p.TinyNode.tools.AESTools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


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


    public byte[] getAes_key() {
        return Base64.getDecoder().decode(aes_key);
    }

    /**
     * Encrypts the given data using the AES algorithm and the device's key.
     * @param data The data to encrypt.
     * @return The encrypted data as a base64 encoded string.
     */
    public String encrypt(String data) {
        return AESTools.encrypt(data, getAes_key());
    }

    
    /**
     * Decrypts the given data using the AES algorithm and the device's key.
     * @param data The data to decrypt.
     * @return The decrypted data as a string.
     */
    public String decrypt(String data) {
        return AESTools.decrypt(data, getAes_key());
    }
}
