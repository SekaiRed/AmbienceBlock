package com.sekai.ambienceblocks.ambience.util.messenger;

import java.util.ArrayList;

public class AmbienceWidgetScroll extends AbstractAmbienceWidgetMessenger {
    String value;
    ArrayList<String> values;

    public AmbienceWidgetScroll(String key, String label, int width, ArrayList<String> values) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.values = values;
        this.value = values.size() > 0 ? values.get(0) : "";
    }

    public AmbienceWidgetScroll(String key, String label, int width, ArrayList<String> values, String value) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.values = values;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getValues() {
        return values;
    }
}
