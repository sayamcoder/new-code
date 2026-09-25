package code.versee.nexoclient.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.nio.file.Files;

public class NexoVault {
    private static final File VAULT_FILE = new File(Minecraft.getInstance().gameDirectory, "nexo_vault.bin");
    private static JsonObject cache = new JsonObject();

    static {
        loadVault();
    }

    public static synchronized void set(String key, String value) {
        cache.addProperty(key, value);
        saveVault();
    }

    public static synchronized String get(String key, String defaultValue) {
        if (cache.has(key)) {
            return cache.get(key).getAsString();
        }
        return defaultValue;
    }

    private static void saveVault() {
        try {
            String encryptedData = NexoCrypto.encrypt(cache.toString());
            Files.writeString(VAULT_FILE.toPath(), encryptedData);
        } catch (Exception ignored) {}
    }

    private static void loadVault() {
        try {
            if (VAULT_FILE.exists()) {
                String encryptedData = Files.readString(VAULT_FILE.toPath());
                String decryptedJson = NexoCrypto.decrypt(encryptedData);
                if (decryptedJson != null) {
                    cache = JsonParser.parseString(decryptedJson).getAsJsonObject();
                } else {
                    // HWID Mismatch! Unauthorized Machine.
                    cache = new JsonObject();
                }
            }
        } catch (Exception e) {
            cache = new JsonObject();
        }
    }
}