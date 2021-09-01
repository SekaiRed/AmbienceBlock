package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

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
    public boolean isWithinBounds(EntityPlayer player, Vector3d origin) {
        return true;
    }

    @Override
    public double distanceFromCenter(EntityPlayer player, Vector3d origin) {
        return 0;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin) {
        return 1;
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        return compound;
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {

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
