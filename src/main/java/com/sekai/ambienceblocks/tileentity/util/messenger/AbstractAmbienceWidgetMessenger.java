package com.sekai.ambienceblocks.tileentity.util.messenger;


/**
 * This class' goal is to hold reference to widgets without them existing
 */
public abstract class AbstractAmbienceWidgetMessenger {
    String key;
    int width;

    public String getKey() {
        return key;
    }

    public int getWidth() {
        return width;
    }
}
