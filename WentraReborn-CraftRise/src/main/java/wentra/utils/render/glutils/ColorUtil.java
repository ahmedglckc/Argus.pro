package wentra.utils.render.glutils;

import org.lwjgl.opengl.GL11;
import wentra.utils.render.RenderUtil;

import java.awt.*;

public class ColorUtil {
    public static Color gradientColor(int speed, int idx, Color... c) {
        int angle = (int)((System.currentTimeMillis() / speed + idx) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int i = (int)(angle / 360f * c.length);
        if (i == c.length) i--;
        Color c1 = c[i];
        Color c2 = c[i == c.length - 1 ? 0 : i + 1];
        return interpolateColorC(c1.getRGB(), c2.getRGB(), angle / 360f * c.length - i);
    }

    public static int createRainbowFromOffset(int speed, int offset) {
        return getArrayAstolfo(offset, 0.3).hashCode();
    }

    public static Color getArrayAstolfo(float colorOffset, double renk) {
        double timer = (double) System.currentTimeMillis() / 1.0E8 * renk * 990000.0;
        double factor = (Math.sin(timer + (double) (colorOffset * 0.85f)) + 1.0) * (double) 0.45f;
        return mixColors(new Color(224, 114, 238), Color.cyan, factor);
    }

    static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static void setColor(Color color) {
        float alpha = (color.getRGB() >> 24 & 0xFF) / 255.0F;
        float red = (color.getRGB() >> 16 & 0xFF) / 255.0F;
        float green = (color.getRGB() >> 8 & 0xFF) / 255.0F;
        float blue = (color.getRGB() & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int applyOpacity(int color, float opacity)
    {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue)
    {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static void color(int color, float alpha)
    {
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        ColorUtil.color(r, g, b, alpha);
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static void color(double d, double d2, double d3) {
        color(d, d2, d3, 1.0);
    }

    public static void color(double d, double d2, double d3, double d4) {
        GL11.glColor4d(d, d2, d3, d4);
    }

    public static void color(int color)
    {
        color(color, (float)(color >> 24 & 255) / 255.0F);
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor)
    {
        int angle = (int)(((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtil.interpolateColorHue(start, end, angle / 360f) : ColorUtil.interpolateColorC(start, end, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount)
    {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount)
    {
        amount = Math.min(1, Math.max(0, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color TwoColorEffect(int cl1, int cl2, double speed, int index) {
        int angle = (int)(((double)System.currentTimeMillis() / speed + (double)index) % 360.0);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1, cl2, (float)angle / 360.0f);
    }

    public static Color rainbow(int speed, float sat, float bright) {
        float hue = (System.currentTimeMillis() % speed) / (float) speed;
        return Color.getHSBColor(hue, sat, bright);
    }

    public static int gradientColor(float t) {
        int[] c = {0xFF0071FF, 0xFF8000FF, 0xFFFF00FF};
        int l = c.length;
        float scaled = t * l;
        int i = (int) scaled % l;
        int nxt = (i + 1) % l;
        float blend = scaled - i;

        int r1 = (c[i] >> 16) & 0xFF;
        int g1 = (c[i] >> 8) & 0xFF;
        int b1 = c[i] & 0xFF;

        int r2 = (c[nxt] >> 16) & 0xFF;
        int g2 = (c[nxt] >> 8) & 0xFF;
        int b2 = c[nxt] & 0xFF;

        int r = (int) (r1 * (1 - blend) + r2 * blend);
        int g = (int) (g1 * (1 - blend) + g2 * blend);
        int b = (int) (b1 * (1 - blend) + b2 * blend);

        return 0xFF << 24 | r << 16 | g << 8 | b;
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static Color TwoColorEffect(Color cl1, Color cl2, double speed, int index) {
        int angle = (int)(((double)System.currentTimeMillis() / speed + (double)index) % 360.0);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1.getRGB(), cl2.getRGB(), (float)angle / 360.0f);
    }

    public static void color2(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        ColorUtil.color(r, g, b, alpha);
    }

    public static void color2(int color) {
        color2(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static Color getGradientColor(Color c1, Color c2, float t) {
        float time = (float) ((Math.sin(t * Math.PI * 2 - Math.PI / 2) + 1) / 2); // 0 -> 1 sinüs smooth

        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * time);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * time);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * time);
        int a = (int) (c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * time);

        return new Color(r, g, b, a);
    }

    public static Color gradient(int speed, int index, Color ... colors) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int)((float)angle / 360.0f * (float)colors.length);
        if (colorIndex == colors.length) {
            --colorIndex;
        }
        Color color1 = colors[colorIndex];
        Color color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return interpolateColorC(color1.getRGB(), color2.getRGB(), (float)angle / 360.0f * (float)colors.length - (float)colorIndex);
    }

    public static Color mixColor(int cl1, int cl2, double speed, int idx) {
        int angle = (int)((System.currentTimeMillis() / speed + idx) % 360.0);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1, cl2, angle / 360.0f);
    }

    public static int getRed(int hex) {
        return hex >> 16 & 0xFF;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 0xFF;
    }

    public static int getBlue(int hex) {
        return hex & 0xFF;
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 0xFF;
    }

    public static Color interpolateColorC(int c1, int c2, float a) {
        a = Math.max(0f, Math.min(1f, a));
        return new Color(interpolateInt(getRed(c1), getRed(c2), a), interpolateInt(getGreen(c1), getGreen(c2), a), interpolateInt(getBlue(c1), getBlue(c2), a), interpolateInt(getAlpha(c1), getAlpha(c2), a));
    }

    public static Double interpolate(double o, double n, double f) {
        return o + (n - o) * f;
    }

    public static int interpolateInt(int o, int n, double f) {
        return interpolate(o, n, (float)f).intValue();
    }

    public static int getRainbowLight(int s, int o) {
        float h = (System.currentTimeMillis() + o) % s;
        h /= s;
        return Color.getHSBColor(h, 0.55f, 1f).getRGB();
    }
}