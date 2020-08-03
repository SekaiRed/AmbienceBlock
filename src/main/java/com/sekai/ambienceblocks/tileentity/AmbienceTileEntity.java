package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.Vec3d;

public class AmbienceTileEntity extends TileEntity {
    public AmbienceTileEntityData data = new AmbienceTileEntityData();

    public AmbienceTileEntity(final TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public AmbienceTileEntity() {
        super(RegistryHandler.AMBIENCE_TILE_ENTITY.get());
    }

    /*public void setServerMusicName(String musicName, ServerPlayerEntity player) {
        this.musicName = musicName;
        this.priority = 1;
        this.maxDistance = 32D;
        //PacketHandler.NET.send(PacketDistributor.PLAYER.noArg(), new PacketDebug(4));
        PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketUpdateGlobalAmbienceTE(musicName, priority, maxDistance, getPos()));
        //PacketHandler.NET.send(PacketDistributor.TargetPoint(), new MyMessage());
    }*/

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        /*compound.putString("musicName", this.musicName);
        compound.putInt("priority",this.priority);
        compound.putDouble("offDistance", this.offDistance);
        compound.putBoolean("isGlobal", this.isGlobal);*/
        data.toNBT(compound);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        data.fromNBT(compound);
        /*this.musicName = compound.getString("musicName");
        this.priority = compound.getInt("priority");
        this.offDistance = compound.getDouble("offDistance");
        this.isGlobal = compound.getBoolean("isGlobal");*/
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        data.toNBT(tag);
        /*tag.putString("musicName", this.musicName);
        tag.putInt("priority",this.priority);
        tag.putDouble("offDistance", this.offDistance);
        tag.putBoolean("isGlobal", this.isGlobal);*/
        super.write(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.read(tag);
        data.fromNBT(tag);
        /*this.musicName = tag.getString("musicName");
        this.priority = tag.getInt("priority");
        this.offDistance = tag.getDouble("offDistance");
        this.isGlobal = tag.getBoolean("isGlobal");*/
    }

    //fancy
    public boolean isWithinBounds(PlayerEntity player) {
        /*double dist = distanceTo(player);

        return dist < this.getOffDistance();*/
        return data.isWithinBounds(player, pos);
    }

    public double distanceTo(PlayerEntity player) {
        /*Vec3d vecPlayer = new Vec3d(player.getPosX(), player.getPosY(), player.getPosZ());
        Vec3d vecTile = new Vec3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());

        return vecPlayer.distanceTo(vecTile);*/
        return data.distanceFromCenter(player, pos);
    }

    //Getter and setters
    public String getMusicName() { return data.getMusicName(); }

    public int getPriority() { return data.getPriority(); }

    public boolean isUsingPriority() { return data.isUsingPriority(); }

    /*public double getOffDistance() {
        return data.getOffDistance();
    }*/

    public boolean isGlobal() {
        return data.isGlobal();
    }

    public void setMusicName(String musicName) {
        data.setMusicName(musicName);
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

}
