package wentra.module.setting;

public class Setting {
    public String name;
    public float animX;
    public float animX1;
    public float optionAnim = 0;
    public float optionAnimNow = 0;
    public Setting(String name) {
        this.name = name;
    }

    public <T> T getConfigValue() {
        return null;
    }
}
