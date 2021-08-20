package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceWeather;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.IWorldInfo;

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
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        IWorldInfo info = worldIn.getWorldInfo();
        boolean verified = false;
        if(weather == AmbienceWeather.CLEAR)
            verified = !info.isRaining() && !info.isThundering();
        if(weather == AmbienceWeather.RAIN)
            verified = info.isRaining() && !info.isThundering();
        if(weather == AmbienceWeather.STORM)
            verified = info.isThundering();
        return equal.testFor(verified);
    }

    //gui

    /*@Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + EQUAL, new Button(0, 0, 20, 20, new StringTextComponent(equal.getName()), button -> {
            equal = equal.next();
            button.setMessage(new StringTextComponent(equal.getName()));
        })));
        list.add(new AmbienceWidgetHolder(getName() + "." + WEATHER, new Button(0, 0, 50, 20, new StringTextComponent(weather.getName()), button -> {
            weather = weather.next();
            button.setMessage(new StringTextComponent(weather.getName()));
        })));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets) {

    }*/

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
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putInt(WEATHER, weather.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());//AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        weather = StaticUtil.getEnumValue(nbt.getInt(WEATHER), AmbienceWeather.values());//AmbienceWeather.values()[nbt.getInt(WEATHER) < AmbienceWeather.values().length ? nbt.getInt(WEATHER) : 0];
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
