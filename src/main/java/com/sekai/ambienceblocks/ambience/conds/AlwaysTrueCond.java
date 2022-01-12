package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
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
    public CompoundTag toNBT() {
        return new CompoundTag();
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {

    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {

    }

    @Override
    public void toJson(JsonObject json) {

    }

    @Override
    public void fromJson(JsonObject json) {

    }
}
