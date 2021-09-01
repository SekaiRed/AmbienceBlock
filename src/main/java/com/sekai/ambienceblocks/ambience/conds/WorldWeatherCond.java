package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.*;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class WorldWeatherCond extends AbstractCond {
    private AmbienceEquality equal;
    private AmbienceWeather weather;

    private static final String EQUAL = "equal";
    private static final String WEATHER = "weather";

    public WorldWeatherCond(AmbienceEquality equal, AmbienceWeather weather) {
        this.equal = equal;
        this.weather = weather;
    }

    @Override
    public AbstractCond clone() {
        WorldWeatherCond cond = new WorldWeatherCond(equal, weather);
        return cond;
    }

    @Override
    public String getName() {
        return "world.weather";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + weather.getName();
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        WorldInfo info = worldIn.getWorldInfo();
        boolean verified = false;
        if(weather == AmbienceWeather.CLEAR)
            verified = !info.isRaining() && !info.isThundering();
        if(weather == AmbienceWeather.RAIN)
            verified = info.isRaining() && !info.isThundering();
        if(weather == AmbienceWeather.STORM)
            verified = info.isThundering();
        return equal.testFor(verified);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetEnum<>(WEATHER, "Weather :",50, weather));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(WEATHER.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                weather = (AmbienceWeather) ((AmbienceWidgetEnum) widget).getValue();
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setInteger(WEATHER, weather.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = AmbienceEquality.values()[nbt.getInteger(EQUAL) < AmbienceEquality.values().length ? nbt.getInteger(EQUAL) : 0];
        weather = AmbienceWeather.values()[nbt.getInteger(WEATHER) < AmbienceWeather.values().length ? nbt.getInteger(WEATHER) : 0];
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(weather.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.weather = StaticUtil.getEnumValue(buf.readInt(), AmbienceWeather.values());
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(WEATHER, weather.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        weather = StaticUtil.getEnumValue(json.get(WEATHER).getAsString(), AmbienceWeather.values());
    }
}
