package com.sekai.ambienceblocks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.json.*;

public class JsonUtil {
    public static final Gson GSON;
    static {
        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.setPrettyPrinting();

        builder.addSerializationExclusionStrategy(new HiddenStrategy());

        builder.registerTypeAdapter(AbstractBounds.class, new BoundsSerializer());
        builder.registerTypeAdapter(AbstractBounds.class, new BoundsDeserializer());

        builder.registerTypeAdapter(AbstractCond.class, new CondSerializer());
        builder.registerTypeAdapter(AbstractCond.class, new CondDeserializer());

        GSON = builder.create();
    }

    public static final Gson GSON_NO_CUSTOM;
    static {
        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.setPrettyPrinting();

        builder.addSerializationExclusionStrategy(new HiddenStrategy());

        GSON_NO_CUSTOM = builder.create();
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
}
