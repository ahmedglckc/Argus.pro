package wentra.module.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    public String value;
    public String name;
    private String mode;
    private List<String> modes;
    public List<String> values;

    public ModeSetting(String name, String value, String... values) {
        super(name);
        this.value = value;
        this.name = name;
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    public static BooleanSetting[] a(final String... arr) {
        final BooleanSetting[] array = new BooleanSetting[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            array[i] = new BooleanSetting(arr[i] , true);
        }
        return array;
    }

    public void cycle() {
        toggle();
    }

    public String getValue() {
        return value;
    }
    public String getMode() {
        return name;
    }

    public void toggle() {
        if(values.indexOf(value) + 1 >= values.size()) {
            value = values.get(0);
        } else {
            value = values.get(values.indexOf(value) + 1);
        }
    }
}
