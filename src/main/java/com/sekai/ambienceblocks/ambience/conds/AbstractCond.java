package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.util.CondsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractCond {
    public AbstractCond() {
    }
    public abstract AbstractCond clone();
    public AbstractCond copy() {
        return CondsUtil.fromNBT(CondsUtil.toNBT(this));
    }

    public abstract String getName();
    public abstract String getListDescription();
    public abstract boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn);

    public boolean stringValidation(String original, String compared) {
        return ParsingUtil.validateString(original, compared);
    }

    public Vector3d getPlayerPos(EntityPlayer player) {
        return new Vector3d(player.posX, player.posY, player.posZ);
    }

    //gui
    public abstract List<AbstractAmbienceWidgetMessenger> getWidgets();
    public abstract void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets);
    //public abstract List<AmbienceWidgetHolder> getWidgets();
    //public abstract void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets);

    //nbt
    public abstract NBTTagCompound toNBT();
    public abstract void fromNBT(NBTTagCompound nbt);

    //buff
    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);

    //json
    public abstract void toJson(JsonObject json);
    public abstract void fromJson(JsonObject json);

    @Override
    public String toString() {
        return getListDescription();
    }
}