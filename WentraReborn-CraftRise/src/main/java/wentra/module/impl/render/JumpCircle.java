package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.EventRender3D;
import wentra.module.Module;
import wentra.module.setting.DoubleSetting;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.transformers.etc.impl.helpers.MathHelper;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.animations.Animation;
import wentra.utils.render.animations.Direction;
import wentra.utils.render.animations.impl.DecelerateAnimation;
import wentra.utils.render.glutils.ColorUtil;
import wentra.utils.render.glutils.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class JumpCircle extends Module {
    private boolean inAir = false;
    private static final DoubleSetting radius = new DoubleSetting("Radius", 1.5, 1, 10 );
    private final List<Circle> circles = new ArrayList<>();
    private final List<Circle> toRemove = new ArrayList<>();
    public JumpCircle() {
        super("JumpCircle", ModuleCategory.RENDER, Keyboard.KEY_J);
        this.toggle();
    }

    @Subscribe
    public void onRender3DEvent(EventRender3D e) {
        if (!Entity.onGround()) {
            inAir = true;
        } else if (Entity.onGround() && inAir) {
            circles.add(new Circle(Entity.getPosX(), Entity.getPosY(), Entity.getPosZ()));
            inAir = false;
        }

        for (Circle circle : circles) {
            circle.drawCircle();
            if (circle.fadeAnimation != null && circle.fadeAnimation.finished(Direction.BACKWARDS)) {
                toRemove.add(circle);
            }
        }

        for (Circle circle : toRemove) {
            circles.remove(circle);
        }
    }

    private static class Circle {
        private final float x, y, z;
        private final Animation expandAnimation;
        private final Animation fadeAnimation;


        public Circle(double x, double y, double z) {
            this.x = (float) x;
            this.y = (float) y;
            this.z = (float) z;
            this.fadeAnimation = new DecelerateAnimation(600, 1);
            this.expandAnimation = new DecelerateAnimation(1000, radius.getNumber());
        }


        public void drawCircle() {
            if (expandAnimation.getOutput() > (radius.getNumber() * .7f)) {
                fadeAnimation.setDirection(Direction.BACKWARDS);
            }

            glPushMatrix();
            GlStateManager.setAlphaLimit(0);

            float animation = expandAnimation.getOutput().floatValue();
            float fade = fadeAnimation.getOutput().floatValue();
            GlStateManager.setup2DRendering();
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glShadeModel(GL_SMOOTH);
            double pi2 = MathHelper.PI2;

            double xVal = x - Entity.getRenderPosX();
            double yVal = y - Entity.getRenderPosY();
            double zVal = z - Entity.getRenderPosZ();

            glBegin(GL_TRIANGLE_STRIP);

            int color1 = new Color(218, 0, 252, 255).getRGB();
            int color2 = new Color(107, 59, 252).getRGB();

            float newAnim = (float) Math.max(0, animation - (.3f * (animation / expandAnimation.getEndPoint())));

            for (int i = 0; i <= 90; ++i) {
                float value = (float) Math.sin((i * 4) * (MathHelper.PI / 180));
                int color = ColorUtil.interpolateColor(color1, color2, Math.abs(value));
                ColorUtil.color(color, fade * .6f);
                glVertex3d(xVal + animation * Math.cos(i * pi2 / 45), yVal, zVal + animation * Math.sin(i * pi2 / 45));


                ColorUtil.color(color, fade * .15f);
                glVertex3d(xVal + newAnim * Math.cos(i * pi2 / 45), yVal, zVal + newAnim * Math.sin(i * pi2 / 45));
            }

            glEnd();


            glShadeModel(GL_FLAT);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            GlStateManager.end2DRendering();
            glPopMatrix();
            RenderUtil.resetColor();
            ColorUtil.color(-1);

        }

    }

}
