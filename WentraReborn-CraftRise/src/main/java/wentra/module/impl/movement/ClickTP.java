package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.NumberSetting;
import wentra.utils.mapper.Entity;
import wentra.utils.time.TimerUtil;

public class ClickTP extends Module {

    private final TimerUtil timerUtil = new TimerUtil();

    public ClickTP() {
        super("ClickTP", ModuleCategory.MOVEMENT, Keyboard.KEY_H);
    }

    @Override
    public void onEnable() {
        double yawRadians = Math.toRadians(-Entity.getRotationYaw());
        double pitchRadians = Math.toRadians(-Entity.getRotationPitch());
        double blockDistance = 2.95;

        Entity.setPositionAndRotation(
                Entity.getPosX() + Math.sin(yawRadians) * Math.cos(pitchRadians) * blockDistance,
                Entity.getPosY() + Math.sin(pitchRadians) * blockDistance,
                Entity.getPosZ() + Math.cos(yawRadians) * Math.cos(pitchRadians) * blockDistance,
                Entity.getRotationYaw(),
                Entity.getRotationPitch()
        );

        Entity.resetMotion(true);
        this.toggle();
    }
}