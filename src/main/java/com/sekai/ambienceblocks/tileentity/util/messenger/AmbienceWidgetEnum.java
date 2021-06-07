package com.sekai.ambienceblocks.tileentity.util.messenger;

public class AmbienceWidgetEnum<E extends Enum<E>> extends AbstractAmbienceWidgetMessenger {
    E value;

    public AmbienceWidgetEnum(String key, int width, E value) {
        this.key = key;
        this.width = width;
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void next() {
        //is this tragedy even gonna work
        Class<E> clazz = (Class<E>) value.getClass();
        E[] values = clazz.getEnumConstants();
        value = values[(value.ordinal()+1) % values.length];
    }
}
