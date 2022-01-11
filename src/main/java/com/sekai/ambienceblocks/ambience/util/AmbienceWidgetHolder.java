package com.sekai.ambienceblocks.ambience.util;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;

public class AmbienceWidgetHolder {
    private String key;
    private AbstractWidget widget;

    public AmbienceWidgetHolder(String key, AbstractWidget widget) {
        this.key = key;
        this.widget = widget;
    }

    public boolean isKey(String key) {
        return this.key.contains(key);
    }

    public String getKey() {
        return key;
    }

    public AbstractWidget get() {
        return widget;
    }
}
