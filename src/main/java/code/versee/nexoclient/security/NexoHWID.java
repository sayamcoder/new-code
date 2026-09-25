package code.versee.nexoclient.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class NexoHWID {
    private static String cachedHWID = null;

    public static String getHWID() {
        if (cachedHWID != null) return cachedHWID;

        try {
            String rawId = System.getProperty("os.name") +
                    System.getProperty("os.arch") +
                    System.getProperty("user.name") +
                    System.getenv("PROCESSOR_IDENTIFIER") +
                    System.getenv("COMPUTERNAME") +
                    Runtime.getRuntime().availableProcessors();

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(rawId.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            cachedHWID = hex.toString();
        } catch (Exception e) {
            cachedHWID = "nexo_fallback_hwid_fallback_key";
        }
        return cachedHWID;
    }
}