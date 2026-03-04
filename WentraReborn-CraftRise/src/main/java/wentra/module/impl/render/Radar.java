package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.DoubleSetting;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.NumberSetting;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.render.ColorManager;
import wentra.utils.render.Colors;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.glutils.ScaledResolution;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Radar extends Module {
    float hue;
    public static NumberSetting x = new NumberSetting("X", 5, 1, 1920);
    public static NumberSetting y = new NumberSetting("Y", 25, 1, 1080);
    public static NumberSetting sizee = new NumberSetting("Size", 100, 50, 500);
    public static DoubleSetting scale = new DoubleSetting("Scale", 1.3, 1.0, 5.0);

    public Radar() {
        super("Radar", ModuleCategory.RENDER, 0);
        this.settings.add(x);
        this.settings.add(y);
        this.settings.add(scale);
        this.settings.add(sizee);
        this.toggle();
    }

    private boolean isNpc(Object ent) {
        try {
            String name = Entity.getName(ent);
            return name.contains("[CR]") || name.equals("[CR]");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBot(Object ent) {
        try {
            float hp = Entity.getEntityHealth(ent);
            int id = Entity.getId(ent);
            return hp == 1.0f && id != 8;
        } catch (Exception e) {
            return false;
        }
    }

    @Subscribe
    public void onRender2DEvent(RenderEvent e) throws InvocationTargetException, IllegalAccessException {
        final ScaledResolution er = new ScaledResolution();
        final int size = (int) sizee.getNumber();
        final float xOffset = x.getNumber();
        final float yOffset = y.getNumber();
        final float playerOffsetX = (float) Entity.getPosX();
        final float playerOffSetZ = (float) Entity.getPosZ();

        if (this.hue > 255.0f) {
            this.hue = 0.0f;
        }
        float h = this.hue;
        float h2 = this.hue + 85.0f;
        float h3 = this.hue + 170.0f;
        if (h > 255.0f) {
            h = 0.0f;
        }
        if (h2 > 255.0f) {
            h2 -= 255.0f;
        }
        if (h3 > 255.0f) {
            h3 -= 255.0f;
        }
        final Color color33 = Color.getHSBColor(h / 255.0f, 0.9f, 1.0f);
        final Color color34 = Color.getHSBColor(h2 / 255.0f, 0.9f, 1.0f);
        final Color color35 = Color.getHSBColor(h3 / 255.0f, 0.9f, 1.0f);
        final int color36 = color33.getRGB();
        final int color37 = color34.getRGB();
        final int color38 = color35.getRGB();
        this.hue += 0.1;
        RenderUtil.rectangleBordered(xOffset, yOffset, xOffset + size, yOffset + size, 0.5, Colors.getColor(90), Colors.getColor(0));
        RenderUtil.rectangleBordered(xOffset + 1.0f, yOffset + 1.0f, xOffset + size - 1.0f, yOffset + size - 1.0f, 1.0, Colors.getColor(90), Colors.getColor(61));
        RenderUtil.rectangleBordered(xOffset + 2.5, yOffset + 2.5, xOffset + size - 2.5, yOffset + size - 2.5, 0.5, Colors.getColor(61), Colors.getColor(0));
        RenderUtil.rectangleBordered(xOffset + 3.0f, yOffset + 3.0f, xOffset + size - 3.0f, yOffset + size - 3.0f, 0.5, Colors.getColor(27), Colors.getColor(61));
        RenderUtil.drawGradientSideways(xOffset + 3.0f, yOffset + 3.0f, xOffset + size / 2, yOffset + 3.6, color36, color37);
        RenderUtil.drawGradientSideways(xOffset + size / 2, yOffset + 3.0f, xOffset + size - 3.0f, yOffset + 3.6, color37, color38);
        RenderUtil.rectangleESP(xOffset + (size / 2 - 0.5), yOffset + 3.5, xOffset + (size / 2 + 0.5), yOffset + size - 3.5, Colors.getColor(255, 80));
        RenderUtil.rectangleESP(xOffset + 3.5, yOffset + (size / 2 - 0.5), xOffset + size - 3.5, yOffset + (size / 2 + 0.5), Colors.getColor(255, 80));
        for (final Object o : Entity.getPlayerEntitiesInWorld()) {
            if (Mapper.EntityPlayer.isInstance(o)) {
                final Object ent = (Object) o;
                if (ent == Entity.getThePlayer()) {
                    continue;
                }
                if (isBot(ent)) continue;
                if (isNpc(ent)) continue;
                final float pTicks = e.getPartialTicks();
                final float posX = (float) ((Entity.getPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - playerOffsetX) * scale.getNumber());
                final float posZ = (float) ((Entity.getPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - playerOffSetZ) * scale.getNumber());
                int color39 = ColorManager.getEnemyVisible().getColorInt();
                final float cos = (float) Math.cos(Entity.getRotationYaw() * 0.017453292519943295);
                final float sin = (float) Math.sin(Entity.getRotationYaw() * 0.017453292519943295);
                float rotY = -(posZ * cos - posX * sin);
                float rotX = -(posX * cos + posZ * sin);
                if (rotY > size / 2 - 5) {
                    rotY = size / 2 - 5.0f;
                } else if (rotY < -(size / 2 - 5)) {
                    rotY = -(size / 2 - 5);
                }
                if (rotX > size / 2 - 5.0f) {
                    rotX = size / 2 - 5;
                } else if (rotX < -(size / 2 - 5)) {
                    rotX = -(size / 2 - 5.0f);
                }
                RenderUtil.rectangleBordered(xOffset + size / 2 + rotX - 1.5, yOffset + size / 2 + rotY - 1.5, xOffset + size / 2 + rotX + 1.5, yOffset + size / 2 + rotY + 1.5, 0.5, color39, Colors.getColor(46));
            }
        }
    }
}