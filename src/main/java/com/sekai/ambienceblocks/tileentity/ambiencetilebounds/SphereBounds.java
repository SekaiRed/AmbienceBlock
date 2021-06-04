package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class SphereBounds extends AbstractBounds {
    public static final int id = 0;

    private double radius;

    public SphereBounds(double radius) {
        this.radius = radius;
    }

    public SphereBounds() {
        radius = 0;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return "Sphere";
    }

    @Override
    public boolean isWithinBounds(PlayerEntity player, Vector3d origin) {
        double dist = distanceFromCenter(player, origin);

        return dist < radius;
    }

    @Override
    public double distanceFromCenter(PlayerEntity player, Vector3d origin) {
        Vector3d vecPlayer = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        Vector3d vecTile = new Vector3d(origin.getX(), origin.getY(), origin.getZ()).add(blockOffset);

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, Vector3d origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putDouble("radius", radius);

        return compound;
    }

    @Override
    public void fromNBT(CompoundNBT compound) {
        this.radius = compound.getDouble("radius");
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(this.radius);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.radius = buf.readDouble();
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                '}';
    }
}
