package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;

public class AmbienceTileEntity extends TileEntity implements IAmbienceSource {
    public AmbienceData data = new AmbienceData();

    public AmbienceTileEntity(final TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public AmbienceTileEntity() {
        super(RegistryHandler.AMBIENCE_TILE_ENTITY.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        data.toNBT(compound);
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        data.fromNBT(compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        data.toNBT(tag);
        super.write(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        data.fromNBT(tag);
    }

    //I have no idea if this is a good idea
    @Override
    public CompoundNBT getTileData() {
        CompoundNBT tag = new CompoundNBT();
        data.toNBT(tag);
        super.write(tag);
        return tag;
    }

    //interface
    @Override
    public AmbienceData getData() {
        return data;
    }

    public Vector3d getOrigin() {
        Vector3d oPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vector3d(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vector3d(0.5, 0.5, 0.5));
    }

    /*public boolean isWithinBounds(PlayerEntity player) {
        return data.isWithinBounds(player, getOrigin());
    }

    public double distanceTo(PlayerEntity player) {
        return data.distanceFromCenter(player, getOrigin());
    }*/
}
