package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CylinderBounds extends AbstractBounds {
    public static final int id = 1;

    private double radius;
    private double length;
    private BoundsAxis axis;

    public CylinderBounds(double radius, double length, BoundsAxis axis) {
        this.radius = radius;
        this.length = length;
        this.axis = axis;
    }

    public CylinderBounds() {
        this.radius = 0;
        this.length = 0;
        this.axis = BoundsAxis.Y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public BoundsAxis getAxis() {
        return axis;
    }

    public void setAxis(BoundsAxis axis) {
        this.axis = axis;
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
    public boolean isWithinBounds(PlayerEntity player, Vec3d origin) {
        double dist = distanceFromCenter(player, origin);

        //return dist < radius && player.getPosY() < origin.getY() - 1 + length && player.getPosY() > origin.getY() - 1;
        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(getFixedOrigin(origin), axis);
        return dist < radius && playerPos < originPos + length / 2D && playerPos > originPos - length / 2D;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(PlayerEntity player, Vec3d origin) {
        //Vec3d vecPlayer = new Vec3d(player.getPosX(), 0, player.getPosZ());
        //Vec3d vecTile = new Vec3d(origin.getX(), 0, origin.getZ()).add(new Vec3d(blockOffset.x, 0D, blockOffset.z));
        Vec3d axisMask = axis.getIgnoreAxisMask();

        Vec3d vecPlayer = new Vec3d(player.getPosX() * axisMask.x, player.getPosY() * axisMask.y, player.getPosZ() * axisMask.z);
        Vec3d vecTile = getFixedOrigin(new Vec3d(origin.getX() * axisMask.x, origin.getY() * axisMask.y - blockOffset.y, origin.getZ() * axisMask.z));

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, Vec3d origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putDouble("radius", radius);
        compound.putDouble("length", length);
        compound.putInt("axis", axis.getId());

        return compound;
    }

    @Override
    public void fromNBT(CompoundNBT compound) {
        this.radius = compound.getDouble("radius");
        this.length = compound.getDouble("length");
        this.axis = BoundsAxis.getAxisFromInt(compound.getInt("axis"));
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(this.radius);
        buf.writeDouble(this.length);
        buf.writeInt(this.axis.getId());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.radius = buf.readDouble();
        this.length = buf.readDouble();
        this.axis = BoundsAxis.getAxisFromInt(buf.readInt());
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                ", length=" + length +
                '}';
    }
}
