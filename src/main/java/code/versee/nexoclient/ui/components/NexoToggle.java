package code.versee.nexoclient.ui.components;

import code.versee.nexoclient.ui.core.AnimationHelper;
import code.versee.nexoclient.ui.core.NexoTheme;
import code.versee.nexoclient.ui.core.UIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class NexoToggle extends UIComponent {
    private final String label;
    private boolean state;
    private float toggleAnim = 0.0f;
    private final Consumer<Boolean> onToggle;

    public NexoToggle(int x, int y, int width, String label, boolean initialState, Consumer<Boolean> onToggle) {
        super(x, y, width, 22);
        this.label = label;
        this.state = initialState;
        this.toggleAnim = initialState ? 1.0f : 0.0f;
        this.onToggle = onToggle;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        updateHover(mouseX, mouseY, delta);

        this.toggleAnim = AnimationHelper.lerp(this.toggleAnim, state ? 1.0f : 0.0f, delta * 0.25f);

        Minecraft mc = Minecraft.getInstance();

        // Label text
        graphics.text(mc.font, Component.literal(this.label), this.x, this.y + 6, NexoTheme.TEXT_WHITE, false);

        // Switch track
        int switchW = 34;
        int switchH = 16;
        int switchX = this.x + this.width - switchW;
        int switchY = this.y + 3;

        int trackColor = AnimationHelper.interpolateColor(0xFF1E293B, NexoTheme.ACCENT_CYAN, toggleAnim);
        graphics.fill(switchX, switchY, switchX + switchW, switchY + switchH, trackColor);
        graphics.fill(switchX, switchY, switchX + switchW, switchY + 1, NexoTheme.BORDER_IDLE);

        // Switch knob
        int knobSize = 12;
        int knobX = (int) (switchX + 2 + (switchW - knobSize - 4) * toggleAnim);
        int knobY = switchY + 2;

        graphics.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        if (isHovered((int) mouseX, (int) mouseY)) {
            this.state = !this.state;
            if (this.onToggle != null) this.onToggle.accept(this.state);
            return true;
        }
        return false;
    }
}