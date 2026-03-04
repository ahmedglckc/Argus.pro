package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;

public class InventoryWalk extends Module {

    public InventoryWalk() {
        super("InventoryMove", ModuleCategory.PLAYER, Keyboard.KEY_0);
        toggle();
    }

    @Subscribe
    public void onRender(RenderEvent e) throws IllegalAccessException {
        updateKeyState(Keyboard.KEY_W, 17);
        updateKeyState(Keyboard.KEY_A, 30);
        updateKeyState(Keyboard.KEY_S, 31);
        updateKeyState(Keyboard.KEY_D, 32);
        updateKeyState(Keyboard.KEY_SPACE, 57);
    }

    private void updateKeyState(int keyCode, Object keybind) {
        if(Keyboard.isKeyDown(keyCode)) {
            setKeybind(true, keybind);
        } else if(!Keyboard.isKeyDown(keyCode)) {
            setKeybind(false, keybind);
        }
    }

    private static void setKeybind(boolean value, Object keybind) {
        try {
            Mapper.setKeyBindState.setAccessible(true);
            Mapper.setKeyBindState.invoke(null, Entity.getMinecraft(), keybind, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            Mapper.log("Failed to set keybind: " + ex.getMessage());
        }
    }
}
