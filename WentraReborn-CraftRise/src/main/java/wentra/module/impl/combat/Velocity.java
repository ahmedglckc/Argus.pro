package wentra.module.impl.combat;

import org.lwjgl.input.Keyboard;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT, Keyboard.KEY_N);
        this.toggle();
    }
}
