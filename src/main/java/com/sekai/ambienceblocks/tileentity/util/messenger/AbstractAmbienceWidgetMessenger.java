package com.sekai.ambienceblocks.tileentity.util.messenger;


/**
 * This class' goal is to hold reference to widgets without them existing server side
 */
public abstract class AbstractAmbienceWidgetMessenger {
    String key;
    String label;
    int width;

    public String getKey() {
        return key;
    }

    public int getWidth() {
        return width;
    }

    public String getLabel() {
        return label;
    }
}
