package wentra.utils.mapper.transformers.etc;

import wentra.event.EventBus;
import wentra.event.impl.EventRender3D;
import wentra.module.Module;
import wentra.utils.render.glutils.GlStateManager;

public class EffectRendererHelper {
    public static float renderPartialTicks = 0.0f;
    public void Hook(Object entity, float ticks) {
        EventBus.callEvent(new EventRender3D(ticks));
        renderPartialTicks = ticks;
    }
}
