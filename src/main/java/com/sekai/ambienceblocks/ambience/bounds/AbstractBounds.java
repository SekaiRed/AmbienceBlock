package com.sekai.ambienceblocks.ambience.bounds;

import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

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

    public abstract boolean isWithinBounds(PlayerEntity player, Vector3d origin);
    public abstract double distanceFromCenter(PlayerEntity player, Vector3d origin);
    public abstract double getPercentageHowCloseIsPlayer(PlayerEntity player, Vector3d origin);

    public abstract CompoundNBT toNBT();
    public abstract void fromNBT(CompoundNBT compound);

    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);

    //util
    /*public Vector3d getFixedOrigin(Vector3d origin) {
        return new Vector3d(origin.getX(), origin.getY(), origin.getZ()).add(blockOffset);
    }*/

    public double getPlayerPosByAxis(PlayerEntity player, AmbienceAxis axis) {
        switch(axis) {
            case X:return player.getPosX();
            case Y:return player.getPosY();
            case Z:return player.getPosZ();
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
