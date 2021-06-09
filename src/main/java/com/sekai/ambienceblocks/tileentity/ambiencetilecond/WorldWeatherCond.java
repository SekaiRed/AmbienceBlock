package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.util.*;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
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
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn) {
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
        equal = AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        weather = AmbienceWeather.values()[nbt.getInt(WEATHER) < AmbienceWeather.values().length ? nbt.getInt(WEATHER) : 0];
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(weather.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.weather = AmbienceWeather.values()[buf.readInt()];
    }
}
