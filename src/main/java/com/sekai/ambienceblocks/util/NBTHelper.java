package com.sekai.ambienceblocks.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper {
    public static NBTTagCompound writeVec3d(Vector3d pos) {
        NBTTagCompound compoundnbt = new NBTTagCompound();
        compoundnbt.setDouble("X", pos.getX());
        compoundnbt.setDouble("Y", pos.getY());
        compoundnbt.setDouble("Z", pos.getZ());
        return compoundnbt;
    }

    public static Vector3d readVec3d(NBTTagCompound tag) {
        return new Vector3d(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }
}
