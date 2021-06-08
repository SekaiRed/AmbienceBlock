package com.sekai.ambienceblocks.tileentity.util.messenger;

public class AmbienceWidgetSound extends AbstractAmbienceWidgetMessenger {
    String value;

    public AmbienceWidgetSound(String key, String label, int width, String value) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
