package wentra.utils.mapper.transformers.etc;

import wentra.utils.mapper.Mapper;

public class GuiScreenHelper {
    public static Object gui;

    static {
        try {
            Class<?> clickGui = Class.forName("wentra.utils.render.ClickGui");
            gui = clickGui.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onDrawScreen(Object gs, int x, int y, float partialTicks) {
        try {
            Mapper.drawScreen.invoke(gs, x, y, partialTicks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onMouseClick(Object gs, int x, int y, int btn) {
        try {
            Mapper.mouseClicked.invoke(gs, x, y, btn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onMouseRelease(Object gs, int x, int y, int btn) {
        try {
            Mapper.mouseReleased.invoke(gs, x, y, btn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onKey(Object gs, char c) {
        try {
            Mapper.keyTyped.invoke(gs, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}