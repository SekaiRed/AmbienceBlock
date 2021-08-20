package com.sekai.ambienceblocks.util.json;

import com.google.gson.*;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.util.BoundsUtil;

import java.lang.reflect.Type;

public class BoundsDeserializer implements JsonDeserializer<AbstractBounds> {
    @Override
    public AbstractBounds deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();

        int type = json.get("type").getAsInt();
        AbstractBounds bounds = BoundsUtil.getBoundsFromType(type);

        bounds.fromJson(json);

        return bounds;
    }
}
