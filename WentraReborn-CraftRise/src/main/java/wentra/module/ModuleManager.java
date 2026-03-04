package wentra.module;

import org.lwjgl.input.Keyboard;
import wentra.module.Module;
import wentra.utils.mapper.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private static final ModuleManager moduleManager = new ModuleManager();

    public static final List<Module> MODULES = new ArrayList<>();

    public static void addMod(Module kys) {
        MODULES.add(kys);
    }

    public static List<Module> enabled() {
        return MODULES.stream().filter(Module::isToggled).collect(Collectors.toList());
    }

    public static List<Module> listAllModules() {
        return MODULES;
    }

    public static boolean isEnabled(String name) {
        return MODULES.stream().filter(Module::isToggled).anyMatch(it -> it.name.equalsIgnoreCase(name));
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static void checkForKeyBind() {
        for (Module module : MODULES) {
            boolean isKeyDown = Keyboard.isKeyDown(module.key);
            if (Keyboard.isKeyDown(Keyboard.KEY_COMMA) || Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
                return;
            }

            if (Entity.getCurrentScreen().equals("null")) {
                if (isKeyDown && !module.keyIsPressed) {
                    module.toggle();
                    module.keyIsPressed = true;
                } else if (!isKeyDown) {
                    module.keyIsPressed = false;
                }
            }
        }
    }
}