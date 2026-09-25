package code.versee.nexoclient.ui.components;

import code.versee.nexoclient.ui.core.NexoTheme;
import code.versee.nexoclient.ui.core.UIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class NexoSlider extends UIComponent {
    private final String label;
    private double value; // 0.0 to 1.0
    private final double min, max;
    private boolean dragging = false;
    private final Consumer<Double> onChange;

    public NexoSlider(int x, int y, int width, String label, double min, double max, double current, Consumer<Double> onChange) {
        super(x, y, width, 26);
        this.label = label;
        this.min = min;
        this.max = max;
        this.value = (current - min) / (max - min);
        this.onChange = onChange;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        updateHover(mouseX, mouseY, delta);

        Minecraft mc = Minecraft.getInstance();
        double displayVal = min + (max - min) * value;
        String valText = String.format("%.1f", displayVal);

        // Header (Label + Value badge)
        graphics.text(mc.font, Component.literal(this.label), this.x, this.y, NexoTheme.TEXT_WHITE, false);
        graphics.text(mc.font, Component.literal(valText), this.x + this.width - mc.font.width(valText), this.y, NexoTheme.ACCENT_CYAN, false);

        // Track bar
        int barY = this.y + 14;
        int barH = 6;
        graphics.fill(this.x, barY, this.x + this.width, barY + barH, 0xFF1E293B);

        // Filled active bar
        int fillW = (int) (this.width * value);
        graphics.fill(this.x, barY, this.x + fillW, barY + barH, NexoTheme.ACCENT_CYAN);

        // Draggable Knob
        int knobX = this.x + fillW - 3;
        graphics.fill(knobX, barY - 2, knobX + 6, barY + barH + 2, 0xFFFFFFFF);

        if (dragging) {
            this.value = Math.clamp((double) (mouseX - this.x) / this.width, 0.0, 1.0);
            if (this.onChange != null) this.onChange.accept(min + (max - min) * this.value);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        if (isHovered((int) mouseX, (int) mouseY)) {
            this.dragging = true;
            this.value = Math.clamp((mouseX - this.x) / this.width, 0.0, 1.0);
            if (this.onChange != null) this.onChange.accept(min + (max - min) * this.value);
            return true;
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
    }
}