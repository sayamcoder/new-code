package code.versee.nexoclient.gui;

import code.versee.nexoclient.ui.components.NexoTextButton;
import code.versee.nexoclient.util.NexoTextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NexoPartnersScreen extends Screen {
    private final Screen parent;

    public record ServerCard(String name, String ip, String tag, int players, String flag) {}
    private final List<ServerCard> servers = new ArrayList<>();

    public NexoPartnersScreen(Screen parent) {
        super(Component.literal("Partnered Servers"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.servers.clear();

        // High-Quality Partner Servers (Screenshot 5 Match)
        servers.add(new ServerCard("PvPLegacy", "play.pvplegacy.net", "Custom Duels • Crystal FFA", 1249, "🇺🇸"));
        servers.add(new ServerCard("FlameFrags", "play.flamefrags.com", "Duels • Survival", 460, "🇺🇸"));
        servers.add(new ServerCard("RainMC", "play.rainmc.fun", "Official Partner #1", 380, "🌐"));
        servers.add(new ServerCard("Hypixel Network", "mc.hypixel.net", "Skyblock • Bedwars", 29701, "🇺🇸"));
        servers.add(new ServerCard("JackpotMC", "play.jackpotmc.com", "Lifesteal C2", 393, "🇺🇸"));
        servers.add(new ServerCard("LeoneMC", "play.leonemc.net", "Crystal PvP • Elytra", 332, "🇺🇸"));

        int modalW = 620;
        int modalH = 350;
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // Red Close Button
        this.addRenderableWidget(new NexoTextButton(modalX + modalW - 28, modalY + 12, 18, 18, Component.literal("✕"), false, () -> {
            if (this.minecraft != null) this.minecraft.gui.setScreen(this.parent);
        }));

        // 3-Column Card Buttons
        int cardStartX = modalX + 16;
        int cardStartY = modalY + 68;
        int cardW = 188;
        int cardH = 110;

        int col = 0;
        int row = 0;

        for (ServerCard s : servers) {
            int cx = cardStartX + col * (cardW + 12);
            int cy = cardStartY + row * (cardH + 12);

            // One-Click Join Button on Card
            this.addRenderableWidget(new NexoTextButton(cx, cy, cardW, cardH, Component.empty(), false, () -> {
                if (this.minecraft != null) {
                    ServerData data = new ServerData(s.name(), s.ip(), ServerData.Type.OTHER);
                    ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(s.ip()), data, false, null);
                }
            }));

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0xAA060911);

        int modalW = 620;
        int modalH = 350;
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // Dark Luxury Modal Box
        graphics.fill(modalX, modalY, modalX + modalW, modalY + modalH, 0x1AFFFFFF);
        graphics.fill(modalX + 1, modalY + 1, modalX + modalW - 1, modalY + modalH - 1, 0xF00B0E14);

        // Header: Logo + "PARTNERED SERVERS"
        Identifier logo = NexoTextureHelper.getLogo();
        try {
            graphics.blit(RenderPipelines.GUI_TEXTURED, logo, modalX + 16, modalY + 12, 0.0F, 0.0F, 20, 20, 256, 256);
        } catch (Exception ignored) {}

        graphics.text(this.font, Component.literal("PARTNERED SERVERS"), modalX + 42, modalY + 18, 0xFFFFFFFF, true);

        // Time Widget
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd  hh:mm a"));
        graphics.fill(modalX + 180, modalY + 15, modalX + 320, modalY + 29, 0x1AFFFFFF);
        graphics.text(this.font, Component.literal("🌙 " + timeStr), modalX + 186, modalY + 18, 0xFF94A3B8, false);

        // 3-Column Server Cards Grid
        int cardStartX = modalX + 16;
        int cardStartY = modalY + 68;
        int cardW = 188;
        int cardH = 110;

        int col = 0;
        int row = 0;

        for (ServerCard s : servers) {
            int cx = cardStartX + col * (cardW + 12);
            int cy = cardStartY + row * (cardH + 12);
            boolean hovered = (mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH);

            // Card Frame
            graphics.fill(cx, cy, cx + cardW, cy + cardH, hovered ? 0x6638BDF8 : 0x1AFFFFFF);
            graphics.fill(cx + 1, cy + 1, cx + cardW - 1, cy + cardH - 1, 0xF0121723);

            // Banner Dark Tone
            graphics.fill(cx + 1, cy + 1, cx + cardW - 1, cy + 50, 0xFF19202E);

            // Server Title & Tags
            graphics.text(this.font, Component.literal(s.name()), cx + 8, cy + 8, 0xFFFFFFFF, true);
            graphics.text(this.font, Component.literal(s.flag()), cx + cardW - 20, cy + 8, 0xFFFFFFFF, false);

            graphics.text(this.font, Component.literal(s.tag()), cx + 8, cy + 24, 0xFF94A3B8, false);

            // IP Address
            graphics.text(this.font, Component.literal(s.ip()), cx + 8, cy + 62, 0xFF38BDF8, false);

            // Player Count with Green Dot (🟢 1249)
            graphics.fill(cx + 8, cy + cardH - 16, cx + 14, cy + cardH - 10, 0xFF10B981);
            graphics.text(this.font, Component.literal(String.valueOf(s.players())), cx + 18, cy + cardH - 18, 0xFF94A3B8, false);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}