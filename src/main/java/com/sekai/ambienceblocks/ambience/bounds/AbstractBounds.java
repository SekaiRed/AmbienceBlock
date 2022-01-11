package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractBounds {
    public static final Vec3 blockOffset = new Vec3(0.5D, 0.5D, 0.5D);

    public abstract int getID();
    public abstract String getName();

    /*protected BlockPos offset = new BlockPos(0, 0, 0);
    public BlockPos getOffset() {
        return this.offset;
    }
    public void setOffset(BlockPos pos) {
        this.offset = pos;
    }*/

    public abstract boolean isWithinBounds(Player player, Vec3 origin);
    public abstract double distanceFromCenter(Player player, Vec3 origin);
    public abstract double getPercentageHowCloseIsPlayer(Player player, Vec3 origin);

    //NBT
    public abstract CompoundTag toNBT();
    public abstract void fromNBT(CompoundTag compound);

    //Packet
    public abstract void toBuff(FriendlyByteBuf buf);
    public abstract void fromBuff(FriendlyByteBuf buf);

    //JSON
    public abstract void toJson(JsonObject json);
    public abstract void fromJson(JsonObject json);

    //util
    /*public Vec3 getFixedOrigin(Vec3 origin) {
        return new Vec3(origin.getX(), origin.getY(), origin.getZ()).add(blockOffset);
    }*/

    public double getPlayerPosByAxis(Player player, AmbienceAxis axis) {
        switch(axis) {
            case X:return player.getX();
            case Y:return player.getY();
            case Z:return player.getZ();
        }
        return 0;
    }

    public double getVec3dPosByAxis(Vec3 vec, AmbienceAxis axis) {
        switch(axis) {
            case X:return vec.x;
            case Y:return vec.y;
            case Z:return vec.z;
        }
        return 0;
    }
}
