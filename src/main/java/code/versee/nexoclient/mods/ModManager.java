package code.versee.nexoclient.mods;

import java.util.ArrayList;
import java.util.List;

public class ModManager {
    public record ClientMod(String id, String name, String category, String description, Runnable toggleAction, java.util.function.BooleanSupplier isEnabled) {}

    public static final List<ClientMod> MODS = new ArrayList<>();

    static {
        MODS.add(new ClientMod("fullbright", "FullBright", "Render", "Max brightness in caves and night.", FullBrightMod::toggle, () -> FullBrightMod.enabled));
        MODS.add(new ClientMod("veinminer", "Safe Veinminer", "Mining", "Mine entire ore veins safely without breaking tools.", () -> SafeVeinminerMod.enabled = !SafeVeinminerMod.enabled, () -> SafeVeinminerMod.enabled));
        MODS.add(new ClientMod("sprint", "Toggle Sprint", "PvP", "Automatically sprint without double tapping W.", () -> {}, () -> true));
        MODS.add(new ClientMod("fps", "FPS Display", "HUD", "Clean minimal FPS counter on screen.", () -> {}, () -> true));
    }
}