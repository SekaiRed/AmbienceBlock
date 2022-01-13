package com.sekai.ambienceblocks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.json.*;

public class JsonUtil {
    public static final Gson GSON;// = new GsonBuilder().setPrettyPrinting().create();
    static {
        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.setPrettyPrinting();

        //builder.excludeFieldsWithoutExposeAnnotation();

        builder.addSerializationExclusionStrategy(new HiddenStrategy());

        /*builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.hasModifier(Modifier.FINAL);
                //if(f.hasModifier(Modifier.PRIVATE))
                //return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });*/

        builder.registerTypeAdapter(AbstractBounds.class, new BoundsSerializer());
        builder.registerTypeAdapter(AbstractBounds.class, new BoundsDeserializer());

        builder.registerTypeAdapter(AbstractCond.class, new CondSerializer());
        builder.registerTypeAdapter(AbstractCond.class, new CondDeserializer());

        GSON = builder.create();
    }

    public static final Gson GSON_NO_CUSTOM;// = new GsonBuilder().setPrettyPrinting().create();
    static {
        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.setPrettyPrinting();

        builder.addSerializationExclusionStrategy(new HiddenStrategy());

        //builder.excludeFieldsWithoutExposeAnnotation();

        /*builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.hasModifier(Modifier.FINAL);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });*/

        GSON_NO_CUSTOM = builder.create();
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    /*public static <T> T fromJson(String json) {
        return GSON.fromJson(json, T);
    }*/
}
