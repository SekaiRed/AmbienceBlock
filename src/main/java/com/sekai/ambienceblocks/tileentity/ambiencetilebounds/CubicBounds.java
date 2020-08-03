package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CubicBounds extends AbstractBounds {
    public static final int id = 2;

    private double xSize;
    private double ySize;
    private double zSize;

    public CubicBounds(double xSize, double ySize, double zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public CubicBounds() {
        this.xSize = 0;
        this.ySize = 0;
        this.zSize = 0;
    }

    private double getAverage() {
        return xSize + ySize + zSize;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return "Cubic";
    }

    @Override
    public boolean isWithinBounds(PlayerEntity player, BlockPos origin) {
        return player.getPosX() >= origin.getX() - xSize && player.getPosY() >= origin.getY() - ySize && player.getPosZ() >= origin.getZ() - zSize
                && player.getPosX() <= origin.getX() + 1 + xSize && player.getPosY() <= origin.getY() + 1 + ySize && player.getPosZ() <= origin.getZ() + 1 + zSize;
    }

    @Override
    public double distanceFromCenter(PlayerEntity player, BlockPos origin) {
        Vec3d vecPlayer = new Vec3d(player.getPosX(), player.getPosY(), player.getPosZ());
        Vec3d vecTile = new Vec3d(origin.getX(), origin.getY(), origin.getZ());

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, BlockPos origin) {
        return (getAverage() - distanceFromCenter(player, origin)) / getAverage();//distanceFromCenter(player, origin) / getAverage();
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putDouble("xSize", xSize);
        compound.putDouble("ySize", ySize);
        compound.putDouble("zSize", zSize);

        return compound;
    }

    @Override
    public void fromNBT(CompoundNBT compound) {
        this.xSize = compound.getDouble("xSize");
        this.ySize = compound.getDouble("ySize");
        this.zSize = compound.getDouble("zSize");
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(xSize);
        buf.writeDouble(ySize);
        buf.writeDouble(zSize);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        xSize = buf.readDouble();
        ySize = buf.readDouble();
        zSize = buf.readDouble();
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "x=" + xSize +
                ", y=" + ySize +
                ", z=" + zSize +
                '}';
    }

    //Getter setter
    public double getxSize() {
        return xSize;
    }

    public void setxSize(double xSize) {
        this.xSize = xSize;
    }

    public double getySize() {
        return ySize;
    }

    public void setySize(double ySize) {
        this.ySize = ySize;
    }

    public double getzSize() {
        return zSize;
    }

    public void setzSize(double zSize) {
        this.zSize = zSize;
    }
}
