package code.versee.nexoclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class NexoTextButton extends AbstractButton {
    private final Runnable onPressAction;

    public NexoTextButton(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPressAction = onPress;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (this.onPressAction != null) {
            this.onPressAction.run();
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = this.isHovered();

        if (hovered && !this.getMessage().getString().isEmpty()) {
            graphics.fill(this.getX() - 4, this.getY() - 1, this.getX() + this.width + 4, this.getY() + this.height + 1, 0x2238BDF8);
            graphics.fill(this.getX() - 4, this.getY() + 2, this.getX() - 2, this.getY() + this.height - 2, 0xFF38BDF8);
        }

        if (!this.getMessage().getString().isEmpty()) {
            int textColor = hovered ? 0xFFFFFFFF : 0xFFCBD5E1;
            int textX = this.getX() + (hovered ? 4 : 0);
            int textY = this.getY() + (this.height - 8) / 2;
            graphics.text(mc.font, this.getMessage(), textX, textY, textColor, false);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}