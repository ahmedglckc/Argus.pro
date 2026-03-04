package wentra.module.impl.world;

import java.lang.reflect.Field;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wentra.event.impl.RenderEvent;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.Entity;
import wentra.module.Module;

public class FastPlace extends Module {

    public FastPlace() {
        super("FastPlace", ModuleCategory.WORLD, Keyboard.KEY_Z);
        this.toggle();
    }

    @Subscribe
    public void render(RenderEvent event) throws IllegalAccessException {
        Object mc = Entity.getMinecraft();

        if (Entity.getThePlayer() == null || !Mouse.isButtonDown(1)) return;

        try {
            for (Field f : Mapper.Minecraft.getDeclaredFields()) {
                if (f.getType() == int.class && !java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    if (f.getInt(mc) == 4) {
                        f.setInt(mc, 0);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Mapper.log("[FastPlace] Hata: " + e.getMessage());
        }
    }
}
