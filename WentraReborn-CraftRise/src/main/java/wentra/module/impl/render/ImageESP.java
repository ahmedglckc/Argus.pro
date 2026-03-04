package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import wentra.event.impl.EventRender3D;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModeSetting;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.helpers.MathHelper;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.glutils.GlStateManager;
import wentra.utils.render.glutils.ScaledResolution;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;
import static wentra.utils.render.RenderUtil.rectangleBorderedESP;

public class ImageESP extends Module {
    private final DecimalFormat df = new DecimalFormat("0.#");
    private final Map<Object, double[]> entityConvertedPointsMap = new ConcurrentHashMap<>();

    private final ModeSetting mode = new ModeSetting("Image", "Alperen", "Jahrein", "Alperen", "Rakun", "Htalks", "Social Credit", "Kurdistan");

    public ImageESP() {
        super("ImageESP", ModuleCategory.RENDER, 0);
        settings.add(mode);
    }

    @Subscribe
    public void onRender2DEvent(RenderEvent partialTicks) {
        try {
            renderBox();
        } catch (Exception e) {
            System.err.println("Error in renderBoxNameTags: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onRender3DEvent(EventRender3D e) {
        try {
            updatePositions();
        } catch (Exception ex) {
            System.err.println("Error in updatePositions: " + ex.getMessage());
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

    public void renderBox() {
        glPushMatrix();
        ScaledResolution scaledRes = new ScaledResolution();
        double twoDscale = (double) scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
        GlStateManager.scale((float) twoDscale, (float) twoDscale, (float) twoDscale);
        for (Map.Entry<Object, double[]> entry : entityConvertedPointsMap.entrySet()) {
            Object entity = entry.getKey();

            if (isBot(entity)) continue;
            if (isNpc(entity)) continue;

            double[] renderPositions = this.entityConvertedPointsMap.get(entity);
            double[] renderPositionsBottom = new double[]{renderPositions[4], renderPositions[5], renderPositions[6]};
            double[] renderPositionsX = new double[]{renderPositions[7], renderPositions[8], renderPositions[9]};
            double[] renderPositionsX1 = new double[]{renderPositions[10], renderPositions[11], renderPositions[12]};
            double[] renderPositionsZ = new double[]{renderPositions[13], renderPositions[14], renderPositions[15]};
            double[] renderPositionsZ1 = new double[]{renderPositions[16], renderPositions[17], renderPositions[18]};
            double[] renderPositionsTop1 = new double[]{renderPositions[19], renderPositions[20], renderPositions[21]};
            double[] renderPositionsTop2 = new double[]{renderPositions[22], renderPositions[23], renderPositions[24]};
            boolean shouldRender = renderPositions[3] > 0.0 && renderPositions[3] <= 1.0 && renderPositionsBottom[2] > 0.0
                    && renderPositionsBottom[2] <= 1.0 && renderPositionsX[2] > 0.0 && renderPositionsX[2] <= 1.0
                    && renderPositionsX1[2] > 0.0 && renderPositionsX1[2] <= 1.0 && renderPositionsZ[2] > 0.0
                    && renderPositionsZ[2] <= 1.0 && renderPositionsZ1[2] > 0.0 && renderPositionsZ1[2] <= 1.0
                    && renderPositionsTop1[2] > 0.0 && renderPositionsTop1[2] <= 1.0 && renderPositionsTop2[2] > 0.0
                    && renderPositionsTop2[2] <= 1.0;

            if (Mapper.getDistanceToEntity != null && Entity.getThePlayer() != null) {
                try {
                    float distance = (float) Mapper.getDistanceToEntity.invoke(Entity.getThePlayer(), entity);
                    if (distance < 2.5f && renderPositionsTop1[1] < 0.0) {
                        shouldRender = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error checking distance: " + e.getMessage());
                }
            }
            if (!shouldRender)
                continue;

            glPushMatrix();
            if (Mapper.EntityClass.isInstance(entity) && !Mapper.EntityPlayerSP.isInstance(entity)) {
                try {
                    glEnable((int) 3042);
                    glDisable((int) 3553);

                    RenderUtil.drawRect((int) 0.0f, (int) 0.0f, (int) 0.0f, (int) 0.0f, this.getColor(0, 0));
                    double[] xValues = new double[]{renderPositions[0], renderPositionsBottom[0], renderPositionsX[0],
                            renderPositionsX1[0], renderPositionsZ[0], renderPositionsZ1[0], renderPositionsTop1[0],
                            renderPositionsTop2[0]};
                    double[] yValues = new double[]{renderPositions[1], renderPositionsBottom[1], renderPositionsX[1],
                            renderPositionsX1[1], renderPositionsZ[1], renderPositionsZ1[1], renderPositionsTop1[1],
                            renderPositionsTop2[1]};
                    double x = renderPositions[0];
                    double y = renderPositions[1];
                    double endx = renderPositionsBottom[0];
                    double endy = renderPositionsBottom[1];
                    for (double bdubs : xValues) {
                        if (!(bdubs < x))
                            continue;
                        x = bdubs;
                    }
                    for (double bdubs : xValues) {
                        if (!(bdubs > endx))
                            continue;
                        endx = bdubs;
                    }
                    for (double bdubs : yValues) {
                        if (!(bdubs < y))
                            continue;
                        y = bdubs;
                    }
                    for (double bdubs : yValues) {
                        if (!(bdubs > endy))
                            continue;
                        endy = bdubs;
                    }

                    Color boxColor = new Color(0, 209, 255, 255);

                    rectangleBorderedESP(x + 0.5, y + 0.5, endx - 0.5, endy - 0.5, 1.0, getColor(0, 0, 0, 0), boxColor.getRGB());

                    glEnable(GL11.GL_BLEND);
                    glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    String texturePath = "wentra/esp/alperen.png";

                    Object rl = Mapper.ResourceLocation.getConstructor(String.class)
                            .newInstance(texturePath);
                    Mapper.bindTexture.invoke(Entity.getTextureManager(), rl, 1, 1);

                    RenderUtil.drawModalRectWithCustomSizedTexture(
                            (int) (x + 0.5 - 2),
                            (int) (y + 0.5),
                            0, 0,
                            (int) (endx - x + 4),
                            (int) (endy - y),
                            (int) (endx - x + 4),
                            (int) (endy - y)
                    );

                    glDisable(GL11.GL_BLEND);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            glPopMatrix();
            GL11.glColor4f(-1.0f, -1.0f, -1.0f, -1.0f);
        }
        GL11.glScalef(-1.0f, -1.0f, -1.0f);
        GL11.glColor4f(-1.0f, -1.0f, -1.0f, -1.0f);
        glPopMatrix();
        RenderUtil.drawRectESP((int) 0.0f, (int) 0.0f, (int) 0.0f, (int) 0.0f, this.getColor(0, 0));
    }

    private void updatePositions() {
        entityConvertedPointsMap.clear();
        if (Mapper.partialTicks == null || Mapper.Config == null) {
            System.err.println("Mapper.partialTicks or Mapper.Config is null, skipping updatePositions");
            return;
        }

        float pTicks = 0;
        try {
            Mapper.partialTicks.setAccessible(true);
            pTicks = Mapper.partialTicks.getFloat(Mapper.Config);
        } catch (Exception e) {
            System.err.println("Error accessing partialTicks: " + e.getMessage());
            return;
        }

        List<Object> entities = Entity.getPlayerEntitiesInWorld();
        if (entities == null) return;

        for (Object ent : entities) {
            if (!Mapper.EntityPlayer.isInstance(ent) || ent == Entity.getThePlayer()) continue;

            try {
                double x = Entity.getLastTickPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - Entity.getViewerPosX() + 0.36;
                double y = Entity.getLastTickPosY(ent) + (Entity.getPosY(ent) - Entity.getLastTickPosY(ent)) * pTicks - Entity.getViewerPosY();
                double z = Entity.getLastTickPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - Entity.getViewerPosZ() + 0.36;
                double topY = y + 1.95;

                double[] convertedPoints = convertTo2D(x, y, z);
                if (convertedPoints == null) continue;

                double[] convertedPoints22 = convertTo2D(x - 0.36, y, z - 0.36);
                if (convertedPoints22 == null || convertedPoints22[2] < 0.0 || convertedPoints22[2] >= 1.0) continue;

                x = Entity.getLastTickPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - Entity.getViewerPosX() - 0.36;
                z = Entity.getLastTickPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - Entity.getViewerPosZ() - 0.36;
                double[] convertedPointsBottom = convertTo2D(x, y, z);

                y = Entity.getLastTickPosY(ent) + (Entity.getPosY(ent) - Entity.getLastTickPosY(ent)) * pTicks - Entity.getViewerPosY() - 0.05;
                double[] convertedPointsx = convertTo2D(x, y, z);

                x = Entity.getLastTickPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - Entity.getViewerPosX() - 0.36;
                z = Entity.getLastTickPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - Entity.getViewerPosZ() + 0.36;
                double[] convertedPointsTop1 = convertTo2D(x, topY, z);
                double[] convertedPointsx1 = convertTo2D(x, y, z);

                x = Entity.getLastTickPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - Entity.getViewerPosX() + 0.36;
                z = Entity.getLastTickPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - Entity.getViewerPosZ() + 0.36;
                double[] convertedPointsz = convertTo2D(x, y, z);

                x = Entity.getLastTickPosX(ent) + (Entity.getPosX(ent) - Entity.getLastTickPosX(ent)) * pTicks - Entity.getViewerPosX() + 0.36;
                z = Entity.getLastTickPosZ(ent) + (Entity.getPosZ(ent) - Entity.getLastTickPosZ(ent)) * pTicks - Entity.getViewerPosZ() - 0.36;
                double[] convertedPointsTop2 = convertTo2D(x, topY, z);
                double[] convertedPointsz1 = convertTo2D(x, y, z);

                if (convertedPointsBottom == null || convertedPointsx == null || convertedPointsTop1 == null ||
                        convertedPointsx1 == null || convertedPointsz == null || convertedPointsTop2 == null || convertedPointsz1 == null) {
                    continue;
                }

                entityConvertedPointsMap.put(ent, new double[]{
                        convertedPoints[0], convertedPoints[1], 0.0, convertedPoints[2],
                        convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2],
                        convertedPointsx[0], convertedPointsx[1], convertedPointsx[2],
                        convertedPointsx1[0], convertedPointsx1[1], convertedPointsx1[2],
                        convertedPointsz[0], convertedPointsz[1], convertedPointsz[2],
                        convertedPointsz1[0], convertedPointsz1[1], convertedPointsz1[2],
                        convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2],
                        convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2]
                });
            } catch (Exception e) {
                System.err.println("Error processing entity " + ent + ": " + e.getMessage());
            }
        }
    }

    private int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }
        return new int[]{Math.max(0, startPoint - 1), Math.min(fractions.length - 1, startPoint)};
    }

    private Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = color1.getRGBColorComponents(null);
        float[] rgb2 = color2.getRGBColorComponents(null);
        return new Color(
                MathHelper.clamp_float(rgb1[0] * r + rgb2[0] * ir, 0.0f, 1.0f),
                MathHelper.clamp_float(rgb1[1] * r + rgb2[1] * ir, 0.0f, 1.0f),
                MathHelper.clamp_float(rgb1[2] * r + rgb2[2] * ir, 0.0f, 1.0f)
        );
    }

    private double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    private Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colors must have equal number of elements");
        }
        int[] indices = getFractionIndices(fractions, progress);
        float[] range = {fractions[indices[0]], fractions[indices[1]]};
        Color[] colorRange = {colors[indices[0]], colors[indices[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = max > 0 ? value / max : 0;
        return blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    private int getColor(int red, int green, int blue, int alpha) {
        return (MathHelper.clamp_int(alpha, 0, 255) << 24) |
                (MathHelper.clamp_int(red, 0, 255) << 16) |
                (MathHelper.clamp_int(green, 0, 255) << 8) |
                MathHelper.clamp_int(blue, 0, 255);
    }

    private int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    private double[] convertTo2D(double x, double y, double z) {
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
}