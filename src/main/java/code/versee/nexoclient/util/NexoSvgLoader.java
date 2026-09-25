package code.versee.nexoclient.util;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NexoSvgLoader {
    private static final Map<String, Identifier> SVG_CACHE = new ConcurrentHashMap<>();

    public static Identifier loadSvg(String relativePath, int width, int height) {
        String cacheKey = relativePath + "_" + width + "x" + height;
        if (SVG_CACHE.containsKey(cacheKey)) {
            return SVG_CACHE.get(cacheKey);
        }

        try (InputStream is = NexoSvgLoader.class.getClassLoader().getResourceAsStream("assets/nexoclient/" + relativePath)) {
            if (is != null) {
                SVGLoader loader = new SVGLoader();
                SVGDocument document = loader.load(is);

                if (document != null) {
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // 🟢 Real Vector ViewBox Scaling (0 Errors)
                    document.render(null, g2d, new ViewBox(0, 0, width, height));
                    g2d.dispose();

                    NativeImage nativeImage = new NativeImage(width, height, false);
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int argb = image.getRGB(x, y);
                            int a = (argb >> 24) & 0xFF;
                            int r = (argb >> 16) & 0xFF;
                            int g = (argb >> 8) & 0xFF;
                            int b = argb & 0xFF;
                            int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                            nativeImage.setPixel(x, y, abgr);
                        }
                    }

                    Identifier dynamicId = Identifier.fromNamespaceAndPath("nexoclient", "svg_" + cacheKey.toLowerCase().replaceAll("[^a-z0-9_]", "_"));
                    Minecraft.getInstance().getTextureManager().register(dynamicId, new DynamicTexture(dynamicId::toString, nativeImage));
                    SVG_CACHE.put(cacheKey, dynamicId);
                    return dynamicId;
                }
            }
        } catch (Exception ignored) {}

        return Identifier.fromNamespaceAndPath("nexoclient", relativePath);
    }
}