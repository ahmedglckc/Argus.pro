package wentra.utils.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.jhlabs.image.GaussianFilter;
import org.lwjgl.opengl.GL11;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.render.glutils.ColorUtil;
import wentra.utils.render.glutils.GlStateManager;
import wentra.utils.render.glutils.ScaledResolution;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    private static double ticks = 0;
    private static long lastFrame = 0;
    private static final float[] COLOR_CACHE = new float[4];

    private static void extractColor(int color, float[] out) {
        out[0] = (color >> 16 & 0xFF) / 255f;
        out[1] = (color >> 8 & 0xFF) / 255f;
        out[2] = (color & 0xFF) / 255f;
        out[3] = (color >> 24 & 0xFF) / 255f;
    }

    public static void resetColor() {
        glColor4f(1f, 1f, 1f, 1f);
    }

    public static void renderPlayerModelTexture(double x, double y, float u, float v, int uW, int vH, int w, int h, float tW, float tH, Object player) throws Exception {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        String texturePath = "wentra/steve.png";

        Object rl = Mapper.ResourceLocation.getConstructor(String.class)
                .newInstance(texturePath);
        Mapper.bindTexture.invoke(Entity.getTextureManager(), rl, 1, 1);

        drawScaledCustomSizeModalRect((float) x, (float) y, u, v, uW, vH, w, h, tW, tH);

        glDisable(GL_BLEND);
    }

    public static void renderPlayer2D(float x, float y, float width, float height, Object player) throws InvocationTargetException, IllegalAccessException {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Mapper.bindTexture.invoke(Entity.getTextureManager(), "wentra/steve.png", 1, 1);
        drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);

        glDisable(GL_BLEND);
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        RenderUtil.drawRect((int) left, (int) top, (int) right, (int) bottom, color);
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        try {
            Mapper.drawRect.invoke(null, (int) x, (int) y, (int) (x + width), (int) (y + height), color);
        } catch (Exception e) {
            Mapper.log("Failed to draw rectangle" + e);
        }
    }

    public static void rectangleESP(double left, double top, double right, double bottom, int color) {
        RenderUtil.drawRectESP((int) left, (int) top, (int) right, (int) bottom, color);
    }

    public static void rectangleBorderedESP(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        rectangleESP(x + width, y + width, x1 - width, y1 - width, internalColor);
        ColorUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangleESP(x + width, y, x1 - width, y + width, borderColor);
        ColorUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangleESP(x, y, x + width, y1, borderColor);
        ColorUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangleESP(x1 - width, y, x1, y1, borderColor);
        ColorUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangleESP(x + width, y1 - width, x1 - width, y1, borderColor);
        ColorUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawRectESP(int left, int top, int right, int bottom, int color) {
        try {
            Mapper.drawRect.invoke(Mapper.Gui, left, top, right, bottom, color);
        } catch (Exception e) {
            Mapper.log("Failed to draw ESP rectangle" + e);
        }
    }

    public static void drawRoundedRect2(double x, double y, double endX, double endY, double radius, int color) {
        int n2;
        float f = (float) (color >> 24 & 0xFF) / (float) 255;
        float f2 = (float) (color >> 16 & 0xFF) / (float) 255;
        float f3 = (float) (color >> 8 & 0xFF) / (float) 255;
        float f4 = (float) (color & 0xFF) / (float) 255;
        glPushAttrib(0);
        glScaled(0.5, 0.5, 0.5);
        x *= 2;
        y *= 2;
        endX *= 2;
        endY *= 2;
        glEnable(3042);
        glDisable(3553);
        glColor4f(f2, f3, f4, f);
        glEnable(2848);
        glBegin(9);
        for (n2 = 0; n2 <= 90; n2 += 3) {
            glVertex2d(x + radius + Math.sin((double) n2 * (6.5973445528769465 * 0.4761904776096344) / (double) 180) * (radius * (double) -1), y + radius + Math.cos((double) n2 * (42.5 * 0.07391982714328925) / (double) 180) * (radius * (double) -1));
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            glVertex2d(x + 0f + Math.sin((double) n2 * (0.5711986642890533 * 5.5) / (double) 180) * (0f * (double) -1), endY - 0f + Math.cos((double) n2 * (0.21052631735801697 * 14.922564993369743) / (double) 180) * (0f * (double) -1));
        }
        for (n2 = 0; n2 <= 90; n2 += 3) {
            glVertex2d(endX - 0f + Math.sin((double) n2 * (4.466951941998311 * 0.7032967209815979) / (double) 180) * (0f * (double) -1), endY - 0f + Math.cos((double) n2 * (28.33333396911621 * 0.11087973822685955) / (double) 180) * (0f * (double) -1));
        }
        for (n2 = 90; n2 <= 180; n2 += 3) {
            glVertex2d(endX - radius + Math.sin((double) n2 * ((double) 0.6f * 5.2359875479235365) / (double) 180) * radius, y + radius + Math.cos((double) n2 * (2.8529412746429443 * 1.1011767685204017) / (double) 180) * radius);
        }
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2, 2, 2);
        glPopAttrib();
    }

    public static void drawFilledCircleNoGL(int x, int y, double r, int c, int quality) {
        RenderUtil.resetColor();
        GlStateManager.setAlphaLimit(0);
        GlStateManager.setup2DRendering();
        ColorUtil.color(c);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
        GlStateManager.disable2D();
    }

    private static final HashMap<Integer, Integer> texCache = new HashMap<>();

    public static void drawGlowGradient(float x, float y, float w, float h, int r, Color c1, Color c2, Color c3, Color c4) {
        float gx = x - r;
        float gy = y - r;
        float gw = w + r * 2;
        float gh = h + r * 2;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        for (int i = r; i > 0; i--) {
            float alpha = (float) i / r * 0.4f;
            drawGradientRect(
                    gx + i, gy + i,
                    gw - i * 2, gh - i * 2,
                    new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), (int) (c1.getAlpha() * alpha)),
                    new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), (int) (c2.getAlpha() * alpha)),
                    new Color(c3.getRed(), c3.getGreen(), c3.getBlue(), (int) (c3.getAlpha() * alpha)),
                    new Color(c4.getRed(), c4.getGreen(), c4.getBlue(), (int) (c4.getAlpha() * alpha))
            );
        }

        drawGradientRect(x, y, w, h, c1, c2, c3, c4);

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(float x, float y, float w, float h, Color c1, Color c2, Color c3, Color c4) {
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.setColor(c1);
        GL11.glVertex2f(x, y);

        ColorUtil.setColor(c2);
        GL11.glVertex2f(x, y + h);

        ColorUtil.setColor(c4);
        GL11.glVertex2f(x + w, y + h);

        ColorUtil.setColor(c3);
        GL11.glVertex2f(x + w, y);

        GL11.glEnd();
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        GlStateManager.enable2D();
        GL11.glShadeModel((int) 7425);
        GL11.glBegin((int) 7);
        GlStateManager.setColor(topColor);
        GL11.glVertex2f((float) x, (float) y1);
        GL11.glVertex2f((float) x1, (float) y1);
        GlStateManager.setColor(bottomColor);
        GL11.glVertex2f((float) x1, (float) y);
        GL11.glVertex2f((float) x, (float) y);
        GL11.glEnd();
        GL11.glShadeModel((int) 7424);
        GlStateManager.disable2D();
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect(x + width, y + width, x1 - x - width * 2, y1 - y - width * 2, internalColor);
        drawRect(x + width, y, x1 - x - width * 2, width, borderColor); // Top
        drawRect(x, y, width, y1 - y, borderColor); // Left
        drawRect(x1 - width, y, width, y1 - y, borderColor); // Right
        drawRect(x + width, y1 - width, x1 - x - width * 2, width, borderColor); // Bottom
        resetColor();
    }

    public static void render(int mode, Runnable render) {
        glBegin(mode);
        try {
            render.run();
        } finally {
            glEnd();
        }
    }

    public static void setColor(int color) {
        extractColor(color, COLOR_CACHE);
        glColor4f(COLOR_CACHE[0], COLOR_CACHE[1], COLOR_CACHE[2], COLOR_CACHE[3]);
    }

    public static void setColor(int color, float alpha) {
        extractColor(color, COLOR_CACHE);
        glColor4f(COLOR_CACHE[0], COLOR_CACHE[1], COLOR_CACHE[2], alpha);
    }

    public static void setColor(double r, double g, double b, double a) {
        glColor4d(r, g, b, a);
    }

    public static void drawRect2(double x, double y, double width, double height, int color) {
        resetColor();
        GlStateManager.setup2DRendering(() -> render(GL_QUADS, () -> {
            setColor(color);
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }));
    }

    public static void drawRoundedRect(double x, double y, double endX, double endY, double radius, int color) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        try {
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_LINE_SMOOTH);
            setColor(color);
            double width = endX - x, height = endY - y;
            render(GL_POLYGON, () -> {
                for (int i = 0; i <= 90; i += 2) {
                    double angle = Math.toRadians(i);
                    glVertex2d(x + radius + Math.sin(angle) * radius, y + radius + Math.cos(angle) * radius);
                    glVertex2d(x + radius + Math.sin(angle) * radius, y + height - radius + Math.cos(angle) * radius);
                    glVertex2d(x + width - radius + Math.sin(angle) * radius, y + height - radius + Math.cos(angle) * radius);
                    glVertex2d(x + width - radius + Math.sin(angle) * radius, y + radius + Math.cos(angle) * radius);
                }
            });
        } finally {
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
            glPopAttrib();
        }
    }

    public static void drawRoundOutline2(float x, float y, float w, float h, float radius, float thickness, Color bg, Color g1, Color g2) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        try {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_LINE_SMOOTH);
            setColor(bg.getRGB());
            drawRoundedRect(x, y, x + w, y + h, radius, bg.getRGB());

            final int segments = 90;
            final float angleStep = 360f / segments;
            final float time = (System.currentTimeMillis() % 4000L) / 4000f;
            final float w2 = w / 2f, h2 = h / 2f;

            render(GL_QUADS, () -> {
                for (int i = 0; i < segments; i++) {
                    float angle1 = (float) Math.toRadians(i * angleStep);
                    float angle2 = (float) Math.toRadians((i + 1) * angleStep);
                    float cx1 = (float) Math.cos(angle1), cy1 = (float) Math.sin(angle1);
                    float cx2 = (float) Math.cos(angle2), cy2 = (float) Math.sin(angle2);

                    float tx1 = x + w2 + cx1 * (w2 + thickness);
                    float ty1 = y + h2 + cy1 * (h2 + thickness);
                    float tx2 = x + w2 + cx2 * (w2 + thickness);
                    float ty2 = y + h2 + cy2 * (h2 + thickness);
                    float ox1 = x + w2 + cx1 * w2;
                    float oy1 = y + h2 + cy1 * h2;
                    float ox2 = x + w2 + cx2 * w2;
                    float oy2 = y + h2 + cy2 * h2;

                    setColor(ColorUtil.getGradientColor(g1, g2, (time + i / (float) segments) % 1f).getRGB());
                    glVertex2f(tx1, ty1);
                    glVertex2f(ox1, oy1);
                    setColor(ColorUtil.getGradientColor(g1, g2, (time + (i + 1) / (float) segments) % 1f).getRGB());
                    glVertex2f(ox2, oy2);
                    glVertex2f(tx2, ty2);
                }
            });
        } finally {
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopAttrib();
        }
    }

    public static void drawBorder(float x, float y, float w, float h, float thickness, int color) {
        GlStateManager.prepareGL();
        try {
            setColor(color);
            render(GL_QUADS, () -> {
                glVertex2d(x, y);
                glVertex2d(x, y + thickness);
                glVertex2d(x + w, y + thickness);
                glVertex2d(x + w, y); // Top
                glVertex2d(x, y + h - thickness);
                glVertex2d(x, y + h);
                glVertex2d(x + w, y + h);
                glVertex2d(x + w, y + h - thickness); // Bottom
                glVertex2d(x, y);
                glVertex2d(x, y + h);
                glVertex2d(x + thickness, y + h);
                glVertex2d(x + thickness, y); // Left
                glVertex2d(x + w - thickness, y);
                glVertex2d(x + w - thickness, y + h);
                glVertex2d(x + w, y + h);
                glVertex2d(x + w, y); // Right
            });
        } finally {
            GlStateManager.restoreGL();
        }
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        try {
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glShadeModel(GL_SMOOTH);
            render(GL_QUADS, () -> {
                setColor(col1);
                glVertex2d(left, top);
                glVertex2d(left, bottom);
                setColor(col2);
                glVertex2d(right, bottom);
                glVertex2d(right, top);
            });
        } finally {
            glShadeModel(GL_FLAT);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopAttrib();
        }
    }

    public static void applyGradientHorizontal(float x, float y, float w, float h, float alpha, Color c1, Color c2, Runnable drawCall) {
        ScaledResolution sr = new ScaledResolution();
        int scale = sr.getScaleFactor();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        try {
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glShadeModel(GL_SMOOTH);
            render(GL_QUADS, () -> {
                setColor(c1.getRGB(), alpha);
                glVertex2f(x * scale, y * scale);
                glVertex2f(x * scale, (y + h) * scale);
                setColor(c2.getRGB(), alpha);
                glVertex2f((x + w) * scale, (y + h) * scale);
                glVertex2f((x + w) * scale, y * scale);
            });
            if (drawCall != null) drawCall.run();
        } finally {
            glShadeModel(GL_FLAT);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopAttrib();
        }
    }

    public static void drawCircle2(Object ent, float partialTicks, double radius, int color, float alpha) {
        ticks += 0.004 * (System.currentTimeMillis() - lastFrame);
        lastFrame = System.currentTimeMillis();

        double x = Entity.getPosX(ent);
        double y = Entity.getPosY(ent) + Math.sin(ticks) + 1;
        double z = Entity.getPosZ(ent);

        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        try {
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            glShadeModel(GL_SMOOTH);

            render(GL_TRIANGLE_STRIP, () -> {
                for (float i = 0; i <= Math.PI * 2; i += Math.PI / 32) {
                    double cx = x + radius * Math.cos(i);
                    double cz = z + radius * Math.sin(i);
                    setColor(color, 0);
                    glVertex3d(cx, y - Math.sin(ticks + 1) / 2.7f, cz);
                    setColor(color, 0.52f * alpha);
                    glVertex3d(cx, y, cz);
                }
            });

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth(1.5f);
            render(GL_LINE_STRIP, () -> {
                setColor(color, 0.5f * alpha);
                for (int i = 0; i <= 180; i += 2) {
                    double cx = x - Math.sin(i * Math.PI / 90) * radius;
                    double cz = z + Math.cos(i * Math.PI / 90) * radius;
                    glVertex3d(cx, y, cz);
                }
            });
        } finally {
            glShadeModel(GL_FLAT);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
            glPopAttrib();
            resetColor();
        }
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int w, int h, float tw, float th) {
        try {
            Mapper.drawModalRectWithCustomSizedTexture.invoke(null, x, y, u, v, w, h, tw, th);
        } catch (Exception e) {
            Mapper.log("Failed to draw modal rect with custom sized texture" + e);
        }
    }

    public static void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uW, float uH, float w, float h, float tw, float th) {
        try {
            Mapper.drawScaledCustomSizeModalRect.invoke(null, x, y, u, v, uW, uH, w, h, tw, th);
        } catch (Exception e) {
            Mapper.log("Failed to draw scaled custom size modal rect" + e);
        }
    }
}