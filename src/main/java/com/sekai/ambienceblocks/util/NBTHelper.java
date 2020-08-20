package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NBTHelper {
    public static CompoundNBT writeVec3d(Vec3d pos) {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putDouble("X", pos.getX());
        compoundnbt.putDouble("Y", pos.getY());
        compoundnbt.putDouble("Z", pos.getZ());
        return compoundnbt;
    }

    public static Vec3d readVec3d(CompoundNBT tag) {
        return new Vec3d(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }
}
