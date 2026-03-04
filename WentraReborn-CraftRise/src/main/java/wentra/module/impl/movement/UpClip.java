package wentra.module.impl.movement;

import org.lwjgl.input.Keyboard;

import wentra.module.setting.ModuleCategory;
import wentra.module.setting.NumberSetting;
import wentra.utils.mapper.Entity;
import wentra.module.Module;

public class UpClip extends Module {

    private final NumberSetting block = new NumberSetting("Block", 4.0f, 1.0f, 10.0f);

    public UpClip() {
        super("UpClip", ModuleCategory.MOVEMENT, Keyboard.KEY_UP);
        settings.add(block);
    }

    @Override
    public void onEnable() {
        Entity.resetMotion(true);
        Entity.setPositionAndRotation(Entity.getPosX(), Entity.getPosY() + block.getNumber(), Entity.getPosZ(), Entity.getRotationYaw(), Entity.getRotationPitch());
        this.toggle();
    }
}