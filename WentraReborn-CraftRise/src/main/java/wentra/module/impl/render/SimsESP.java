package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import wentra.event.impl.EventRender3D;
import wentra.event.impl.RenderEvent;
import wentra.utils.mapper.transformers.etc.EffectRendererHelper;
import wentra.utils.mapper.transformers.etc.Render2DHelper;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.render.glutils.ColorUtil;
import wentra.utils.render.glutils.GlStateManager;

import java.awt.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.GLU_FILL;

public class SimsESP extends Module {
    private String targetName;

    public SimsESP() {
        super("SimsESP", ModuleCategory.RENDER, 0);
    }


    @Subscribe
    public void onRender3DEvent(EventRender3D e) {
        try {
            List<Object> entityList = Entity.getPlayerEntitiesInWorld();

            for (Object entity : entityList) {
                if (isBot(entity)) continue;
                if (isNpc(entity)) continue;
                drawSimsESP(entity, Color.GREEN);
            }
        } catch (Exception ex) {
            System.err.println("Error in simsesp: " + ex.getMessage());
            ex.printStackTrace();
        }
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

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glEnable(2884);
        glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void disableSmoothLine() {
        glEnable(3553);
        glEnable(2929);
        GL11.glDisable(3042);
        glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawSimsESP(Object entity, Color color) {

        float pTicks = 0;
        try {
            Mapper.partialTicks.setAccessible(true);
            pTicks = Mapper.partialTicks.getFloat(Mapper.Config);
        } catch (Exception e) {
            System.err.println("Error accessing partialTicks: " + e.getMessage());
            return;
        }
        
        final double x = Entity.getLastTickPosX(entity) + (Entity.getPosX(entity) - Entity.getLastTickPosX(entity)) * pTicks
                - Entity.getRenderPosX();
        final double y = Entity.getLastTickPosY(entity) + (Entity.getPosY(entity) - Entity.getLastTickPosY(entity)) * pTicks
                - Entity.getRenderPosY() + 1.62 + (-1.5) + Math.sin((System.currentTimeMillis() % 1000000) / 333f) / 10;
        ;
        final double z = Entity.getLastTickPosZ(entity) + (Entity.getPosZ(entity) - Entity.getLastTickPosZ(entity)) * pTicks
                - Entity.getRenderPosZ();


        final int side = 4;
        final int stack = 100;

        GL11.glPushMatrix();
        ColorUtil.color(color);
        GL11.glTranslated(x, y + 2.8, z);
        enableSmoothLine(1);
        final Cylinder c = new Cylinder();
        GL11.glRotatef(90.0f, 90.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.0f, 0.22f, 0.4f, side, stack);
        disableSmoothLine();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2, z);
        GL11.glRotatef(0, 0.0f, 1.0f, 0.0f);
        enableSmoothLine(1.0f);
        GL11.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.0f, 0.22f, 0.4f, side, stack);
        disableSmoothLine();
        ColorUtil.color(0, 0, 0, 0);
        GL11.glPopMatrix();
    }

    public static Color getRainbowColor(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        return Color.getHSBColor(hue / speed, 0.9f, 1.0f);
    }
}
