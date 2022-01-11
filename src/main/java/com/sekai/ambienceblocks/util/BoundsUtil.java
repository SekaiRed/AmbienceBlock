package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.ambience.bounds.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class BoundsUtil {
    public static int lastBoundType = 4;

    //nbt stuff
    public static CompoundTag toNBT(AbstractBounds bounds) {
        CompoundTag compound = bounds.toNBT(); //get CompoundTag from bounds
        compound.putInt("type", bounds.getID()); //put type information
        return compound;
    }

    public static AbstractBounds fromNBT(CompoundTag nbt) {
        int id = nbt.getInt("type");
        AbstractBounds bounds = getBoundsFromType(id);

        bounds.fromNBT(nbt);
        return bounds;
    }

    //buffer stuff
    public static void toBuff(AbstractBounds bounds, FriendlyByteBuf buf) {
        buf.writeInt(bounds.getID());
        bounds.toBuff(buf);
    }

    public static AbstractBounds fromBuff(FriendlyByteBuf buf) {
        int id = buf.readInt();
        AbstractBounds bounds = getBoundsFromType(id);

        bounds.fromBuff(buf);
        return bounds;
    }

    public static AbstractBounds getBoundsFromType(int id) {
        if(SphereBounds.id == id) return new SphereBounds();
        if(CylinderBounds.id == id) return new CylinderBounds();
        if(CapsuleBounds.id == id) return new CapsuleBounds();
        if(CubicBounds.id == id) return new CubicBounds();
        if(NoneBounds.id == id) return new NoneBounds();

        return new SphereBounds();
    }

    public static AbstractBounds getDefault() {
        return new SphereBounds(16D);
    }
}
