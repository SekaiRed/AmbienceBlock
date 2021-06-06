package com.sekai.ambienceblocks.tileentity.util.messenger;


/**
 * This class' goal is to hold reference to widgets without them existing
 */
public abstract class AmbienceWidgetMessenger<T> {
    String key;
    T value;
}
