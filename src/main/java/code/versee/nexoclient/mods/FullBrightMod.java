package code.versee.nexoclient.mods;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class FullBrightMod {
    public static boolean enabled = true;
    private static double originalGamma = 1.0;

    public static void toggle() {
        enabled = !enabled;
        apply();
    }

    public static void apply() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options != null && mc.options.gamma() != null) {
                Object gammaOption = mc.options.gamma();

                // SimpleOption ke internal 'value' field par direct reflection
                Class<?> clazz = gammaOption.getClass();
                Field targetField = null;

                while (clazz != null && targetField == null) {
                    for (Field f : clazz.getDeclaredFields()) {
                        if (f.getType() == Object.class || f.getType() == Double.class || f.getType() == double.class) {
                            targetField = f;
                            break;
                        }
                    }
                    if (targetField == null) {
                        clazz = clazz.getSuperclass();
                    }
                }

                if (targetField != null) {
                    targetField.setAccessible(true);
                    if (enabled) {
                        targetField.set(gammaOption, 15.0); // 100% Daylight brightness
                    } else {
                        targetField.set(gammaOption, originalGamma);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}