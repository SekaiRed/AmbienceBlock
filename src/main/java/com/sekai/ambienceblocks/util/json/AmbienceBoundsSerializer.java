package com.sekai.ambienceblocks.util.json;

import com.google.gson.*;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.AbstractBounds;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.JsonUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.JsonUtils;

import java.lang.reflect.Type;

public class AmbienceBoundsSerializer implements JsonSerializer<AbstractBounds> {
    @Override
    public JsonElement serialize(AbstractBounds src, Type typeOfSrc, JsonSerializationContext context) {
        /*if(aBoolean){
            return new JsonPrimitive(1);
        }
        return new JsonPrimitive(0);*/
        //return null;
        JsonObject jsonObject = new JsonObject();

        //CompoundNBT nbt = BoundsUtil.toNBT(src);
        /*for(String key : nbt.keySet())
            jsonObject.add(key, new JsonPrimitive(nbt.get(key)));*/

        jsonObject.add("type", new JsonPrimitive(src.getID()));
        //add other properties here through a method in json, or use nbt
        //jsonObject.add("property", new JsonPrimitive(src));
        //jsonObject.add("misc", JsonUtil.toJson(src));

        return jsonObject;
    }
}
