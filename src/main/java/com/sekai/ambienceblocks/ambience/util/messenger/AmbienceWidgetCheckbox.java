package com.sekai.ambienceblocks.ambience.util.messenger;

public class AmbienceWidgetCheckbox extends AbstractAmbienceWidgetMessenger {
    boolean value;

    public AmbienceWidgetCheckbox(String key, String label, int width, boolean value) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
