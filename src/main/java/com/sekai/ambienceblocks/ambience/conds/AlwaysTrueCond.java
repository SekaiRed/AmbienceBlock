package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AlwaysTrueCond extends AbstractCond  {
    public AlwaysTrueCond() {
    }

    @Override
    public AbstractCond clone() {
        return new AlwaysTrueCond();
    }

    @Override
    public String getName() {
        return "always_true";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "]";
    }

    @Override
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        return true;
    }

    //gui

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        return new ArrayList<>();
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {

    }

    @Override
    public CompoundNBT toNBT() {
        return new CompoundNBT();
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
    }

    @Override
    public void toBuff(PacketBuffer buf) {

    }

    @Override
    public void fromBuff(PacketBuffer buf) {

    }

    @Override
    public void toJson(JsonObject json) {

    }

    @Override
    public void fromJson(JsonObject json) {

    }
}
