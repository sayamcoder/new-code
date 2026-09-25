package code.versee.nexoclient.ui.screens;

import code.versee.nexoclient.gui.NexoPartnersScreen;
import code.versee.nexoclient.ui.components.NexoTextButton;
import code.versee.nexoclient.ui.core.NexoRenderUtils;
import code.versee.nexoclient.util.NexoSvgLoader;
import code.versee.nexoclient.util.NexoTextureHelper;
import code.versee.nexoclient.util.SkinHelper;
import code.versee.nexoclient.util.UserSetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.Panorama;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.Desktop;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NexoMainMenuScreen extends Screen {
    private static final Identifier DEFAULT_SKIN = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
    private static final List<String> SAVED_ACCOUNTS = new ArrayList<>();
    static {
        SAVED_ACCOUNTS.add("MasterNaughty");
    }

    private final Panorama panorama = new Panorama();
    private boolean isDrawerOpen = false;
    private float drawerAnim = 0.0f;
    private static final int DRAWER_WIDTH = 290;

    private boolean showAddInput = false;
    private EditBox offlineInput;
    private NexoTextButton addAccountBtn;

    private final List<NexoTextButton> centerButtons = new ArrayList<>();
    private final List<NexoTextButton> dockButtons = new ArrayList<>();
    private final List<NexoTextButton> drawerButtons = new ArrayList<>();
    private NexoTextButton backdropCloser;
    private NexoTextButton zapNodesBannerBtn;
    private NexoTextButton topProfileBtn;

    public NexoMainMenuScreen() {
        super(Component.literal("NexoClient Main Menu"));
    }

    @Override
    protected void init() {
        super.init();
        this.centerButtons.clear();
        this.dockButtons.clear();
        this.drawerButtons.clear();

        String currentUser = (this.minecraft != null && this.minecraft.getUser() != null)
                ? this.minecraft.getUser().getName()
                : "Player";

        if (!SAVED_ACCOUNTS.contains(currentUser)) {
            SAVED_ACCOUNTS.addFirst(currentUser);
        }
        SkinHelper.getSkin(currentUser);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuW = 220;
        int menuH = 34;
        int startY = centerY - 16;
        int gap = 40;

        // 1. Center Buttons (Singleplayer / Multiplayer / Partners / Store)
        NexoTextButton spBtn = new NexoTextButton(centerX - menuW / 2, startY, menuW, menuH, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new SelectWorldScreen(this));
        });
        this.addRenderableWidget(spBtn);
        this.centerButtons.add(spBtn);

        NexoTextButton mpBtn = new NexoTextButton(centerX - menuW / 2, startY + gap, menuW, menuH, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new JoinMultiplayerScreen(this));
        });
        this.addRenderableWidget(mpBtn);
        this.centerButtons.add(mpBtn);

        int splitW = (menuW - 8) / 2;
        int splitY = startY + (gap * 2);

        NexoTextButton discoverBtn = new NexoTextButton(centerX - menuW / 2, splitY, splitW, menuH, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new NexoPartnersScreen(this));
        });
        this.addRenderableWidget(discoverBtn);
        this.centerButtons.add(discoverBtn);

        NexoTextButton storeBtn = new NexoTextButton(centerX - menuW / 2 + splitW + 8, splitY, splitW, menuH, Component.empty(), false, () -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("https://nexoclient-api.bonto.run"));
                }
            } catch (Exception ignored) {}
        });
        this.addRenderableWidget(storeBtn);
        this.centerButtons.add(storeBtn);

        // 2. Bottom Floating Dock (Lunar Style)
        int dockW = 210;
        int dockH = 32;
        int dockX = centerX - dockW / 2;
        int dockY = this.height - 42;
        int itemSize = 26;
        int itemStep = 34;

        NexoTextButton dMods = new NexoTextButton(dockX + 6, dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new NexoModMenuScreen());
        });
        this.addRenderableWidget(dMods);
        this.dockButtons.add(dMods);

        NexoTextButton dCosmetics = new NexoTextButton(dockX + 6 + itemStep, dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new NexoPartnersScreen(this));
        });
        this.addRenderableWidget(dCosmetics);
        this.dockButtons.add(dCosmetics);

        NexoTextButton dAccounts = new NexoTextButton(dockX + 6 + (itemStep * 2), dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            this.isDrawerOpen = true;
            updateWidgetStates();
        });
        this.addRenderableWidget(dAccounts);
        this.dockButtons.add(dAccounts);

        NexoTextButton dSettings = new NexoTextButton(dockX + 6 + (itemStep * 3), dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(new OptionsScreen(this, this.minecraft.options));
        });
        this.addRenderableWidget(dSettings);
        this.dockButtons.add(dSettings);

        NexoTextButton dFolder = new NexoTextButton(dockX + 6 + (itemStep * 4), dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            try {
                if (this.minecraft != null && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(this.minecraft.gameDirectory);
                }
            } catch (Exception ignored) {}
        });
        this.addRenderableWidget(dFolder);
        this.dockButtons.add(dFolder);

        NexoTextButton dQuit = new NexoTextButton(dockX + 6 + (itemStep * 5), dockY + 3, itemSize, itemSize, Component.empty(), false, () -> {
            if (this.minecraft != null) this.minecraft.stop();
        });
        this.addRenderableWidget(dQuit);
        this.dockButtons.add(dQuit);

        // 3. Top-Right Profile Pill
        int nameW = this.font.width(currentUser);
        int topCardW = 28 + nameW + 16;
        int topCardX = this.width - topCardW - 16;
        int topCardY = 14;

        this.topProfileBtn = new NexoTextButton(topCardX, topCardY, topCardW, 24, Component.empty(), false, () -> {
            this.isDrawerOpen = true;
            updateWidgetStates();
        });
        this.addRenderableWidget(this.topProfileBtn);

        // 4. Bottom-Right ZapNodes Banner
        int bannerW = 168;
        int bannerH = 46;
        int bannerX = this.width - bannerW - 16;
        int bannerY = this.height - bannerH - 12;

        this.zapNodesBannerBtn = new NexoTextButton(bannerX, bannerY, bannerW, bannerH, Component.empty(), false, () -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("https://zapnodes.net"));
                }
            } catch (Exception ignored) {}
        });
        this.addRenderableWidget(this.zapNodesBannerBtn);

        // 5. Account Switcher Drawer
        int drawerX = this.width - DRAWER_WIDTH;
        this.backdropCloser = new NexoTextButton(0, 0, drawerX, this.height, Component.empty(), false, () -> {
            if (isDrawerOpen) {
                this.isDrawerOpen = false;
                updateWidgetStates();
            }
        });
        this.addRenderableWidget(this.backdropCloser);

        NexoTextButton toggleAddBtn = new NexoTextButton(this.width - 48, 14, 20, 20, Component.empty(), false, () -> {
            this.showAddInput = !this.showAddInput;
            updateWidgetStates();
        });
        this.addRenderableWidget(toggleAddBtn);
        this.drawerButtons.add(toggleAddBtn);

        NexoTextButton closeDrawerBtn = new NexoTextButton(this.width - 28, 14, 20, 20, Component.empty(), false, () -> {
            this.isDrawerOpen = false;
            updateWidgetStates();
        });
        this.addRenderableWidget(closeDrawerBtn);
        this.drawerButtons.add(closeDrawerBtn);

        int cardStartX = drawerX + 18;
        int cardStartY = 50;
        int cW = 118;
        int cH = 142;

        for (int i = 0; i < SAVED_ACCOUNTS.size() && i < 4; i++) {
            final String acc = SAVED_ACCOUNTS.get(i);
            SkinHelper.getSkin(acc);

            int col = i % 2;
            int row = i / 2;
            int cx = cardStartX + col * (cW + 16);
            int cy = cardStartY + row * (cH + 14);

            NexoTextButton cardBtn = new NexoTextButton(cx, cy, cW, cH, Component.empty(), false, () -> {
                if (isDrawerOpen && !acc.equalsIgnoreCase(currentUser)) {
                    switchAccount(acc);
                }
            });
            this.addRenderableWidget(cardBtn);
            this.drawerButtons.add(cardBtn);
        }

        this.offlineInput = new EditBox(this.font, drawerX + 20, this.height - 82, DRAWER_WIDTH - 40, 20, Component.literal("Username"));
        this.offlineInput.setMaxLength(16);
        this.offlineInput.setHint(Component.literal("Enter username..."));
        this.addRenderableWidget(this.offlineInput);

        this.addAccountBtn = new NexoTextButton(drawerX + 20, this.height - 56, DRAWER_WIDTH - 40, 22,
                Component.literal("Add Account"), false, () -> {
            String newName = this.offlineInput.getValue().trim();
            if (!newName.isEmpty()) {
                if (!SAVED_ACCOUNTS.contains(newName)) {
                    SAVED_ACCOUNTS.addFirst(newName);
                }
                switchAccount(newName);
            }
        });
        this.addRenderableWidget(this.addAccountBtn);

        updateWidgetStates();
    }

    private void updateWidgetStates() {
        if (this.offlineInput != null) {
            for (NexoTextButton btn : this.centerButtons) btn.visible = !isDrawerOpen;
            for (NexoTextButton btn : this.dockButtons) btn.visible = !isDrawerOpen;
            this.zapNodesBannerBtn.visible = !isDrawerOpen;
            this.topProfileBtn.visible = !isDrawerOpen;

            this.backdropCloser.visible = isDrawerOpen;
            for (NexoTextButton btn : this.drawerButtons) btn.visible = isDrawerOpen;
            this.offlineInput.visible = isDrawerOpen && showAddInput;
            this.addAccountBtn.visible = isDrawerOpen && showAddInput;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Deep Cinematic Radial Background Blur
        this.panorama.extractRenderState(graphics, this.width, this.height);
        graphics.fill(0, 0, this.width, this.height, 0x5904070D);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 1. Center Crisp Vector SVG Logo (Auto-loads logo.svg or logo.png)
        Identifier logoSvg = NexoSvgLoader.loadSvg("textures/gui/logo.svg", 64, 64);
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, logoSvg, centerX - 32, centerY - 110, 0.0F, 0.0F, 64, 64, 64, 64);
        } catch (Exception ignored) {}

        // 2. Center Buttons Rendering
        if (!isDrawerOpen) {
            renderCenterStack(graphics, centerX, centerY - 16, mouseX, mouseY);
        }

        // 3. Bottom Floating Pill Dock with Vector SVG Icons
        if (!isDrawerOpen) {
            renderBottomDock(graphics, centerX, this.height - 42, mouseX, mouseY);
        }

        // 4. ZapNodes Banner
        if (!isDrawerOpen) {
            renderZapNodesBanner(graphics, mouseX, mouseY);
        }

        // 5. Top-Right Profile Pill
        if (!isDrawerOpen) {
            renderTopProfile(graphics, mouseX, mouseY);
        }

        graphics.text(this.font, Component.literal("NexoClient 1.0 (Vector Edition)"), 14, this.height - 16, 0xFF475569, false);

        // Drawer Slide Animation
        this.drawerAnim += ((isDrawerOpen ? 1.0f : 0.0f) - this.drawerAnim) * Math.min(1.0f, delta * 0.28f);
        if (this.drawerAnim > 0.01f) {
            renderAccountDrawer(graphics, mouseX, mouseY);
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void renderCenterStack(GuiGraphicsExtractor graphics, int cx, int cy, int mouseX, int mouseY) {
        int menuW = 220;
        int menuH = 34;
        int gap = 40;

        // 1. Singleplayer
        boolean spHov = (mouseX >= cx - menuW / 2 && mouseX <= cx + menuW / 2 && mouseY >= cy && mouseY <= cy + menuH);
        int spBg = spHov ? 0xF01E293B : 0xE60D111A;
        int spBorder = spHov ? 0x66FFFFFF : 0x22FFFFFF;
        NexoRenderUtils.drawSmoothPill(graphics, cx - menuW / 2, cy, menuW, menuH, spBg, spBorder, spHov);

        // Singleplayer SVG Icon + Text
        Identifier spIcon = NexoSvgLoader.loadSvg("textures/gui/singleplayer.svg", 16, 16);
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, spIcon, cx - 64, cy + 9, 0.0F, 0.0F, 16, 16, 16, 16);
        } catch (Exception ignored) {}
        graphics.text(this.font, Component.literal("Singleplayer"), cx - 42, cy + (menuH - 8) / 2, 0xFFF8FAFC, true);

        // 2. Multiplayer
        boolean mpHov = (mouseX >= cx - menuW / 2 && mouseX <= cx + menuW / 2 && mouseY >= cy + gap && mouseY <= cy + gap + menuH);
        int mpBg = mpHov ? 0xF01E293B : 0xE60D111A;
        int mpBorder = mpHov ? 0x66FFFFFF : 0x22FFFFFF;
        NexoRenderUtils.drawSmoothPill(graphics, cx - menuW / 2, cy + gap, menuW, menuH, mpBg, mpBorder, mpHov);

        Identifier mpIcon = NexoSvgLoader.loadSvg("textures/gui/multiplayer.svg", 16, 16);
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, mpIcon, cx - 60, cy + gap + 9, 0.0F, 0.0F, 16, 16, 16, 16);
        } catch (Exception ignored) {}
        graphics.text(this.font, Component.literal("Multiplayer"), cx - 38, cy + gap + (menuH - 8) / 2, 0xFFF8FAFC, true);

        // 3. Split Row (Partners + Store)
        int splitW = (menuW - 8) / 2;
        int splitY = cy + (gap * 2);

        boolean discHov = (mouseX >= cx - menuW / 2 && mouseX <= cx - menuW / 2 + splitW && mouseY >= splitY && mouseY <= splitY + menuH);
        NexoRenderUtils.drawPartnersPill(graphics, cx - menuW / 2, splitY, splitW, menuH, discHov);

        int storeX = cx - menuW / 2 + splitW + 8;
        boolean storeHov = (mouseX >= storeX && mouseX <= storeX + splitW && mouseY >= splitY && mouseY <= splitY + menuH);
        NexoRenderUtils.drawStorePill(graphics, storeX, splitY, splitW, menuH, storeHov);
    }

    private void renderBottomDock(GuiGraphicsExtractor graphics, int cx, int dockY, int mouseX, int mouseY) {
        int dockW = 210;
        int dockH = 32;
        int dockX = cx - dockW / 2;

        NexoRenderUtils.drawSmoothPill(graphics, dockX, dockY, dockW, dockH, 0xF20B0E17, 0x26FFFFFF, false);

        String[] svgFiles = {"mods.svg", "cosmetics.svg", "accounts.svg", "settings.svg", "folder.svg", "quit.svg"};
        int itemStep = 34;

        for (int i = 0; i < svgFiles.length; i++) {
            int ix = dockX + 6 + (i * itemStep);
            boolean hov = (mouseX >= ix && mouseX <= ix + 26 && mouseY >= dockY + 2 && mouseY <= dockY + 30);

            if (hov) graphics.fill(ix + 1, dockY + 4, ix + 25, dockY + 28, 0x22FFFFFF);

            Identifier svgIcon = NexoSvgLoader.loadSvg("textures/gui/" + svgFiles[i], 16, 16);
            try {
                graphics.blit(RenderPipelines.GUI_TEXTURED, svgIcon, ix + 5, dockY + 8, 0.0F, 0.0F, 16, 16, 16, 16);
            } catch (Exception ignored) {}

            // Green Active Dot on Cosmetics
            if (i == 1) graphics.fill(ix + 18, dockY + 6, ix + 22, dockY + 10, 0xFF10B981);
        }
    }

    private void renderZapNodesBanner(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int bannerW = 168;
        int bannerH = 46;
        int bannerX = this.width - bannerW - 16;
        int bannerY = this.height - bannerH - 12;

        boolean hovered = (mouseX >= bannerX && mouseX <= bannerX + bannerW && mouseY >= bannerY && mouseY <= bannerY + bannerH);
        int border = hovered ? 0x8838BDF8 : 0x22FFFFFF;

        NexoRenderUtils.drawSmoothPill(graphics, bannerX, bannerY, bannerW, bannerH, 0xF50D111A, border, hovered);

        Identifier bannerId = NexoTextureHelper.getZapNodesBanner();
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, bannerId, bannerX + 1, bannerY + 1, 0.0F, 0.0F, bannerW - 2, bannerH - 2, bannerW - 2, bannerH - 2);
        } catch (Exception ignored) {}

        int dotY = bannerY + bannerH + 4;
        graphics.fill(bannerX + 62, dotY, bannerX + 78, dotY + 2, 0xFF38BDF8);
        graphics.fill(bannerX + 82, dotY, bannerX + 90, dotY + 2, 0x44FFFFFF);
        graphics.fill(bannerX + 94, dotY, bannerX + 102, dotY + 2, 0x44FFFFFF);
    }

    private void renderTopProfile(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        String currentUser = (this.minecraft != null && this.minecraft.getUser() != null) ? this.minecraft.getUser().getName() : "Player";
        int nameW = this.font.width(currentUser);
        int cardW = 28 + nameW + 16;
        int cardX = this.width - cardW - 16;
        int cardY = 14;

        boolean hovered = (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + 24);
        int border = hovered ? 0x66FFFFFF : 0x22FFFFFF;

        NexoRenderUtils.drawSmoothPill(graphics, cardX, cardY, cardW, 24, 0xEE0B0F19, border, hovered);

        Identifier userSkin = SkinHelper.getSkin(currentUser);
        renderAvatarHead(graphics, userSkin, cardX + 4, cardY + 4, 16);
        graphics.text(this.font, Component.literal(currentUser), cardX + 24, cardY + 8, 0xFFE2E8F0, true);
    }

    private void renderAccountDrawer(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int currentDrawerX = (int) (this.width - (DRAWER_WIDTH * drawerAnim));
        int overlayAlpha = (int) (drawerAnim * 130);
        graphics.fill(0, 0, currentDrawerX, this.height, (overlayAlpha << 24));

        graphics.fill(currentDrawerX, 0, this.width, this.height, 0xFA0B0E14);
        graphics.fill(currentDrawerX, 0, currentDrawerX + 1, this.height, 0x1AFFFFFF);

        graphics.text(this.font, Component.literal("Accounts"), currentDrawerX + 20, 20, 0xFFF1F5F9, true);

        int cardStartX = currentDrawerX + 18;
        int cardStartY = 50;
        int cW = 118;
        int cH = 142;

        String currentUser = (this.minecraft != null && this.minecraft.getUser() != null) ? this.minecraft.getUser().getName() : "Player";

        for (int i = 0; i < SAVED_ACCOUNTS.size() && i < 4; i++) {
            String acc = SAVED_ACCOUNTS.get(i);
            boolean isActive = acc.equalsIgnoreCase(currentUser);

            int col = i % 2;
            int row = i / 2;
            int cx = cardStartX + col * (cW + 16);
            int cy = cardStartY + row * (cH + 14);

            boolean hovered = (mouseX >= cx && mouseX <= cx + cW && mouseY >= cy && mouseY <= cy + cH);

            int borderColor = isActive ? 0xFFEF4444 : (hovered ? 0x66FFFFFF : 0x1AFFFFFF);
            NexoRenderUtils.drawSmoothPill(graphics, cx, cy, cW, cH, 0xF010141E, borderColor, hovered);

            int avatarSize = 56;
            int avX = cx + (cW - avatarSize) / 2;
            int avY = cy + 16;
            graphics.fill(avX - 2, avY - 2, avX + avatarSize + 2, avY + avatarSize + 2, 0xFF19202E);

            Identifier skin = SkinHelper.getSkin(acc);
            renderAvatarHead(graphics, skin, avX, avY, avatarSize);

            int bannerColor = isActive ? 0xFFEF4444 : (hovered ? 0xFF242E42 : 0xFF171D2B);
            graphics.fill(cx + 1, cy + cH - 24, cx + cW - 1, cy + cH - 1, bannerColor);

            int textX = cx + (cW - this.font.width(acc)) / 2;
            graphics.text(this.font, Component.literal(acc), textX, cy + cH - 16, 0xFFFFFFFF, true);
        }

        if (showAddInput) {
            graphics.text(this.font, Component.literal("Username:"), currentDrawerX + 20, this.height - 100, 0xFF94A3B8, false);
        } else {
            graphics.text(this.font, Component.literal("Click '+' to add account"), currentDrawerX + 20, this.height - 32, 0xFF64748B, false);
        }
    }

    private void renderAvatarHead(GuiGraphicsExtractor graphics, Identifier skin, int x, int y, int size) {
        if (skin == null) skin = DEFAULT_SKIN;
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8, 8, size, size, 8, 8, 64, 64);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40, 8, size, size, 8, 8, 64, 64);
    }

    private void switchAccount(String newName) {
        if (this.minecraft != null) {
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + newName).getBytes(StandardCharsets.UTF_8));
            User newUser = new User(newName, uuid, "", Optional.empty(), Optional.empty());
            ((UserSetter) this.minecraft).nexo$setUser(newUser);
            this.minecraft.gui.setScreen(new NexoMainMenuScreen());
        }
    }
}