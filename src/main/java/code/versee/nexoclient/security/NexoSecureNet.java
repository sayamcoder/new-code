package code.versee.nexoclient.security;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NexoSecureNet {

    public static String sendEncryptedPayload(String routePath, String plainJson) throws Exception {
        String fullUrl = NexoConfig.BACKEND_API_URL + routePath;
        URL url = new URI(fullUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);

        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();
        String hwid = NexoHWID.getHWID();

        // 1. AES-256-GCM Encryption
        String encryptedPayload = NexoCrypto.encrypt(plainJson);

        // 2. Cryptographic Signature
        String dataToSign = timestamp + ":" + nonce + ":" + hwid + ":" + encryptedPayload;
        String signature = NexoCrypto.generateSignature(dataToSign, NexoConfig.API_SECRET_KEY);

        // 3. Attach Anti-Tamper Headers
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("X-Nexo-Timestamp", String.valueOf(timestamp));
        conn.setRequestProperty("X-Nexo-Nonce", nonce);
        conn.setRequestProperty("X-Nexo-HWID", hwid);
        conn.setRequestProperty("X-Nexo-Signature", signature);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(encryptedPayload.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status == 200) {
            try (var reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                StringBuilder response = new StringBuilder();
                char[] buf = new char[1024];
                int len;
                while ((len = reader.read(buf)) != -1) {
                    response.append(buf, 0, len);
                }
                // Decrypt backend response
                return NexoCrypto.decrypt(response.toString().trim());
            }
        } else {
            throw new RuntimeException("Cloud rejected connection: HTTP " + status);
        }
    }
}