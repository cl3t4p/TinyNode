package com.cl3t4p.TinyNode.tools;

import com.goterl.lazysodium.LazySodium;
import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.interfaces.AEAD;
import com.goterl.lazysodium.utils.LibraryLoader;
import java.util.Base64;
import org.jetbrains.annotations.NotNull;

/**
 * AESTools - A utility class for AES encryption and decryption.
 *
 * <p>This class provides methods to encrypt and decrypt data using AES algorithm with a given key.
 * It supports encryption and decryption using byte arrays, base64 encoded keys, and SecretKey
 * objects.
 */
public class AESTools {

  private static final LazySodium LS =
      new LazySodiumJava(new SodiumJava(LibraryLoader.Mode.PREFER_SYSTEM));

  public static String encryptFromByteToBase64(byte[] data, @NotNull byte[] key) {
    byte[] nonce = LS.nonce(AEAD.CHACHA20POLY1305_IETF_NPUBBYTES);
    byte[] encrypted_msg = new byte[data.length + AEAD.CHACHA20POLY1305_IETF_ABYTES];
    long[] length = new long[1];

    LS.cryptoAeadChaCha20Poly1305IetfEncrypt(
        encrypted_msg, length, data, data.length, null, 0, null, nonce, key);

    byte[] full_msg = new byte[encrypted_msg.length + nonce.length];
    System.arraycopy(encrypted_msg, 0, full_msg, 0, encrypted_msg.length);
    System.arraycopy(nonce, 0, full_msg, encrypted_msg.length, nonce.length);
    return Base64.getEncoder().encodeToString(full_msg);
  }

  public static byte[] decryptFromBase64ToByte(String data, @NotNull byte[] key)
      throws SodiumException {
    byte[] encrypted_with_nonce = Base64.getDecoder().decode(data);
    byte[] nonce = new byte[AEAD.CHACHA20POLY1305_IETF_NPUBBYTES];

    int encrypted_data_len = encrypted_with_nonce.length - nonce.length;
    System.arraycopy(encrypted_with_nonce, encrypted_data_len, nonce, 0, nonce.length);

    if (encrypted_data_len - AEAD.CHACHA20POLY1305_IETF_ABYTES <= 0) {
      throw new SodiumException("There is no data in the encrypted message");
    }
    byte[] plain_msg = new byte[encrypted_data_len - AEAD.CHACHA20POLY1305_IETF_ABYTES];
    long[] plain_msg_len = new long[1];
    boolean result =
        LS.cryptoAeadChaCha20Poly1305IetfDecrypt(
            plain_msg,
            plain_msg_len,
            null,
            encrypted_with_nonce,
            encrypted_data_len,
            null,
            0,
            nonce,
            key);
    if (!result) {
      throw new SodiumException("Error while decrypting...");
    }
    return plain_msg;
  }

  public static byte[] generatePrivateKey() {
    return LS.keygen(AEAD.Method.CHACHA20_POLY1305_IETF).getAsBytes();
  }
}
