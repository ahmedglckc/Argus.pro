package wentra.module.setting;

public class BooleanSetting extends Setting {
    private boolean toggled;

    public BooleanSetting(String name, boolean value) {
        super(name);
        this.toggled = value;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean b) {
        this.toggled = b;
    }

    public void toggle() {
        toggled = !toggled;
    }
}
