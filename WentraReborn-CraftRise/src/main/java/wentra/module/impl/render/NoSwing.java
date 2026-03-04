package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.ModuleManager;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;

public class NoSwing extends Module {

    public NoSwing() {
        super("NoSwing", ModuleCategory.PLAYER, Keyboard.KEY_L);
        this.toggle();
    }

    @Subscribe
    public void onTick(RenderEvent e) {
        if (isToggled() && !ModuleManager.isEnabled("KillAura")) {
            Entity.setSwingProgress(Entity.getThePlayer(), false);
        }
    }
}
