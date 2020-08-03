package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NBTHelper {
    public static CompoundNBT toNBT(Object o) {
        if(o instanceof ItemStack)
            return writeItemStack((ItemStack)o);

        //if(o instanceof AmbienceTileEntity)
        //    return writeAmbienceTile((AmbienceTileEntity)o);

        return null;
    }

    /*private static CompoundNBT writeAmbienceTile(AmbienceTileEntity o){
        CompoundNBT compound = new CompoundNBT();
        compound.putString("musicName", o.musicName);
        compound.putInt("priority",o.priority);
        compound.putDouble("maxDistance", o.offDistance);
        return compound;
    }*/

    private static CompoundNBT writeItemStack(ItemStack o) {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("count", o.getCount());
        compound.putString("item", o.getItem().getRegistryName().toString());
        compound.putByte("type", (byte)0);
        return compound;
    }

    @Nullable
    public static Object fromNBT(@Nonnull CompoundNBT compound) {
        switch (compound.getByte("type")) {
            case 0:
                return readItemStack(compound);
            default:
                return null;
        }
    }

    private static ItemStack readItemStack(CompoundNBT compound) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("item")));
        int count = compound.getInt("count");
        return new ItemStack(item, count);
    }
}
