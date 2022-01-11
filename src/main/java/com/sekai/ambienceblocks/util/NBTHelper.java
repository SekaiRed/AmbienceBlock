package com.sekai.ambienceblocks.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class NBTHelper {
    public static CompoundTag writeVec3d(Vec3 pos) {
        CompoundTag compoundnbt = new CompoundTag();
        compoundnbt.putDouble("X", pos.x);
        compoundnbt.putDouble("Y", pos.y);
        compoundnbt.putDouble("Z", pos.z);
        return compoundnbt;
    }

    public static Vec3 readVec3d(CompoundTag tag) {
        return new Vec3(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }
}
