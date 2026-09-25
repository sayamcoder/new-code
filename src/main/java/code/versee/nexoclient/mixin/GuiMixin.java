package code.versee.nexoclient.mixin;

import code.versee.nexoclient.ui.screens.NexoModMenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final
    protected Minecraft minecraft;

    @Unique
    private static boolean nexo$wasRightShiftDown = false;

    // 26.3 Exact Frame Hook (0 Errors)
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void nexo$onRenderHud(DeltaTracker deltaTracker, boolean z1, boolean z2, CallbackInfo ci) {
        boolean isDown = InputConstants.isKeyDown(345); // 345 = Right Shift

        if (isDown && !nexo$wasRightShiftDown) {
            nexo$wasRightShiftDown = true;
            if (this.minecraft != null && this.minecraft.gui != null) {
                if (this.minecraft.gui.screen() == null) {
                    this.minecraft.gui.setScreen(new NexoModMenuScreen());
                } else if (this.minecraft.gui.screen() instanceof NexoModMenuScreen) {
                    this.minecraft.gui.setScreen(null);
                }
            }
        } else if (!isDown) {
            nexo$wasRightShiftDown = false;
        }
    }
}