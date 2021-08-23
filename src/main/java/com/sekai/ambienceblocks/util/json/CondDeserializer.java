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

        //System.out.println(json == null);//(
        //System.out.println(json.get("name") == null);

        if(json.get("name") == null)
            return CondsUtil.getDefault();

        String name = json.get("name").getAsString();
        AbstractCond cond = CondsUtil.CondList.getCondFromName(name);

        cond.fromJson(json);

        return cond;
    }
}
