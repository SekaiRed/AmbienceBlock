package com.sekai.ambienceblocks.ambience.util;

import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;

public class AmbienceWidgetHolder {
    private final String key;
    private final Widget widget;

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
