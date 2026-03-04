package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.event.impl.TickEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.BooleanSetting;
import wentra.utils.mapper.Entity;

public class Strafe extends Module {

    public Strafe() {
        super("Strafe", ModuleCategory.MOVEMENT, Keyboard.KEY_V);
        this.toggle();
    }


    @Subscribe
    public void onTick(TickEvent event) {
        double x = Entity.getMotionX();
        double z = Entity.getMotionZ();
        double shotSpeed = Math.sqrt(x * x + z * z);

        float rotationYaw = Entity.getRotationYaw();

        if (Entity.getMoveForward() < 0.0f) {
            rotationYaw += 180.0f;
        }
        float f2 = 1.0f;
        if (Entity.getMoveForward() < 0.0f) {
            f2 = -0.5f;
        } else if (Entity.getMoveForward() > 0.0f) {
            f2 = 0.5f;
        }

        if (Entity.getMoveStrafe() > 0.0f) {
            rotationYaw -= 90.0f * f2;
        }
        if (Entity.getMoveStrafe() < 0.0f) {
            rotationYaw += 90.0f * f2;
        }

        double direction = degreesToRadians(rotationYaw);

        Entity.setMotionX(-Math.sin(direction) * shotSpeed);
        Entity.setMotionZ(Math.cos(direction) * shotSpeed);
    }

    float degreesToRadians(float degrees) {
        return degrees * (3.14159265358979323846f / 180.0f);
    }
}
