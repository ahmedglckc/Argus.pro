package wentra.module.setting;

import java.awt.Color;

public enum ModuleCategory {
    COMBAT(new Color(220, 130, 130)),
    MOVEMENT(new Color(130, 220, 130)),
    PLAYER(new Color(130, 130, 220)),
    RENDER(new Color(180, 140, 180)),
    MISC(new Color(220, 140, 40)),
    WORLD(new Color(40, 40, 180));
    private final Color color;

    ModuleCategory(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}