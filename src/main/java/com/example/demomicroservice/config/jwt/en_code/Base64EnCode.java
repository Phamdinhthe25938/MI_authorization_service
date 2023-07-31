package com.example.demomicroservice.config.jwt.en_code;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Service
public class Base64EnCode {

  public String decrypt(String encryptedBase64, String header) {
    try {
      // Split the encrypted Base64 string and the AES key
      String[] parts = encryptedBase64.split(":");
      String encryptedString = parts[0];
      byte[] secretKeyBytes = Base64.getDecoder().decode(parts[1]);

      // Decrypt the Base64-encoded bytes using the AES key
      byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
      SecretKeySpec key = new SecretKeySpec(secretKeyBytes, "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

      // Return the decrypted string
      return new String(decryptedBytes);
    } catch (Exception e) {
      return null;
    }
  }


}
