package com.sekai.ambienceblocks.tileentity.util.messenger;

import com.sekai.ambienceblocks.util.ParsingUtil;

import com.google.common.base.Predicate;

public class AmbienceWidgetString extends AbstractAmbienceWidgetMessenger {
    String value;

    int charLimit = 0;
    Predicate<String> validator = s -> true;

    public AmbienceWidgetString(String key, String label, int width, String value) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.value = value;
    }

    public AmbienceWidgetString(String key, String label, int width, String value, int charLimit) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.value = value;
        this.charLimit = charLimit;
    }

    public AmbienceWidgetString(String key, String label, int width, String value, int charLimit, Predicate<String> validator) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.value = value;
        this.charLimit = charLimit;
        this.validator = validator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCharLimit() {
        return charLimit;
    }

    public Predicate<String> getValidator() {
        return validator;
    }
}