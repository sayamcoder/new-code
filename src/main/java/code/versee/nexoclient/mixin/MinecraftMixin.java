package code.versee.nexoclient.mixin;

import code.versee.nexoclient.input.NexoKeybinds;
import code.versee.nexoclient.ui.screens.NexoModMenuScreen;
import code.versee.nexoclient.util.UserSetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements UserSetter {

    @Shadow @Mutable
    private User user;

    @Shadow @Final
    public Gui gui;

    @Override
    public void nexo$setUser(User newUser) {
        this.user = newUser;
    }

    // 100% Reliable In-Game Keybind Check
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void nexo$handleRightShiftMenu(CallbackInfo ci) {
        if (this.gui != null) {
            while (NexoKeybinds.OPEN_MOD_MENU.consumeClick()) {
                if (this.gui.screen() == null) {
                    this.gui.setScreen(new NexoModMenuScreen());
                } else if (this.gui.screen() instanceof NexoModMenuScreen) {
                    this.gui.setScreen(null);
                }
            }
        }
    }
}