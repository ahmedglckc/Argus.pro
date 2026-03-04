package wentra.module.impl.render;

import wentra.module.Module;
import wentra.module.setting.ModeSetting;
import wentra.module.setting.ModuleCategory;

public class Notifications extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Wentra", "Exhi", "SX", "Wentra");

    public Notifications() {
        super("Notifications", ModuleCategory.RENDER, 0);
        settings.add(mode);
    }
}