package code.versee.nexoclient.ui.screens;

import code.versee.nexoclient.gui.NexoPartnersScreen;
import code.versee.nexoclient.ui.components.NexoTextButton;
import code.versee.nexoclient.util.NexoTextureHelper;
import code.versee.nexoclient.util.SkinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.Desktop;

public class NexoPauseScreen extends Screen {
    private static final Identifier DEFAULT_SKIN = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");

    public NexoPauseScreen() {
        super(Component.literal("Game Menu"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int btnW = 190;
        int btnH = 22;
        int startY = centerY - 42;
        int gap = 26;

        // 1. Clean Minimal Center Stack (NO STORE, Partners Included)
        this.addRenderableWidget(new NexoTextButton(centerX - btnW / 2, startY, btnW, btnH, Component.literal("< Back to Game"), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(null);
        }));

        this.addRenderableWidget(new NexoTextButton(centerX - btnW / 2, startY + gap, btnW, btnH, Component.literal("Partners"), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new NexoPartnersScreen(this));
        }));

        this.addRenderableWidget(new NexoTextButton(centerX - btnW / 2, startY + (gap * 2), btnW, btnH, Component.literal("Options"), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));

        this.addRenderableWidget(new NexoTextButton(centerX - btnW / 2, startY + (gap * 3), btnW, btnH, Component.literal("Save and Quit to Title"), false, () -> {
            if (this.minecraft != null) {
                this.minecraft.disconnect(new NexoMainMenuScreen(), false);
            }
        }));

        // 2. Minimal Bottom-Right Pills
        int miniY = this.height - 26;
        int miniW = 80;
        int miniGap = 84;
        int miniStartX = this.width - (miniGap * 3) - 16;

        this.addRenderableWidget(new NexoTextButton(miniStartX, miniY, miniW, 18, Component.literal("Advancements"), false, () -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.gui.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
            }
        }));

        this.addRenderableWidget(new NexoTextButton(miniStartX + miniGap, miniY, miniW, 18, Component.literal("Statistics"), false, () -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.gui.setScreen(new net.minecraft.client.gui.screens.achievement.StatsScreen(this, this.minecraft.player.getStats()));
            }
        }));

        this.addRenderableWidget(new NexoTextButton(miniStartX + (miniGap * 2), miniY, miniW, 18, Component.literal("Folder"), false, () -> {
            try {
                if (this.minecraft != null && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(this.minecraft.gameDirectory);
                }
            } catch (Exception ignored) {}
        }));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Subtle dark backdrop blur (no blue wash)
        graphics.fill(0, 0, this.width, this.height, 0xA6080B10);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int titleY = centerY - 76;

        // Branding Logo
        Identifier logo = NexoTextureHelper.getLogo();
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, logo, centerX - 42, titleY, 0.0F, 0.0F, 20, 20, 256, 256);
        } catch (Exception e) {
            graphics.fill(centerX - 42, titleY + 2, centerX - 26, titleY + 18, 0x44FFFFFF);
        }
        graphics.text(this.font, Component.literal("NEXO CLIENT"), centerX - 16, titleY + 6, 0xFFE2E8F0, true);

        // Top-Right Clean Minimal Profile Card (No neon/cyber dots)
        String username = (this.minecraft != null && this.minecraft.getUser() != null) ? this.minecraft.getUser().getName() : "Player";
        int nameW = this.font.width(username);
        int pillW = 28 + nameW + 14;
        int pillX = this.width - pillW - 20;
        int pillY = 16;

        graphics.fill(pillX, pillY, pillX + pillW, pillY + 24, 0x1AFFFFFF);
        graphics.fill(pillX + 1, pillY + 1, pillX + pillW - 1, pillY + 23, 0xF00D111A);
        Identifier skin = SkinHelper.getSkin(username);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin != null ? skin : DEFAULT_SKIN, pillX + 4, pillY + 4, 8, 8, 16, 16, 8, 8, 64, 64);
        graphics.text(this.font, Component.literal(username), pillX + 26, pillY + 8, 0xFFE2E8F0, true);

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}