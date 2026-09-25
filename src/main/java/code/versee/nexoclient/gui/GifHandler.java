package code.versee.nexoclient.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GifHandler {
    private static final List<Identifier> frames = new ArrayList<>();
    private static int currentFrameIndex = 0;
    private static long lastFrameTime = 0;
    private static boolean loadingStarted = false;
    private static final int FRAME_DELAY_MS = 60; // Smooth frame speed

    public static void loadGif() {
        if (loadingStarted) return;
        loadingStarted = true;

        new Thread(() -> {
            InputStream is = null;
            try {
                // 1. Resource dhoondein
                Identifier gifId = Identifier.fromNamespaceAndPath("nexoclient", "textures/gui/background.gif");
                var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(gifId);
                if (resourceOpt.isPresent()) {
                    is = resourceOpt.get().open();
                }

                if (is == null) {
                    is = GifHandler.class.getResourceAsStream("/assets/nexoclient/textures/gui/background.gif");
                }

                if (is == null) {
                    System.out.println("[NexoClient] background.gif nahi mila!");
                    return;
                }

                ImageInputStream stream = ImageIO.createImageInputStream(is);
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
                if (!readers.hasNext()) return;

                ImageReader reader = readers.next();
                reader.setInput(stream);

                int count = reader.getNumImages(true);
                System.out.println("[NexoClient] Decoding " + count + " GIF frames cleanly...");

                // Persistent canvas taake 8-bit GIF ke transparent frame artifacts khatam hon
                BufferedImage masterCanvas = null;
                Graphics2D g2d = null;

                for (int i = 0; i < count; i++) {
                    BufferedImage rawFrame = reader.read(i);
                    int w = rawFrame.getWidth();
                    int h = rawFrame.getHeight();

                    if (masterCanvas == null) {
                        masterCanvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        g2d = masterCanvas.createGraphics();
                    }

                    // Frame ko canvas par draw karein (Glitch aur transparent pixels fix karne ke liye)
                    g2d.drawImage(rawFrame, 0, 0, null);

                    @SuppressWarnings("resource")
                    NativeImage nativeImage = new NativeImage(w, h, false);
                    for (int y = 0; y < h; y++) {
                        for (int x = 0; x < w; x++) {
                            int argb = masterCanvas.getRGB(x, y);
                            // Minecraft 26.x ABGR format
                            int a = (argb >> 24) & 0xFF;
                            int r = (argb >> 16) & 0xFF;
                            int g = (argb >> 8) & 0xFF;
                            int b = argb & 0xFF;
                            int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                            nativeImage.setPixel(x, y, abgr);
                        }
                    }

                    int frameIdx = i;
                    Minecraft.getInstance().execute(() -> {
                        DynamicTexture texture = new DynamicTexture(() -> "nexo_gif_" + frameIdx, nativeImage);
                        Identifier id = Identifier.fromNamespaceAndPath("nexoclient", "textures/gui/gif_frame_" + frameIdx);
                        Minecraft.getInstance().getTextureManager().register(id, texture);
                        frames.add(id);
                    });
                }

                if (g2d != null) g2d.dispose();
                System.out.println("[NexoClient] GIF bilkul clean load ho gaya!");

            } catch (Exception e) {
                System.out.println("[NexoClient] GIF Error: " + e.getMessage());
            } finally {
                if (is != null) {
                    try { is.close(); } catch (Exception ignored) {}
                }
            }
        }, "NexoClient-GifLoader").start();
    }

    public static Identifier getCurrentFrame() {
        if (frames.isEmpty()) return null;

        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= FRAME_DELAY_MS) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            lastFrameTime = now;
        }

        return frames.get(currentFrameIndex);
    }
}