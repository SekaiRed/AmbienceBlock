package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public abstract class AbstractBounds {
    public static final Vector3d blockOffset = new Vector3d(0.5D, 0.5D, 0.5D);

    public abstract int getID();
    public abstract String getName();

    /*protected BlockPos offset = new BlockPos(0, 0, 0);
    public BlockPos getOffset() {
        return this.offset;
    }
    public void setOffset(BlockPos pos) {
        this.offset = pos;
    }*/

    public abstract boolean isWithinBounds(EntityPlayer player, Vector3d origin);
    public abstract double distanceFromCenter(EntityPlayer player, Vector3d origin);
    public abstract double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin);

    public abstract NBTTagCompound toNBT();
    public abstract void fromNBT(NBTTagCompound compound);

    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);

    public abstract void toJson(JsonObject json);
    public abstract void fromJson(JsonObject json);

    //util
    public double getPlayerPosByAxis(EntityPlayer player, AmbienceAxis axis) {
        switch(axis) {
            case X:return player.posX;
            case Y:return player.posY;
            case Z:return player.posZ;
        }
        return 0;
    }

    public double getVec3dPosByAxis(Vector3d vec, AmbienceAxis axis) {
        switch(axis) {
            case X:return vec.getX();
            case Y:return vec.getY();
            case Z:return vec.getZ();
        }
        return 0;
    }
}
