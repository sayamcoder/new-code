package code.versee.nexoclient.ui.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class UIComponent {
    public int x, y, width, height;
    public boolean visible = true;
    public boolean enabled = true;
    protected float hoverAnimation = 0.0f;

    public UIComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta);
    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void updateHover(int mouseX, int mouseY, float delta) {
        boolean hovered = isHovered(mouseX, mouseY) && enabled;
        this.hoverAnimation = AnimationHelper.lerp(this.hoverAnimation, hovered ? 1.0f : 0.0f, delta * 0.25f);
    }
}