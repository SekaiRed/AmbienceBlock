package com.sekai.ambienceblocks.tileentity.util.messenger;

public class AmbienceWidgetString extends AbstractAmbienceWidgetMessenger {
    String value;

    public AmbienceWidgetString(String key, int width, String value) {
        this.key = key;
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