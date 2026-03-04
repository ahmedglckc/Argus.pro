package wentra.utils.mapper.transformers.etc.impl.filters;

import java.lang.reflect.Field;

public class FieldFilter {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FieldFilter(String key) {
        this.key = key;
    }

    public boolean isMatchWith(Field field) {
        String[] value = field.toString().split(" ");
        for (String string : key.split(" ")) {
            boolean matched = false;
            for (String s : value) {
                if (!s.contains(string))
                    continue;
                matched = true;
                break;
            }
            if (!matched)
                return false;
        }
        return true;
    }
}