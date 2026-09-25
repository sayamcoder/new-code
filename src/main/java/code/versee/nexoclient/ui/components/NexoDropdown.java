package code.versee.nexoclient.ui.components;

import code.versee.nexoclient.ui.core.NexoTheme;
import code.versee.nexoclient.ui.core.UIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class NexoDropdown extends UIComponent {
    private final String title;
    private final List<String> options;
    private int selectedIndex;
    private boolean open = false;
    private final Consumer<String> onSelect;

    public NexoDropdown(int x, int y, int width, String title, List<String> options, int defaultIndex, Consumer<String> onSelect) {
        super(x, y, width, 24);
        this.title = title;
        this.options = options;
        this.selectedIndex = defaultIndex;
        this.onSelect = onSelect;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        updateHover(mouseX, mouseY, delta);

        Minecraft mc = Minecraft.getInstance();

        // Main dropdown box
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF131C2E);
        graphics.fill(this.x, this.y, this.x + this.width, this.y + 1, open ? NexoTheme.ACCENT_CYAN : NexoTheme.BORDER_IDLE);

        String currentText = options.isEmpty() ? "None" : options.get(selectedIndex);
        graphics.text(mc.font, Component.literal(this.title + ": " + currentText), this.x + 8, this.y + 7, NexoTheme.TEXT_WHITE, false);
        graphics.text(mc.font, Component.literal(open ? "▲" : "▼"), this.x + this.width - 14, this.y + 7, NexoTheme.ACCENT_CYAN, false);

        // Popout items
        if (open) {
            int popY = this.y + this.height + 2;
            for (int i = 0; i < options.size(); i++) {
                String opt = options.get(i);
                int optY = popY + (i * 20);
                boolean hovered = (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= optY && mouseY <= optY + 20);

                graphics.fill(this.x, optY, this.x + this.width, optY + 20, hovered ? NexoTheme.BG_CARD_HOVER : 0xF00F172A);
                int textColor = (i == selectedIndex) ? NexoTheme.ACCENT_CYAN : NexoTheme.TEXT_WHITE;
                graphics.text(mc.font, Component.literal(opt), this.x + 8, optY + 6, textColor, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;

        // Toggle dropdown
        if (isHovered((int) mouseX, (int) mouseY)) {
            this.open = !this.open;
            return true;
        }

        // Click on items
        if (open) {
            int popY = this.y + this.height + 2;
            for (int i = 0; i < options.size(); i++) {
                int optY = popY + (i * 20);
                if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= optY && mouseY <= optY + 20) {
                    this.selectedIndex = i;
                    this.open = false;
                    if (this.onSelect != null) this.onSelect.accept(options.get(i));
                    return true;
                }
            }
            this.open = false;
        }

        return false;
    }
}