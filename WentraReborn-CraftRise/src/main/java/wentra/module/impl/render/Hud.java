package wentra.module.impl.render;

import com.google.common.eventbus.Subscribe;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.ModuleManager;
import wentra.module.setting.ModeSetting;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.Setting;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.helpers.FontRenderer;
import wentra.utils.mapper.transformers.etc.Render2DHelper;
import wentra.utils.render.RenderUtil;
import wentra.utils.render.font.FontUtil;
import wentra.utils.render.glutils.ColorUtil;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Hud extends Module {
    public static long lastModuleOpen = 0;
    private static int fps, frames;
    private static long lastTime = System.currentTimeMillis();
    public Hud() {
        super("Hud", ModuleCategory.RENDER, 0);
        toggle();
    }

    @Subscribe
    public void onRender2DEvent(RenderEvent e) throws InvocationTargetException, IllegalAccessException {
        renderHud(true);
        renderArrayList(e, true);

        lastModuleOpen = System.currentTimeMillis();
        ModuleManager.checkForKeyBind();

    }

    public static void renderHud(boolean customFont) throws InvocationTargetException, IllegalAccessException {
        long now = System.currentTimeMillis();
        if (now - lastTime >= 1000) {
            fps = frames;
            frames = 0;
            lastTime = now;
        }
        frames++;

        String txt = "Wentra v1.1 - " + fps + " FPS";
        float w = customFont ? FontUtil.getStringWidth(txt) + 6 : (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, txt) + 6;
        int x = 5, y = 5;

        RenderUtil.drawRect(x - 1, y - 2, w + 2, 15, new Color(65, 65, 65, 200).getRGB());
        RenderUtil.drawRect(x, y, w, 12, new Color(30, 30, 30, 255).getRGB());

        int barH = 1, barY = y - 1, seg = 20;
        float time = (System.currentTimeMillis() % 3000) / 3000f, segW = w / seg;

        for (int i = 0; i < seg; i++) {
            float off = (i / (float) seg + time) % 1f;
            RenderUtil.drawRect(x + i * segW, barY, segW + 1, barH, ColorUtil.gradientColor(off));
        }

        if (customFont) FontUtil.drawString(txt, x + 3, y - 1, 0xFFFFFFFF, true);
        else FontRenderer.drawText(txt, x + 3, y + 2, 0xFFFFFFFF);
    }

    public static void renderArrayList(RenderEvent e, boolean customFont) throws InvocationTargetException, IllegalAccessException {
        List<Module> mods = ModuleManager.enabled();
        mods.sort((a, b) -> {
            String ta = a.name, tb = b.name;
            for (Setting s : a.settings) if (s instanceof ModeSetting) { ta += " §7[" + ((ModeSetting) s).getValue() + "]"; break; }
            for (Setting s : b.settings) if (s instanceof ModeSetting) { tb += " §7[" + ((ModeSetting) s).getValue() + "]"; break; }

            int wa = 0;
            try {
                wa = customFont ? FontUtil.getStringWidth(ta) : (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, ta);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
            int wb = 0;
            try {
                wb = customFont ? FontUtil.getStringWidth(tb) : (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, tb);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
            return Integer.compare(wb, wa);
        });

        int x = e.getScreenWidth(), y = 5;
        for (Module m : mods) {
            String txt = m.name;
            for (Setting s : m.settings) if (s instanceof ModeSetting) { txt += " §7[" + ((ModeSetting) s).getValue() + "]"; break; }

            int w = customFont ? FontUtil.getStringWidth(txt) : (int) Mapper.getStringWidth.invoke(Render2DHelper.fontRenderer, txt);

            RenderUtil.drawRect(x - w - 9, y, w + 5, 12, 0x78000000);
            RenderUtil.drawRect(x - 5, y, 1.5F, 12, Color.WHITE.getRGB());

            if (customFont) FontUtil.drawString(txt, x - w - 7, y - 1, Color.WHITE.getRGB(), true);
            else FontRenderer.drawText(txt, x - w - 7, y + 2, Color.WHITE.getRGB());

            y += 12;
        }
    }
}