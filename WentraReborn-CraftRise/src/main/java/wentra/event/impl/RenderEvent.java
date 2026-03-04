package wentra.event.impl;

import wentra.event.Event;
import wentra.utils.render.glutils.ScaledResolution;

public class RenderEvent extends Event {
    private final float partialTicks;

    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getScreenWidth() {
        ScaledResolution scaledResolution = new ScaledResolution();
        int screenWidth = scaledResolution.getScaledWidth();
        return screenWidth;
    }

    public int getScreenHeight() {
        ScaledResolution scaledResolution = new ScaledResolution();
        int screenHeight = scaledResolution.getScaledHeight();
        return screenHeight;
    }
}
