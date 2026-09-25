package code.versee.nexoclient.ui.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class NexoRenderUtils {

    // 💎 Standard Dark Glass Card (Used by ModMenu and Cards)
    public static void drawGlassCard(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int bgColor, int borderColor) {
        graphics.fill(x, y, x + w, y + h, borderColor);
        graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, bgColor);
    }

    // 💎 Sub-Pixel Anti-Aliased Rounded Pill
    public static void drawSmoothPill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int bg, int border, boolean hovered) {
        // Drop shadow
        int shadow = hovered ? 0x44000000 : 0x26000000;
        graphics.fill(x + 3, y + h, x + w - 3, y + h + 2, shadow);
        graphics.fill(x + 6, y + h + 2, x + w - 6, y + h + 4, shadow & 0x1AFFFFFF);

        // Core Glass Body
        graphics.fill(x + 3, y + 1, x + w - 3, y + h - 1, bg);
        graphics.fill(x + 1, y + 3, x + w - 1, y + h - 3, bg);
        graphics.fill(x + 2, y + 2, x + w - 2, y + h - 2, bg);

        // Borders
        graphics.fill(x + 3, y, x + w - 3, y + 1, border);
        graphics.fill(x + 3, y + h - 1, x + w - 3, y + h, border);
        graphics.fill(x, y + 3, x + 1, y + h - 3, border);
        graphics.fill(x + w - 1, y + 3, x + w, y + h - 3, border);

        // Corner blending
        int blend = (border & 0x00FFFFFF) | 0x40000000;
        graphics.fill(x + 1, y + 1, x + 3, y + 2, blend);
        graphics.fill(x + 1, y + 2, x + 2, y + 3, blend);
        graphics.fill(x + w - 3, y + 1, x + w - 1, y + 2, blend);
        graphics.fill(x + w - 2, y + 2, x + w - 1, y + 3, blend);
        graphics.fill(x + 1, y + h - 2, x + 3, y + h - 1, blend);
        graphics.fill(x + 1, y + h - 3, x + 2, y + h - 2, blend);
        graphics.fill(x + w - 3, y + h - 2, x + w - 1, y + h - 1, blend);
        graphics.fill(x + w - 2, y + h - 3, x + w - 1, y + h - 2, blend);
    }

    // 🟢 Emerald Green Store Button
    public static void drawStorePill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? 0xF0064E3B : 0xE6062D24;
        int border = hovered ? 0xFF10B981 : 0x8010B981;

        if (hovered) {
            graphics.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0x2210B981);
        }

        drawSmoothPill(graphics, x, y, w, h, bg, border, hovered);

        Minecraft mc = Minecraft.getInstance();
        int tx = x + (w - mc.font.width("🛒  Store")) / 2;
        graphics.text(mc.font, Component.literal("🛒  Store"), tx, y + (h - 8) / 2, 0xFF34D399, true);
    }

    // 🏷️ Partners Button with `1371` Pill Badge
    public static void drawPartnersPill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? 0xF01E293B : 0xE60D111A;
        int border = hovered ? 0x66FFFFFF : 0x22FFFFFF;

        drawSmoothPill(graphics, x, y, w, h, bg, border, hovered);

        Minecraft mc = Minecraft.getInstance();
        graphics.text(mc.font, Component.literal("Partners"), x + 12, y + (h - 8) / 2, 0xFFF8FAFC, true);

        int badgeX = x + w - 36;
        int badgeY = y + (h - 14) / 2;
        graphics.fill(badgeX + 1, badgeY, badgeX + 27, badgeY + 14, 0x3338BDF8);
        graphics.fill(badgeX, badgeY + 1, badgeX + 28, badgeY + 13, 0x3338BDF8);
        graphics.text(mc.font, Component.literal("1371"), badgeX + 4, badgeY + 3, 0xFF38BDF8, false);
    }

    public static int lerpColor(int c1, int c2, float t) {
        t = Math.clamp(t, 0.0f, 1.0f);
        int a1 = (c1 >> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int a2 = (c2 >> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}