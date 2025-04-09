package com.cl3t4p.TinyNode.devices;

import com.cl3t4p.TinyNode.tools.AESTools;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.*;
import java.security.NoSuchAlgorithmException;


@Getter
@Setter
public class SimpleDevice {


    private final String id;
    private String name;
    private byte[] encodedKey;


    public SimpleDevice(String id) throws NoSuchAlgorithmException {
        this.id = id;
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        encodedKey = secretKey.getEncoded();
    }



    public String encrypt(String data) {
        return AESTools.encrypt(data, encodedKey);
    }


    public String decrypt(String data) {
        return AESTools.decrypt(data, encodedKey);
    }
}
