package code.versee.nexoclient;

import code.versee.nexoclient.mods.FullBrightMod;
import code.versee.nexoclient.security.NexoAntiTamper;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class Nexoclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NexoAntiTamper.checkEnvironment();
        FullBrightMod.apply();

        // 🟢 Triggers MCEF startup listener seamlessly
        System.out.println("[NexoClient] Client Loaded with In-Game Web View.");
    }
}