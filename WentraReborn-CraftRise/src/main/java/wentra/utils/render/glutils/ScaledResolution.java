package wentra.utils.render.glutils;

import org.lwjgl.opengl.Display;

public class ScaledResolution {
    private final int scaledWidth, scaledHeight, scaleFactor;
    private final double scaledWidthDouble, scaledHeightDouble;

    public ScaledResolution() {
        int displayWidth = Display.getWidth();
        int displayHeight = Display.getHeight();
        int scale = 2;

        this.scaledWidthDouble = displayWidth / (double) scale;
        this.scaledHeightDouble = displayHeight / (double) scale;

        this.scaledWidth = (int) Math.ceil(scaledWidthDouble);
        this.scaledHeight = (int) Math.ceil(scaledHeightDouble);
        this.scaleFactor = scale;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public int getScaleFactor() {
        return scaleFactor;
    }
}
