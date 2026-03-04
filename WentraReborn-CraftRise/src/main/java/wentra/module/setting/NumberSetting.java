package wentra.module.setting;

public class NumberSetting extends Setting {
    private float number;
    private float min, max;

    public NumberSetting(String name, float value, float min, float max) {
        super(name);
        this.number = value;
        this.min = min;
        this.max = max;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public long getNumber() {
        return (long) number;
    }

    public void setNumber(float number) {
        this.number = number;
    }
}
