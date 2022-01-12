package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3d;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.util.CondsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public abstract boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn);

    public boolean stringValidation(String original, String compared) {
        return ParsingUtil.validateString(original, compared);
    }

    public Vec3 getPlayerPos(Player player) {
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    //gui
    public abstract List<AbstractAmbienceWidgetMessenger> getWidgets();
    //@SuppressWarnings()
    public abstract void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets);
    //public abstract List<AmbienceWidgetHolder> getWidgets();
    //public abstract void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets);

    //nbt
    public abstract CompoundTag toNBT();
    public abstract void fromNBT(CompoundTag nbt);

    //buff
    public abstract void toBuff(FriendlyByteBuf buf);
    public abstract void fromBuff(FriendlyByteBuf buf);

    //json
    public abstract void toJson(JsonObject json);
    public abstract void fromJson(JsonObject json);

    @Override
    public String toString() {
        return getListDescription();
    }
}