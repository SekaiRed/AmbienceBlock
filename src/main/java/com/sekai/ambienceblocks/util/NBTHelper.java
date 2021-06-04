package com.sekai.ambienceblocks.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class NBTHelper {
    public static CompoundNBT writeVec3d(Vector3d pos) {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putDouble("X", pos.getX());
        compoundnbt.putDouble("Y", pos.getY());
        compoundnbt.putDouble("Z", pos.getZ());
        return compoundnbt;
    }

    public static Vector3d readVec3d(CompoundNBT tag) {
        return new Vector3d(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }
}
