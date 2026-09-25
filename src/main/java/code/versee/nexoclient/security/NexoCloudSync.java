package code.versee.nexoclient.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NexoCloudSync {

    public static boolean uploadToCloud(JsonObject userData) {
        try {
            String encryptedBlob = NexoZeroVault.sealData(userData.toString());
            String publicHash = NexoZeroVault.getPublicAccountHash();

            JsonObject payload = new JsonObject();
            payload.addProperty("accountId", publicHash);
            payload.addProperty("encryptedVault", encryptedBlob);

            String response = postRequest("/api/vault/push", payload.toString());
            return response != null && response.contains("SUCCESS");
        } catch (Exception e) {
            return false;
        }
    }

    public static JsonObject fetchFromCloud() {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("accountId", NexoZeroVault.getPublicAccountHash());

            String response = postRequest("/api/vault/pull", req.toString());
            if (response != null) {
                JsonObject resObj = JsonParser.parseString(response).getAsJsonObject();
                if (resObj.has("encryptedVault") && !resObj.get("encryptedVault").isJsonNull()) {
                    String encryptedBlob = resObj.get("encryptedVault").getAsString();
                    String decryptedJson = NexoZeroVault.unsealData(encryptedBlob);
                    if (decryptedJson != null) {
                        return JsonParser.parseString(decryptedJson).getAsJsonObject();
                    }
                }
            }
        } catch (Exception ignored) {}
        return new JsonObject();
    }

    private static String postRequest(String endpoint, String jsonBody) {
        try {
            // Points to Bonto DB Server
            URL url = new URI(NexoConfig.DB_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-nexo-auth", NexoConfig.API_SECRET_KEY);
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (var reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    StringBuilder sb = new StringBuilder();
                    char[] buf = new char[1024];
                    int read;
                    while ((read = reader.read(buf)) != -1) sb.append(buf, 0, read);
                    return sb.toString();
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}