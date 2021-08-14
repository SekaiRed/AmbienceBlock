package com.sekai.ambienceblocks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.AbstractBounds;
import com.sekai.ambienceblocks.util.json.AmbienceBoundsSerializer;

public class JsonUtil {
    static final Gson GSON;// = new GsonBuilder().setPrettyPrinting().create();
    static {
        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();

        builder.registerTypeAdapter(AbstractBounds.class, new AmbienceBoundsSerializer());

        GSON = builder.create();
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    /*public static <T> T fromJson(String json) {
        return GSON.fromJson(json, T);
    }*/
}
