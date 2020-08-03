package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.AbstractBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.CubicBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.CylinderBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.SphereBounds;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class BoundsUtil {
    public static int lastBoundType = 2;

    //nbt stuff
    public static CompoundNBT toNBT(AbstractBounds bounds) {
        CompoundNBT compound = bounds.toNBT(); //get CompoundNBT from bounds
        compound.putInt("type", bounds.getID()); //put type information
        return compound;
    }

    public static AbstractBounds fromNBT(CompoundNBT nbt) {
        int id = nbt.getInt("type");
        AbstractBounds bounds = getBoundsFromType(id);

        if(bounds == null) return null;

        bounds.fromNBT(nbt);
        return bounds;
    }

    //buffer stuff
    public static void toBuff(AbstractBounds bounds, PacketBuffer buf) {
        buf.writeInt(bounds.getID());
        bounds.toBuff(buf);
    }

    public static AbstractBounds fromBuff(PacketBuffer buf) {
        int id = buf.readInt();
        AbstractBounds bounds = getBoundsFromType(id);

        if(bounds == null) return null;

        bounds.fromBuff(buf);
        return bounds;
    }

    public static AbstractBounds getBoundsFromType(int id) {
        if(SphereBounds.id == id) return new SphereBounds();
        if(CylinderBounds.id == id) return new CylinderBounds();
        if(CubicBounds.id == id) return new CubicBounds();

        return null;
    }
}
