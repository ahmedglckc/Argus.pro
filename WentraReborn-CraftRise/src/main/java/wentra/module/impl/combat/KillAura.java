package wentra.module.impl.combat;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.event.impl.TickEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.NumberSetting;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.C02Helper;
import wentra.utils.mapper.transformers.etc.Render2DHelper;
import wentra.utils.mapper.transformers.etc.impl.helpers.FontRenderer;
import wentra.utils.mapper.transformers.etc.impl.helpers.MathHelper;
import wentra.utils.mapper.transformers.etc.impl.helpers.Packet;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.animations.ContinualAnimation;
import wentra.utils.render.glutils.ColorUtil;
import wentra.utils.render.glutils.GlStateManager;
import wentra.utils.time.TimerUtil;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class KillAura extends Module {
    public static final NumberSetting max = new NumberSetting("MaxCPS", 15, 2, 15),
            min = new NumberSetting("MinCPS", 12, 1, 15),
            rng = new NumberSetting("Range", 6.0f, 3.0f, 6.0f);
    private static final ContinualAnimation anim = new ContinualAnimation();
    private final TimerUtil cpsT = new TimerUtil(), atkT = new TimerUtil();
    private final Random r = new Random();
    private int delay;
    private static Object tgt;
    private static final Constructor<?> C0A;

    static {
        try {
            C0A = Mapper.C0APacketAnimation.getDeclaredConstructor();
            C0A.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("C0A init err: " + e.getMessage());
        }
    }

    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT, Keyboard.KEY_R);
        settings.add(max);
        settings.add(min);
        settings.add(rng);
    }

    @Override
    public void onEnable() {
        reset();
        cpsT.reset();
        atkT.reset();
        super.onEnable();
    }

    private void reset() {
        delay = 1000 / (int) (min.getNumber() + r.nextInt((int) (max.getNumber() - min.getNumber()) + 1));
    }

    private float dist(Object e) {
        try {
            return e == null ? Float.MAX_VALUE : (float) Mapper.getDistanceToEntity.invoke(Entity.getThePlayer(), e);
        } catch (Exception ex) {
            Mapper.log("DistErr: " + ex.getMessage());
            return Float.MAX_VALUE;
        }
    }

    @Subscribe
    public void render(RenderEvent e) throws InvocationTargetException, IllegalAccessException {
        if (tgt == null || tgt == Entity.getThePlayer() || dist(tgt) >= rng.getNumber()) return;
        float hp = Entity.getEntityHealth(tgt);
        int id = Entity.getId(tgt);
        if (hp == 1f && id != 8) return;

        int sw = e.getScreenWidth(), sh = e.getScreenHeight();
        String name = Entity.getName(tgt);
        float percent = Math.max(0, Math.min(1, hp / 20f));
        int w;
        try {
            w = (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        int hudW = Math.max(120, w + 50), hudH = 34, x = (sw - hudW) / 2 + 20, y = (sh - hudH) / 2 + 45;
        Color c1 = ColorUtil.applyOpacity(new Color(147, 42, 42), 1f), c2 = ColorUtil.applyOpacity(new Color(126, 4, 4), 1f);
        RenderUtil.drawRect2(x, y, hudW, hudH, new Color(29, 29, 29).getRGB());
        RenderUtil.drawRect2(x + 1, y + 1, hudW - 2, hudH - 2, new Color(40, 40, 40).getRGB());
        RenderUtil.drawRect2(x + 34, y + 15, 83, 10, ColorUtil.applyOpacity(0xFF271E1D, 1f));

        float bar = (float) (83 * percent);
        anim.animate(bar, 40);
        RenderUtil.drawGradientRect(x + 34, y + 15, x + 34 + bar, y + 25, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
        RenderUtil.drawGradientRect(x + 34, y + 15, x + 34 + bar, y + 25, c1.getRGB(), c2.getRGB());

        GlStateManager.enableBlend();
        ColorUtil.color(Color.WHITE);
        //RenderUtil.renderPlayer2D(x + 3.5f, y + 3f, 28, 28, tgt);
        FontRenderer.renderTextScale(name, x + 34, y + 5, Color.WHITE.getRGB(), false, 1);

        String pctTxt = MathHelper.round(percent * 100, 0.01) + "%";
        int tw;
        try {
            tw = (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, pctTxt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        FontRenderer.renderTextScale(pctTxt, (int) (x + 34 + 41.5 - tw / 2f), y + 16, Color.WHITE.getRGB(), false, 1);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        for (Object p : Entity.getPlayerEntitiesInWorld()) {
            if (p == Entity.getThePlayer() || dist(p) >= rng.getNumber()) continue;
            float hp = Entity.getEntityHealth(p);
            int id = Entity.getId(p);
            if (hp == 1f && id != 8) continue;

            String n = Entity.getName(p);
            if (n.contains(" ") || n.matches(".*[\\\\/\\[\\]].*")) continue;

            if (atkT.hasTimeElapsed(delay, true)) {
                try {
                    tgt = p;
                    Entity.setSwingProgress(Entity.getThePlayer(), true);
                    Packet.addToSendQueue(new C02Helper(id).getPacket());
                    Packet.addToSendQueue(C0A.newInstance());
                    reset();
                } catch (Exception ex) {
                    Mapper.log("PktErr: " + ex.getMessage());
                }
                atkT.reset();
                break;
            }
        }
    }
}
