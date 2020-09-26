package com.sekai.ambienceblocks.tileentity.util;

import net.minecraft.client.gui.widget.Widget;

public class AmbienceWidgetHolder {
    private String key;
    private Widget widget;

    public AmbienceWidgetHolder(String key, Widget widget) {
        this.key = key;
        this.widget = widget;
    }

    public boolean isKey(String key) {
        return this.key.contains(key);
    }

    public String getKey() {
        return key;
    }

    public Widget get() {
        return widget;
    }
}
