package wentra.module.setting;

import wentra.module.Module;
import wentra.module.impl.render.Hud;

public abstract class SimpleModule extends Module {
    private long a;
    private final DelaySetting b;
    public SimpleModule(String name, ModuleCategory category, final int integer2, final int integer3) {
        super(name, category, 0);
        this.b = new DelaySetting(integer2, integer3);
    }

    public int getActivationDelay() {
        return this.b.getSelectedDelay();
    }

    public void setLastActivationTime(final long long1) {
        this.a = long1;
    }

    public void addDelay(final int integer) {
        this.a += integer;
    }

    public void updateDelay() {
        this.addDelay(this.getActivationDelay());
    }

    @Override
    public void onEnable() {
        this.a = 0L;
        super.onEnable();
    }

    @Override
    public void onRender2DEvent() {
        if (this.a <= Hud.lastModuleOpen) {
            this.a = Hud.lastModuleOpen;
            this.updateDelay();
        }
    }
}
