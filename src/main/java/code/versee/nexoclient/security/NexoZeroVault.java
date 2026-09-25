package code.versee.nexoclient.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class NexoZeroVault {
    // Machine specific local key (Server ko yeh key KABHI pata nahi chalegi)
    private static SecretKey getClientOnlyKey() throws Exception {
        String hwid = NexoHWID.getHWID();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(hwid.toCharArray(), "Nexo_Client_Local_Only_Salt#99".getBytes(StandardCharsets.UTF_8), 100000, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // Client par hi data seal (encrypt) hota hai
    public static String sealData(String plainJson) throws Exception {
        SecretKey key = getClientOnlyKey();
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] cipherText = cipher.doFinal(plainJson.getBytes(StandardCharsets.UTF_8));

        ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
        buffer.put(iv);
        buffer.put(cipherText);
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    // Sirf yahi machine is data ko unseal (decrypt) kar sakti hai
    public static String unsealData(String encryptedBase64) {
        try {
            SecretKey key = getClientOnlyKey();
            byte[] raw = Base64.getDecoder().decode(encryptedBase64);
            ByteBuffer buffer = ByteBuffer.wrap(raw);
            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null; // Decryption impossible on any other device!
        }
    }

    // Server authentication ke liye SHA-256 one-way public identifier
    public static String getPublicAccountHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((NexoHWID.getHWID() + "_nexo_public_id").getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
}