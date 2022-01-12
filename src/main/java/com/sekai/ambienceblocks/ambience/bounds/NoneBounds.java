package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class NoneBounds extends AbstractBounds {
    public static final int id = 4;

    public NoneBounds() { }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public boolean isWithinBounds(Player player, Vec3 origin) {
        return true;
    }

    @Override
    public double distanceFromCenter(Player player, Vec3 origin) {
        return 0;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        return 1;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag();
    }

    @Override
    public void fromNBT(CompoundTag compound) {

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

    @Override
    public String toString() {
        return getName();
    }
}
