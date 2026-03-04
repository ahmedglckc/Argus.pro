package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.*;
import org.lwjgl.opengl.GL11;
import wentra.Main;
import wentra.event.impl.EventRender3D;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.helpers.MathHelper;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.glutils.ColorUtil;
import wentra.utils.render.glutils.GlStateManager;
import wentra.utils.render.vec3.Vec3;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class Breadcrumbs extends Module {

    public Breadcrumbs() {
        super("Breadcrumbs", ModuleCategory.RENDER, 0);
        this.toggle();
    }

    private final List<Vec3> path = new ArrayList<>();

    @Subscribe
    public void onRender3DEvent(EventRender3D e) {
        float pTicks = 0;
        try {
            Mapper.partialTicks.setAccessible(true);
            pTicks = Mapper.partialTicks.getFloat(Mapper.Config);
        } catch (Exception ev) {
            System.err.println("Error accessing partialTicks: " + ev.getMessage());
            return;
        }
        if (Entity.getLastTickPosX(Entity.getThePlayer()) * pTicks != Entity.getPosX(Entity.getThePlayer()) || Entity.getLastTickPosY(Entity.getThePlayer()) * pTicks != Entity.getPosY(Entity.getThePlayer()) || Entity.getLastTickPosZ(Entity.getThePlayer()) * pTicks != Entity.getPosZ(Entity.getThePlayer())) {
            path.add(new Vec3(Entity.getPosX(Entity.getThePlayer()), Entity.getPosY(Entity.getThePlayer()), Entity.getPosZ(Entity.getThePlayer())));
        }

        while (path.size() > 500) {
            path.remove(0);
        }
        renderLine(path);
    }

    @Override
    public void onEnable() {
        path.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        path.clear();
        super.onDisable();
    }

    public void renderLine(final List<Vec3> path) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(3);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        int count = 0;
        int alpha = 200;
        int fadeOffset = 15;
        for (Vec3 v : path) {
            if (fadeOffset > count) {
                alpha = count * (200 / fadeOffset);
            }

            RenderUtil.resetColor();
            ColorUtil.color(ColorUtil.interpolateColorsBackAndForth(15, count * 5,
                    new Color(218, 0, 252, 255), new Color(107, 59, 252), false).getRGB(), alpha / 255f);

            double x = v.xCoord - Entity.getRenderPosX();
            double y = v.yCoord - Entity.getRenderPosY();
            double z = v.zCoord - Entity.getRenderPosZ();
            GL11.glVertex3d(x, y, z);
            count++;
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopAttrib();
    }
}