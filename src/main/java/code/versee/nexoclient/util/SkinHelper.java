package code.versee.nexoclient.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SkinHelper {
    private static final Map<String, Identifier> SKIN_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> PENDING_DOWNLOADS = ConcurrentHashMap.newKeySet();
    private static final Identifier DEFAULT_STEVE = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");

    public static Identifier getSkin(String username) {
        if (username == null || username.trim().isEmpty()) {
            return DEFAULT_STEVE;
        }

        String cleanName = username.trim().toLowerCase();
        if (SKIN_CACHE.containsKey(cleanName)) {
            return SKIN_CACHE.get(cleanName);
        }

        if (!PENDING_DOWNLOADS.contains(cleanName)) {
            PENDING_DOWNLOADS.add(cleanName);
            fetchSkinAsync(username.trim());
        }

        return DEFAULT_STEVE;
    }

    private static void fetchSkinAsync(String username) {
        new Thread(() -> {
            InputStream is = null;
            try {
                URL url = new URI("https://minotar.net/skin/" + username).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "NexoClient/1.0");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);

                is = conn.getInputStream();
                BufferedImage img = ImageIO.read(is);

                if (img != null) {
                    int w = img.getWidth();
                    int h = img.getHeight();

                    NativeImage nativeImage = new NativeImage(w, h, false);
                    for (int y = 0; y < h; y++) {
                        for (int x = 0; x < w; x++) {
                            int argb = img.getRGB(x, y);
                            int a = (argb >> 24) & 0xFF;
                            int r = (argb >> 16) & 0xFF;
                            int g = (argb >> 8) & 0xFF;
                            int b = argb & 0xFF;
                            int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                            nativeImage.setPixel(x, y, abgr);
                        }
                    }

                    Minecraft.getInstance().execute(() -> {
                        DynamicTexture texture = new DynamicTexture(() -> "skin_" + username.toLowerCase(), nativeImage);
                        Identifier skinId = Identifier.fromNamespaceAndPath("nexoclient", "textures/skins/" + username.toLowerCase());
                        Minecraft.getInstance().getTextureManager().register(skinId, texture);
                        SKIN_CACHE.put(username.toLowerCase(), skinId);
                    });
                }
            } catch (Exception ignored) {
            } finally {
                PENDING_DOWNLOADS.remove(username.toLowerCase());
                if (is != null) {
                    try { is.close(); } catch (Exception ignored) {}
                }
            }
        }, "Nexo-SkinLoader-" + username).start();
    }
}