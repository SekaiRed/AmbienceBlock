package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CylinderBounds extends AbstractBounds {
    public static final int id = 1;

    private double radius;
    private double height;

    public CylinderBounds(double radius, double height) {
        this.radius = radius;
        this.height = height;
    }

    public CylinderBounds() {
        this.radius = 0;
        this.height = 0;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return "Cylinder";
    }

    @Override
    public boolean isWithinBounds(PlayerEntity player, BlockPos origin) {
        double dist = distanceFromCenter(player, origin);

        return dist < radius && player.getPosY() < origin.getY() - 1 + height && player.getPosY() > origin.getY() - 1;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(PlayerEntity player, BlockPos origin) {
        Vec3d vecPlayer = new Vec3d(player.getPosX(), 0, player.getPosZ());
        Vec3d vecTile = new Vec3d(origin.getX(), 0, origin.getZ()).add(new Vec3d(blockOffset.x, 0D, blockOffset.z));

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, BlockPos origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putDouble("radius", radius);
        compound.putDouble("height", height);

        return compound;
    }

    @Override
    public void fromNBT(CompoundNBT compound) {
        this.radius = compound.getDouble("radius");
        this.height = compound.getDouble("height");
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(this.radius);
        buf.writeDouble(this.height);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.radius = buf.readDouble();
        this.height = buf.readDouble();
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                ", height=" + height +
                '}';
    }
}
