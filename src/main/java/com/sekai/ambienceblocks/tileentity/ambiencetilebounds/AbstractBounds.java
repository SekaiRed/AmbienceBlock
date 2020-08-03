package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractBounds {
    public static final Vec3d blockOffset = new Vec3d(0.5D, 0.5D, 0.5D);

    public abstract int getID();
    public abstract String getName();

    /*protected BlockPos offset = new BlockPos(0, 0, 0);
    public BlockPos getOffset() {
        return this.offset;
    }
    public void setOffset(BlockPos pos) {
        this.offset = pos;
    }*/

    public abstract boolean isWithinBounds(PlayerEntity player, BlockPos origin);
    public abstract double distanceFromCenter(PlayerEntity player, BlockPos origin);
    public abstract double getPercentageHowCloseIsPlayer(PlayerEntity player, BlockPos origin);

    public abstract CompoundNBT toNBT();
    public abstract void fromNBT(CompoundNBT compound);

    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);
}
