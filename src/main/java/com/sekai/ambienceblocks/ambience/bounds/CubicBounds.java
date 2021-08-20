package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

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
    public boolean isWithinBounds(PlayerEntity player, Vector3d origin) {
        //origin.add(blockOffset);
        return player.getPosX() >= origin.getX() - xSize / 2 && player.getPosY() >= origin.getY() - ySize / 2 && player.getPosZ() >= origin.getZ() - zSize / 2
                && player.getPosX() <= origin.getX() + xSize / 2 && player.getPosY() <= origin.getY() + ySize / 2 && player.getPosZ() <= origin.getZ() + zSize / 2;
    }

    @Override
    public double distanceFromCenter(PlayerEntity player, Vector3d origin) {
        double x = player.getPosX() - origin.getX(), y = player.getPosY() - origin.getY(), z = player.getPosZ() - origin.getZ();
        double sum = Math.abs(x) + Math.abs(y) + Math.abs(z);

        return sum;
    }

    public double maxDistanceFromCenter(Vector3d origin) {
        double sum = getxSize()/2 + getySize()/2 + getzSize()/2;

        return sum;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, Vector3d origin) {
        double x = Math.abs(player.getPosX() - origin.getX()), y = Math.abs(player.getPosY() - origin.getY()), z = Math.abs(player.getPosZ() - origin.getZ());
        return ((1 - (x / (xSize / 2))) * (1 - (y / (ySize / 2))) * (1 - (z / (zSize / 2))));
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
    public void toJson(JsonObject json) {
        json.addProperty("xSize", xSize);
        json.addProperty("ySize", ySize);
        json.addProperty("zSize", zSize);
    }

    @Override
    public void fromJson(JsonObject json) {
        xSize = json.get("xSize").getAsDouble();
        ySize = json.get("ySize").getAsDouble();
        zSize = json.get("zSize").getAsDouble();
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
