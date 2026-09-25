package code.versee.nexoclient.ui.core;

public class AnimationHelper {
    public static float lerp(float current, float target, float speed) {
        return current + (target - current) * Math.clamp(speed, 0.01f, 1.0f);
    }

    public static int interpolateColor(int color1, int color2, float factor) {
        factor = Math.clamp(factor, 0.0f, 1.0f);

        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}