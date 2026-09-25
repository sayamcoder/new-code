package code.versee.nexoclient.ui.screens;

import code.versee.nexoclient.mods.ModManager;
import code.versee.nexoclient.ui.components.NexoTextButton;
import code.versee.nexoclient.ui.core.NexoRenderUtils;
import code.versee.nexoclient.util.NexoTextureHelper;
import code.versee.nexoclient.util.SkinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NexoModMenuScreen extends Screen {
    private static final Identifier DEFAULT_SKIN = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");

    private String selectedCategory = "All";
    private EditBox searchBox;
    private final List<NexoTextButton> cardButtons = new ArrayList<>();

    public NexoModMenuScreen() {
        super(Component.literal("Nexo Mod Menu"));
    }

    @Override
    protected void init() {
        super.init();
        this.cardButtons.clear();

        int modalW = 600;
        int modalH = 350;
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // ✕ Close Button
        this.addRenderableWidget(new NexoTextButton(modalX + modalW - 28, modalY + 12, 18, 18, Component.literal("✕"), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(null);
        }));

        // Category Pills (ALL, HUD, PVP, MINING, RENDER)
        String[] categories = {"All", "HUD", "PvP", "Mining", "Render"};
        int catX = modalX + 16;
        int catY = modalY + 44;

        for (String cat : categories) {
            int cW = this.font.width(cat) + 16;
            this.addRenderableWidget(new NexoTextButton(catX, catY, cW, 18, Component.literal(cat), false, () -> {
                this.selectedCategory = cat;
                rebuildCards();
            }));
            catX += cW + 6;
        }

        // Search Bar
        int searchW = 140;
        int searchX = modalX + modalW - searchW - 16;
        this.searchBox = new EditBox(this.font, searchX, catY, searchW, 18, Component.literal("Search"));
        this.searchBox.setHint(Component.literal("Search mods..."));
        this.addRenderableWidget(this.searchBox);

        rebuildCards();
    }

    private void rebuildCards() {
        for (NexoTextButton btn : cardButtons) {
            this.removeWidget(btn);
        }
        cardButtons.clear();

        int modalW = 600;
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - 350) / 2;

        int cardStartX = modalX + 16;
        int cardStartY = modalY + 74;
        int cardW = 132;
        int cardH = 115;

        int col = 0;
        int row = 0;

        for (ModManager.ClientMod mod : ModManager.MODS) {
            if (!selectedCategory.equals("All") && !mod.category().equalsIgnoreCase(selectedCategory)) {
                continue;
            }

            int cx = cardStartX + col * (cardW + 12);
            int cy = cardStartY + row * (cardH + 12);

            // Toggle Button (Green when Enabled, Dark when Disabled)
            boolean enabled = mod.isEnabled().getAsBoolean();
            NexoTextButton toggleBtn = new NexoTextButton(cx + 8, cy + cardH - 26, cardW - 16, 18,
                    Component.literal(enabled ? "Enabled" : "Disabled"), false, () -> {
                mod.toggleAction().run();
                rebuildCards();
            }) {
                @Override
                protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
                    boolean isEn = mod.isEnabled().getAsBoolean();
                    int bg = isEn ? 0xFF10B981 : 0xFF1E2638;
                    int border = isEn ? 0xFF34D399 : 0x22FFFFFF;

                    NexoRenderUtils.drawGlassCard(graphics, this.getX(), this.getY(), this.width, this.height, bg, border);

                    int tx = this.getX() + (this.width - Minecraft.getInstance().font.width(this.getMessage())) / 2;
                    graphics.text(Minecraft.getInstance().font, this.getMessage(), tx, this.getY() + 5, 0xFFFFFFFF, true);
                }
            };

            this.addRenderableWidget(toggleBtn);
            cardButtons.add(toggleBtn);

            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Deep Cinematic Backdrop
        graphics.fill(0, 0, this.width, this.height, 0x88050811);

        int modalW = 600;
        int modalH = 350;
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // Luxury Obsidian Glass Container
        NexoRenderUtils.drawGlassCard(graphics, modalX, modalY, modalW, modalH, 0xF50B0E17, 0x1AFFFFFF);

        // Header: Logo + "MOD MENU"
        Identifier logo = NexoTextureHelper.getLogo();
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, logo, modalX + 16, modalY + 12, 0.0F, 0.0F, 18, 18, 256, 256);
        } catch (Exception ignored) {}
        graphics.text(this.font, Component.literal("MOD MENU"), modalX + 40, modalY + 17, 0xFFF8FAFC, true);

        // Real-Time Calendar/Clock Chip (Screenshot 2 Match)
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd  hh:mm a"));
        graphics.fill(modalX + 124, modalY + 13, modalX + 265, modalY + 29, 0x14FFFFFF);
        graphics.text(this.font, Component.literal("🌙  " + timeStr), modalX + 130, modalY + 17, 0xFF94A3B8, false);

        // Profile Badge (Screenshot 2 Match)
        String user = (this.minecraft != null && this.minecraft.getUser() != null) ? this.minecraft.getUser().getName() : "Player";
        int userW = this.font.width(user);
        int userX = modalX + modalW - userW - 76;
        graphics.fill(userX, modalY + 12, userX + userW + 28, modalY + 30, 0x1AFFFFFF);
        Identifier skin = SkinHelper.getSkin(user);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin != null ? skin : DEFAULT_SKIN, userX + 3, modalY + 14, 8, 8, 14, 14, 8, 8, 64, 64);
        graphics.text(this.font, Component.literal(user), userX + 20, modalY + 17, 0xFFE2E8F0, true);

        // Render Cards
        int cardStartX = modalX + 16;
        int cardStartY = modalY + 74;
        int cardW = 132;
        int cardH = 115;

        int col = 0;
        int row = 0;

        for (ModManager.ClientMod mod : ModManager.MODS) {
            if (!selectedCategory.equals("All") && !mod.category().equalsIgnoreCase(selectedCategory)) {
                continue;
            }

            int cx = cardStartX + col * (cardW + 12);
            int cy = cardStartY + row * (cardH + 12);
            boolean hovered = (mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH);

            int cardBorder = hovered ? 0x44FFFFFF : 0x14FFFFFF;
            NexoRenderUtils.drawGlassCard(graphics, cx, cy, cardW, cardH, 0xF0111624, cardBorder);

            // Mod Name & Category Tag
            graphics.text(this.font, Component.literal(mod.name()), cx + 10, cy + 10, 0xFFFFFFFF, true);
            graphics.text(this.font, Component.literal(mod.category()), cx + 10, cy + 24, 0xFF38BDF8, false);

            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}