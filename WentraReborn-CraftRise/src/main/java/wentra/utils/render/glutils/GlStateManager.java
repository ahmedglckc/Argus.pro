package wentra.utils.render.glutils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class GlStateManager {
    private static final int[] ENABLED_CAPS = new int[32];
    private static int enabledCapsCount = 0;
    private static final float[] COLOR_CACHE = new float[4];

    private static void extractColor(int color, float[] out) {
        out[0] = (color >> 16 & 0xFF) / 255f;
        out[1] = (color >> 8 & 0xFF) / 255f;
        out[2] = (color & 0xFF) / 255f;
        out[3] = (color >> 24 & 0xFF) / 255f;
    }

    public static void shadeModel(int mode) {
        GL11.glShadeModel(mode);
    }

    public static void setAlphaLimit(float limit) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL_GREATER, limit * 0.01f);
    }

    public static void enableBlend() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void enableLighting() {
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public static void disableLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void disableBlend() {
        glDisable(GL_BLEND);
    }

    public static void enableAlphaTest(float limit) {
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, limit * 0.01f);
    }

    public static void disableAlphaTest() {
        glDisable(GL_ALPHA_TEST);
    }

    public static void enableDepthTest() {
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    public static void disableDepthTest() {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
    }

    public static void enableTexture2D() {
        glEnable(GL_TEXTURE_2D);
    }

    public static void disableTexture2D() {
        glDisable(GL_TEXTURE_2D);
    }


    public static void setup2DRendering(Runnable f) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        f.run();
        glEnable(GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }

    public static void setup2DRendering(boolean blend) {
        if (blend) {
            enableBlend();
        }
        GlStateManager.disableTexture2D();
    }

    public static void setup2DRendering() {
        setup2DRendering(true);
    }

    public static void prepareGL() {
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        enableBlend();
        disableDepthTest();
        glDisable(GL_CULL_FACE);
    }

    public static void restoreGL() {
        glEnable(GL_CULL_FACE);
        enableDepthTest();
        disableBlend();
        glPopAttrib();
        glPopMatrix();
    }

    public static void enableTransparencyAndBlend() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        enableBlend();
        disableDepthTest();
        glEnable(GL_LINE_SMOOTH);
        disableTexture2D();
    }

    public static void disableTransparencyAndBlend() {
        disableBlend();
        enableDepthTest();
        glDisable(GL_LINE_SMOOTH);
        enableTexture2D();
        glPopAttrib();
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        glBlendFunc(srcFactor, dstFactor);
    }

    public static void resetColor() {
        glColor4f(1f, 1f, 1f, 1f);
    }

    public static void setColor(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
    }

    public static void setColor(int color) {
        extractColor(color, COLOR_CACHE);
        glColor4f(COLOR_CACHE[0], COLOR_CACHE[1], COLOR_CACHE[2], COLOR_CACHE[3]);
    }

    public static void setColor(int color, float alpha) {
        extractColor(color, COLOR_CACHE);
        glColor4f(COLOR_CACHE[0], COLOR_CACHE[1], COLOR_CACHE[2], alpha);
    }

    public static void scale(float x, float y, float z) {
        glScalef(x, y, z);
    }

    public static void enableCaps(int... caps) {
        enabledCapsCount = Math.min(caps.length, ENABLED_CAPS.length);
        System.arraycopy(caps, 0, ENABLED_CAPS, 0, enabledCapsCount);
        for (int i = 0; i < enabledCapsCount; i++) {
            glEnable(ENABLED_CAPS[i]);
        }
    }

    public static void disableCaps() {
        for (int i = 0; i < enabledCapsCount; i++) {
            glDisable(ENABLED_CAPS[i]);
        }
        enabledCapsCount = 0;
    }

    public static void render(int mode, Runnable render) {
        glBegin(mode);
        try {
            render.run();
        } finally {
            glEnd();
        }
    }

    public static void enable2D() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisable(GL_DEPTH_TEST);
        enableBlend();
        disableTexture2D();
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    }

    public static void disable2D() {
        enableTexture2D();
        disableBlend();
        enableDepthTest();
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
        glPopAttrib();
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        return result ? new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)} : null;
    }

    public static double[] convertTo2D(double var1, double var3, double var5, Object var7) {
        float pTicks = 0;
        try {
            Mapper.partialTicks.setAccessible(true);
            pTicks = Mapper.partialTicks.getFloat(Mapper.Config);
        } catch (Exception e) {
            System.err.println("Error accessing partialTicks: " + e.getMessage());
        }
        float var8 = pTicks;
        float var9 = Entity.getRotationYaw();
        float var10 = Entity.getPrevRotationYaw();
        float[] var11 = Entity.getRotationFromPosition(Entity.getLastTickPosX(var7) + (Entity.getPosX(var7) - Entity.getLastTickPosX(var7)) * (double) var8, Entity.getLastTickPosZ(var7) + (Entity.getPosZ(var7) - Entity.getLastTickPosZ(var7)) * (double) var8, Entity.getLastTickPosY(var7) + (Entity.getPosY(var7) - Entity.getLastTickPosY(var7)) * (double) var8 - 1.6D);
//        Entity var12 = mc.getRenderViewEntity();
//        Entity var13 = mc.getRenderViewEntity();
       float var14 = var11[0];
//        var13.prevRotationYaw = var14;
//        var12.rotationYaw = var14;
        double[] var15 = convertTo2D(var1, var3, var5);
//        mc.getRenderViewEntity().rotationYaw = var9;
//        mc.getRenderViewEntity().prevRotationYaw = var10;
        return var15;
    }
}