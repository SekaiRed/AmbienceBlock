package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class CubicBounds extends AbstractBounds {
    public static final int id = 3;

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
    public boolean isWithinBounds(EntityPlayer player, Vector3d origin) {
        //origin.add(blockOffset);
        return player.posX >= origin.getX() - xSize / 2 && player.posY >= origin.getY() - ySize / 2 && player.posZ >= origin.getZ() - zSize / 2
                && player.posX <= origin.getX() + xSize / 2 && player.posY <= origin.getY() + ySize / 2 && player.posZ <= origin.getZ() + zSize / 2;
    }

    @Override
    public double distanceFromCenter(EntityPlayer player, Vector3d origin) {
        double x = player.posX - origin.getX(), y = player.posY - origin.getY(), z = player.posZ - origin.getZ();
        double sum = Math.abs(x) + Math.abs(y) + Math.abs(z);

        return sum;
    }

    public double maxDistanceFromCenter(Vector3d origin) {
        double sum = getxSize()/2 + getySize()/2 + getzSize()/2;

        return sum;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin) {
        double x = Math.abs(player.posX - origin.getX()), y = Math.abs(player.posY - origin.getY()), z = Math.abs(player.posZ - origin.getZ());
        return ((1 - (x / (xSize / 2))) * (1 - (y / (ySize / 2))) * (1 - (z / (zSize / 2))));
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setDouble("xSize", xSize);
        compound.setDouble("ySize", ySize);
        compound.setDouble("zSize", zSize);

        return compound;
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
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
