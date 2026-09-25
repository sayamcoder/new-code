package code.versee.nexoclient.mixin;

import code.versee.nexoclient.ui.screens.NexoMainMenuScreen;    // Option 1 (Lunar 1:1)
import code.versee.nexoclient.ui.screens.NexoPureVectorScreen; // Option 2 (Pure Vector)
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class TitleScreenMixin {

    // 1 = Lunar Style 1:1 Layout (NexoMainMenuScreen)
    // 2 = Pure Vector Engine (NexoPureVectorScreen)
    private static final int ACTIVE_UI = 2;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void redirectScreens(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.gui == null) return;

        if ((Object) this instanceof TitleScreen) {
            Screen targetScreen = (ACTIVE_UI == 2) ? new NexoPureVectorScreen() : new NexoMainMenuScreen();

            if (!((Object) this).getClass().equals(targetScreen.getClass())) {
                client.gui.setScreen(targetScreen);
                ci.cancel();
            }
        }
    }
}