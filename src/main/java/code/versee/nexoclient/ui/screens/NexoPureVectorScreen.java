package code.versee.nexoclient.ui.screens;

import code.versee.nexoclient.ui.components.NexoTextButton;
import code.versee.nexoclient.util.NexoTextureHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Panorama;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class NexoPureVectorScreen extends Screen {
    private final Panorama panorama = new Panorama();

    public NexoPureVectorScreen() {
        super(Component.literal("Pure Vector Menu"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        this.panorama.extractRenderState(graphics, this.width, this.height);
        graphics.fill(0, 0, this.width, this.height, 0x4D060910);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // 1. Logo
        Identifier logo = NexoTextureHelper.getLogo();
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, logo, cx - 28, cy - 95, 0.0F, 0.0F, 56, 56, 56, 56);
        } catch (Exception ignored) {}

        // 2. Center Rounded Pills
        drawVectorPill(graphics, cx - 105, cy - 12, 210, 24, 0xDD0D111A, 0x22FFFFFF, "👤  Singleplayer", 0xFFFFFFFF);
        drawVectorPill(graphics, cx - 105, cy + 15, 210, 24, 0xDD0D111A, 0x22FFFFFF, "🌐  Multiplayer", 0xFFFFFFFF);

        // Split Row: Discover + Emerald Store
        drawVectorPill(graphics, cx - 105, cy + 42, 102, 24, 0xDD0D111A, 0x22FFFFFF, "Partners", 0xFFFFFFFF);
        drawVectorPill(graphics, cx + 3, cy + 42, 102, 24, 0xDD062D24, 0xFF10B981, "🛒  Store", 0xFF34D399);

        // 3. Floating Bottom Dock
        drawVectorPill(graphics, cx - 95, this.height - 36, 190, 26, 0xF20B0E17, 0x26FFFFFF, "🌙   👕   👥   ⚙   📁   ✕", 0xFF94A3B8);

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    // Procedural Rounded Vector Geometry
    private void drawVectorPill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int bg, int border, String text, int textColor) {
        // Base Box with Cut-Corners (Anti-aliased simulation)
        graphics.fill(x + 2, y + 1, x + w - 2, y + h - 1, bg);
        graphics.fill(x + 1, y + 2, x + w - 1, y + h - 2, bg);

        // Subtle 1px borders
        graphics.fill(x + 2, y, x + w - 2, y + 1, border);
        graphics.fill(x + 2, y + h - 1, x + w - 2, y + h, border);
        graphics.fill(x, y + 2, x + 1, y + h - 2, border);
        graphics.fill(x + w - 1, y + 2, x + w, y + h - 2, border);

        int tx = x + (w - this.font.width(text)) / 2;
        graphics.text(this.font, Component.literal(text), tx, y + (h - 8) / 2, textColor, true);
    }
}