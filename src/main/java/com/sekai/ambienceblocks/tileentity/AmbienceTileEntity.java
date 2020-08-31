package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class AmbienceTileEntity extends TileEntity {
    public AmbienceTileEntityData data = new AmbienceTileEntityData();

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
    public void read(CompoundNBT compound) {
        super.read(compound);
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
    public void handleUpdateTag(CompoundNBT tag) {
        super.read(tag);
        data.fromNBT(tag);
    }

    //fancy
    public boolean isWithinBounds(PlayerEntity player) {
        return data.isWithinBounds(player, pos);
    }

    public double distanceTo(PlayerEntity player) {
        /*Vec3d vecPlayer = new Vec3d(player.getPosX(), player.getPosY(), player.getPosZ());
        Vec3d vecTile = new Vec3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());

        return vecPlayer.distanceTo(vecTile);*/
        return data.distanceFromCenter(player, pos);
    }

    //Getter and setters
    public String getMusicName() { return data.getSoundName(); }

    public int getPriority() { return data.getPriority(); }

    public boolean isUsingPriority() { return data.isUsingPriority(); }

    /*public double getOffDistance() {
        return data.getOffDistance();
    }*/

    public boolean isGlobal() {
        return data.isGlobal();
    }

    public void setMusicName(String musicName) {
        data.setSoundName(musicName);
    }

    public void setPriority(int priority) {
        data.setPriority(priority);
    }

    public void setUsePriority(boolean usePriority) { data.setUsePriority(usePriority); }

    /*public void setOffDistance(double offDistance) {
        data.setOffDistance(offDistance);
    }*/

    public void setGlobal(boolean global) {
        data.setGlobal(global);
    }

    public int getDelay() {
        int min = data.getMinDelay(), max = data.getMaxDelay();
        if(min > max || min == max) return max;
        return (int) ((max - min) * Math.random() + min);
    }
}
