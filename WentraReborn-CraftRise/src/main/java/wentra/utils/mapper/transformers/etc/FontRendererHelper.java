package wentra.utils.mapper.transformers.etc;

import wentra.utils.mapper.Entity;

import java.util.HashMap;
import java.util.Map;

public class FontRendererHelper {

    public static String Hook(Object fontrenderer, String text) {
        Render2DHelper.fontRenderer = fontrenderer;

        Map<String, String> replacements = new HashMap<>();
        replacements.put(Entity.getName(Entity.getThePlayer()), "§4Kurucu §cWentra§r§f");
        replacements.put("discord.craftrise.tc", "dsc.gg/wentracr§r§f");
        replacements.put("%50 indirim » www.craftrise.com", "§r§fdsc.gg/wentracr§r§f");
        replacements.put("CRAFTRISE.COM.TR", "WENTRA.DEV§r§f");
        replacements.put("www.craftrise.com", "www.wentra.dev§r§f");
        replacements.put("www.craftrise.com.tr", "www.wentra.dev§r§f");
        replacements.put("craftrise.com", "wentra.dev§r§f");
        replacements.put("craftrise.com.tr", "wentra.dev§r§f");
        replacements.put("CRAFT RISE", "WENTRA RISE§r§f");
        replacements.put("CRAFTRISE NETWORK", "WENTRA CLIENT§r§f");
        replacements.put("craftrise.tc", "wentra.dev§r§f");
        replacements.put("CraftRise", "Wentra§r§f");
        replacements.put("t.me/craftrisetc", "dsc.gg/wentracr§r§f");

        for (Map.Entry<String, String> e : replacements.entrySet()) {
            text = text.replace(e.getKey(), e.getValue());
        }

        return text;
    }

    public static String processA4(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String processA5(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String processB6(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String processFloat(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String process2Int(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String processReturnStr(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String process3Int(Object inst, String text) {
        return Hook(inst, text);
    }

    public static String processVoid(Object inst, String text) {
        return Hook(inst, text);
    }
}
