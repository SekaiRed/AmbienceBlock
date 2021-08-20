package com.sekai.ambienceblocks.util.json;

import com.google.gson.*;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.JsonUtil;

import java.lang.reflect.Type;

public class BoundsSerializer implements JsonSerializer<AbstractBounds> {
    @Override
    public JsonElement serialize(AbstractBounds src, Type typeOfSrc, JsonSerializationContext context) {
        //JsonObject json = (JsonObject) JsonUtil.GSON_NO_CUSTOM.toJsonTree(src);
        JsonObject json = new JsonObject();

        json.addProperty("type", src.getID());

        src.toJson(json);

        return json;
    }
}
