package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.module.Module;

public class Flight extends Module {

    public Flight() {
        super("Flight", ModuleCategory.MOVEMENT, Keyboard.KEY_F);
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (Entity.getThePlayer() == null) return;

        if(Entity.getCurrentScreen().equals("null")) {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                Entity.setMotionY(0.42);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                Entity.setMotionY(-0.42);
            } else {
                Entity.setMotionY(-0.06);
            }
        }
    }
}