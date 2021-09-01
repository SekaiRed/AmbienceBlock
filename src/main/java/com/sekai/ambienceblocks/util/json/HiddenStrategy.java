package com.sekai.ambienceblocks.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class HiddenStrategy implements ExclusionStrategy {
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(Hidden.class) != null;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(Hidden.class) != null;
    }
}
