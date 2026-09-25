package code.versee.nexoclient.ui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class NexoTextButton extends AbstractButton {
    private final Runnable onPressAction;
    private final boolean isNavButton;
    private float hoverAnim = 0.0f;

    public NexoTextButton(int x, int y, int width, int height, Component message, boolean isNavButton, Runnable onPress) {
        super(x, y, width, height, message);
        this.isNavButton = isNavButton;
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

        // Smooth delta hover transition
        this.hoverAnim += (hovered ? 1.0f - this.hoverAnim : 0.0f - this.hoverAnim) * Math.min(1.0f, delta * 0.35f);

        if (this.isNavButton) {
            // Minimal Navigation Hover (Subtle glass pill + soft left indicator)
            if (this.hoverAnim > 0.01f) {
                int bgAlpha = (int) (this.hoverAnim * 22);
                graphics.fill(this.getX() - 6, this.getY() - 1, this.getX() + this.width + 6, this.getY() + this.height + 1, (bgAlpha << 24) | 0xFFFFFF);

                // Thin 2px white indicator bar
                int barAlpha = (int) (this.hoverAnim * 230);
                graphics.fill(this.getX() - 6, this.getY() + 3, this.getX() - 4, this.getY() + this.height - 3, (barAlpha << 24) | 0xFFFFFF);
            }

            int textColor = hovered ? 0xFFFFFFFF : 0xFF94A3B8;
            int textX = (int) (this.getX() + (this.hoverAnim * 3));
            int textY = this.getY() + (this.height - 8) / 2;
            graphics.text(mc.font, this.getMessage(), textX, textY, textColor, hovered);
        } else if (!this.getMessage().getString().isEmpty()) {
            // Modern Dark Glass Pill Button (NO harsh solid blue)
            int bg = hovered ? 0xF01E2536 : 0xCC111622;
            int border = hovered ? 0x44FFFFFF : 0x1AFFFFFF;

            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, border);
            graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, bg);

            int textColor = hovered ? 0xFFFFFFFF : 0xFFCBD5E1;
            int textX = this.getX() + (this.width - mc.font.width(this.getMessage())) / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            graphics.text(mc.font, this.getMessage(), textX, textY, textColor, true);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}