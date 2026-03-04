package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.TickEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.time.TimerUtil;

public class Speed extends Module {
    private final TimerUtil timer = new TimerUtil();

    public Speed() {
        super("Speed", ModuleCategory.MOVEMENT, Keyboard.KEY_X);
    }


    @Subscribe
    public void onTick(TickEvent event) {
                Entity.setSpeed(1.1087551867219917);
//        if (timer.hasTimeElapsed(1000 / 50, true) && Entity.isMoving()) {
//            if (Entity.onGround()) {
//                Entity.setMotionY(0.241);
//                Entity.setSpeed(0.47);
//            } else if (Entity.getMotionY() > 0) {
//                Entity.setMotionY(-0.201);
//            }
//        }
    }
}