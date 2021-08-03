package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.tileentity.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.tileentity.util.capability.DataProvider;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class AmbienceTileEntity extends TileEntity {
    //public AmbienceTileEntityData data;
    public AmbienceTileEntityData data;

    public AmbienceTileEntity() {
        data = new AmbienceTileEntityData();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        data.fromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        data.toNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        //NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound tag = super.getUpdateTag();
        data.toNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        data.fromNBT(tag);
    }

    //fancy
    public Vector3d getOrigin() {
        Vector3d oPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vector3d(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vector3d(0.5, 0.5, 0.5));
    }

    public boolean isWithinBounds(EntityPlayer player) {
        return data.isWithinBounds(player, getOrigin());
    }

    public double distanceTo(EntityPlayer player) {
        return data.distanceFromCenter(player, getOrigin());
    }

    /*@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        //if(capability == CapabilityEnergy.ENERGY) return (T)this.storage;
        //if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)this.handler;
        //if(capability == DataProvider.DATA) return (T)this.data;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        //if(capability == CapabilityEnergy.ENERGY) return true;
        //if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        //return super.hasCapability(capability, facing);
        return capability == DataProvider.DATA || super.hasCapability(capability, facing);
    }*/

    /*public AmbienceTileEntityData data = new AmbienceTileEntityData();

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

    //fancy
    public boolean isWithinBounds(PlayerEntity player) {
        return data.isWithinBounds(player, pos);
    }

    public double distanceTo(PlayerEntity player) {
        return data.distanceFromCenter(player, pos);
    }*/
}
