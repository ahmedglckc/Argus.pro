package wentra.module.setting;

import wentra.module.setting.interfaces.IRunnable;

public class RunnableSetting extends Setting {
    public String name;
    private final IRunnable runnable;

    public RunnableSetting(final String string, final IRunnable iRunnable, final Setting setting) {
        super(string);
        this.name = string;
        this.runnable = iRunnable;
    }

    public RunnableSetting(final String string, final IRunnable iRunnable) {
        this(string, iRunnable, null);
    }
}