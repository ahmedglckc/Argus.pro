package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;

public class NoSlow extends Module {

    public NoSlow() {
        super("NoSlow", ModuleCategory.MOVEMENT, Keyboard.KEY_G);
        this.toggle();
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        run();
    }

    public static void run() {
        if (Entity.getThePlayer() == null || !Mouse.isButtonDown(1)) return;

        Entity.setSpeed(0.26324293);
    }
}