package com.sekai.ambienceblocks.util.json;

import com.google.gson.*;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.CondsUtil;

import java.lang.reflect.Type;

public class CondDeserializer implements JsonDeserializer<AbstractCond> {
    @Override
    public AbstractCond deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //return null;
        JsonObject json = jsonElement.getAsJsonObject();

        String name = json.get("name").getAsString();
        AbstractCond cond = CondsUtil.CondList.getCondFromName(name);

        cond.fromJson(json);

        return cond;
    }
}
