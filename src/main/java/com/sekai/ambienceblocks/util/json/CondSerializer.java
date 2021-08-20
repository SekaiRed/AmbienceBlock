package com.sekai.ambienceblocks.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.JsonUtil;

import java.lang.reflect.Type;

public class CondSerializer implements JsonSerializer<AbstractCond> {
    @Override
    public JsonElement serialize(AbstractCond src, Type typeOfSrc, JsonSerializationContext context) {
        //JsonObject json = (JsonObject) JsonUtil.GSON_NO_CUSTOM.toJsonTree(src);
        JsonObject json = new JsonObject();

        json.addProperty("name", src.getName());

        src.toJson(json);

        return json;
    }
}