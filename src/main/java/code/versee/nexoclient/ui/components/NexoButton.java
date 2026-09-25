package code.versee.nexoclient.ui.components;

import code.versee.nexoclient.ui.core.AnimationHelper;
import code.versee.nexoclient.ui.core.NexoTheme;
import code.versee.nexoclient.ui.core.UIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class NexoButton extends UIComponent {
    private final String text;
    private final Runnable onClick;
    private final boolean isNavButton;

    public NexoButton(int x, int y, int width, int height, String text, boolean isNavButton, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.isNavButton = isNavButton;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        updateHover(mouseX, mouseY, delta);

        Minecraft mc = Minecraft.getInstance();

        if (isNavButton) {
            // Feather Left Navigation Style
            int bgColor = AnimationHelper.interpolateColor(0x00000000, 0x2238BDF8, hoverAnimation);
            graphics.fill(this.x - 4, this.y - 2, this.x + this.width, this.y + this.height + 2, bgColor);

            // Left Animated Cyan Bar
            int barAlpha = (int) (hoverAnimation * 255);
            if (barAlpha > 0) {
                graphics.fill(this.x - 4, this.y, this.x - 1, this.y + this.height, (barAlpha << 24) | 0x38BDF8);
            }

            int textColor = AnimationHelper.interpolateColor(NexoTheme.TEXT_MUTED, NexoTheme.TEXT_WHITE, hoverAnimation);
            int textX = (int) (this.x + (hoverAnimation * 4));
            graphics.text(mc.font, Component.literal(this.text), textX, this.y + 5, textColor, hoverAnimation > 0.5f);
        } else {
            // Regular Filled Pill Button
            int bg = AnimationHelper.interpolateColor(NexoTheme.BG_CARD, NexoTheme.ACCENT_BLUE, hoverAnimation);
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, bg);
            graphics.fill(this.x, this.y, this.x + this.width, this.y + 1, NexoTheme.BORDER_IDLE);

            int textX = this.x + (this.width - mc.font.width(this.text)) / 2;
            int textY = this.y + (this.height - 8) / 2;
            graphics.text(mc.font, Component.literal(this.text), textX, textY, NexoTheme.TEXT_WHITE, true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        if (isHovered((int) mouseX, (int) mouseY)) {
            if (this.onClick != null) this.onClick.run();
            return true;
        }
        return false;
    }
}