package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractCond {
    public AbstractCond() {
    }
    public abstract AbstractCond clone();

    public abstract String getName();
    public abstract String getListDescription();
    public abstract boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn);

    //gui
    public abstract List<AbstractAmbienceWidgetMessenger> getWidgets();
    //@SuppressWarnings()
    public abstract void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets);
    //public abstract List<AmbienceWidgetHolder> getWidgets();
    //public abstract void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets);

    //nbt
    public abstract CompoundNBT toNBT();
    public abstract void fromNBT(CompoundNBT nbt);

    //buff
    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);

    //json
    public abstract void toJson(JsonObject json);
    public abstract void fromJson(JsonObject json);
}