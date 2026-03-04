package wentra.module.setting;

public class DoubleSetting extends Setting {
    public int increment = (int) 1.0;

    private double number;
    private double min, max;
    public double renderPercentage, percentage;


    public DoubleSetting(String name, double value, double min, double max) {
        super(name);
        this.number = round(value);
        this.min = min;
        this.max = max;
    }

    private double round(double value) {
        int precision = 2;
        double scale = Math.pow(10, precision);
        return Math.round(value * scale) / scale;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = round(number);
    }

    public int getIncrement() {
        return increment;
    }
}
