package code.versee.nexoclient.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NexoTextureHelper {
    private static final Map<String, Identifier> TEXTURE_CACHE = new ConcurrentHashMap<>();

    public static Identifier getLogo() {
        return getTexture("textures/gui/logo.png", "nexo_logo_dynamic");
    }

    public static Identifier getZapNodesBanner() {
        return getTexture("textures/gui/zapnodes_banner.png", "zapnodes_banner_dynamic");
    }

    public static Identifier getTexture(String resourcePath, String dynamicKey) {
        if (TEXTURE_CACHE.containsKey(dynamicKey)) {
            return TEXTURE_CACHE.get(dynamicKey);
        }

        String[] paths = {
                "assets/nexoclient/" + resourcePath,
                "/assets/nexoclient/" + resourcePath
        };

        for (String path : paths) {
            try (InputStream is = NexoTextureHelper.class.getClassLoader().getResourceAsStream(path)) {
                if (is != null) {
                    NativeImage img = NativeImage.read(is);
                    Identifier dynamicId = Identifier.fromNamespaceAndPath("nexoclient", "dynamic/" + dynamicKey);
                    Minecraft.getInstance().getTextureManager().register(dynamicId, new DynamicTexture(dynamicId::toString, img));
                    TEXTURE_CACHE.put(dynamicKey, dynamicId);
                    return dynamicId;
                }
            } catch (Exception ignored) {}
        }

        Identifier fallback = Identifier.fromNamespaceAndPath("nexoclient", resourcePath);
        TEXTURE_CACHE.put(dynamicKey, fallback);
        return fallback;
    }
}