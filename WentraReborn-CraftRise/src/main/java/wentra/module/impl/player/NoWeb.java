package wentra.module.impl.player;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;

public class NoWeb extends Module {

    public NoWeb() {
        super("NoWeb", ModuleCategory.PLAYER, Keyboard.KEY_M);
        this.toggle();
    }

    @Subscribe
    public void onTick(RenderEvent e) {
        if (isToggled()) {
            Entity.setIsInWeb(Entity.getThePlayer(), false);
        }
    }
}