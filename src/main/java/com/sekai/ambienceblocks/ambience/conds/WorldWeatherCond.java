package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceWeather;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        boolean verified = false;
        if(weather == AmbienceWeather.CLEAR)
            verified = !worldIn.isRaining() && !worldIn.isThundering();
        if(weather == AmbienceWeather.RAIN)
            verified = worldIn.isRaining() && !worldIn.isThundering();
        if(weather == AmbienceWeather.STORM)
            verified = worldIn.isThundering();
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
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putInt(WEATHER, weather.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());//AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        weather = StaticUtil.getEnumValue(nbt.getInt(WEATHER), AmbienceWeather.values());//AmbienceWeather.values()[nbt.getInt(WEATHER) < AmbienceWeather.values().length ? nbt.getInt(WEATHER) : 0];
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(weather.ordinal());
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
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
