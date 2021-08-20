package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

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
    public boolean isWithinBounds(PlayerEntity player, Vector3d origin) {
        return true;
    }

    @Override
    public double distanceFromCenter(PlayerEntity player, Vector3d origin) {
        return 0;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, Vector3d origin) {
        return 1;
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        return compound;
    }

    @Override
    public void fromNBT(CompoundNBT compound) {

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

    @Override
    public String toString() {
        return getName();
    }
}
