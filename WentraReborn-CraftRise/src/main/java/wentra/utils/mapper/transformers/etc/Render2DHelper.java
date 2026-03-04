package wentra.utils.mapper.transformers.etc;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wentra.event.EventBus;
import wentra.event.impl.RenderEvent;
import wentra.event.impl.TickEvent;
import wentra.utils.render.notification.NotificationManager;

import java.lang.reflect.InvocationTargetException;

public class Render2DHelper {

    public static Object fontRenderer;
    public static float renderPartialTicks = 0.0f;

    public static void Hook(float p) {
        EventBus.callEvent(new TickEvent());
        EventBus.callEvent(new RenderEvent(p));
        NotificationManager.render();
        renderPartialTicks = p;
    }
}