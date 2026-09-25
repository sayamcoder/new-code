package code.versee.nexoclient.web;

import code.versee.nexoclient.gui.NexoPartnersScreen;
import code.versee.nexoclient.ui.screens.NexoModMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

import java.awt.Desktop;
import java.net.URI;

public class NexoWebBridge {
    private final Screen parentScreen;

    public NexoWebBridge(Screen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void handleAction(String action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        // Run actions safely on Minecraft main thread
        mc.execute(() -> {
            try {
                switch (action.toLowerCase()) {
                    case "singleplayer":
                        if (mc.gui != null) mc.gui.setScreen(new SelectWorldScreen(this.parentScreen));
                        break;
                    case "multiplayer":
                        if (mc.gui != null) mc.gui.setScreen(new JoinMultiplayerScreen(this.parentScreen));
                        break;
                    case "partners":
                    case "cosmetics":
                        if (mc.gui != null) mc.gui.setScreen(new NexoPartnersScreen(this.parentScreen));
                        break;
                    case "store":
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI("https://nexoclient-api.bonto.run"));
                        }
                        break;
                    case "modmenu":
                        if (mc.gui != null) mc.gui.setScreen(new NexoModMenuScreen());
                        break;
                    case "options":
                        if (mc.gui != null) mc.gui.setScreen(new OptionsScreen(this.parentScreen, mc.options));
                        break;
                    case "folder":
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(mc.gameDirectory);
                        }
                        break;
                    case "zapnodes":
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI("https://zapnodes.site"));
                        }
                        break;
                    case "quit":
                        mc.stop();
                        break;
                }
            } catch (Exception ignored) {}
        });
    }
}