package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
    public boolean isWithinBounds(Player player, Vec3 origin) {
        //origin.add(blockOffset);
        return player.getX() >= origin.x() - xSize / 2 && player.getY() >= origin.y() - ySize / 2 && player.getZ() >= origin.z() - zSize / 2
                && player.getX() <= origin.x() + xSize / 2 && player.getY() <= origin.y() + ySize / 2 && player.getZ() <= origin.z() + zSize / 2;
    }

    @Override
    public double distanceFromCenter(Player player, Vec3 origin) {
        double x = player.getX() - origin.x(), y = player.getY() - origin.y(), z = player.getZ() - origin.z();
        double sum = Math.abs(x) + Math.abs(y) + Math.abs(z);

        return sum;
    }

    public double maxDistanceFromCenter(Vec3 origin) {
        double sum = getxSize()/2 + getySize()/2 + getzSize()/2;

        return sum;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        double x = Math.abs(player.getX() - origin.x()), y = Math.abs(player.getY() - origin.y()), z = Math.abs(player.getZ() - origin.z());
        return ((1 - (x / (xSize / 2))) * (1 - (y / (ySize / 2))) * (1 - (z / (zSize / 2))));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putDouble("xSize", xSize);
        compound.putDouble("ySize", ySize);
        compound.putDouble("zSize", zSize);

        return compound;
    }

    @Override
    public void fromNBT(CompoundTag compound) {
        this.xSize = compound.getDouble("xSize");
        this.ySize = compound.getDouble("ySize");
        this.zSize = compound.getDouble("zSize");
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeDouble(xSize);
        buf.writeDouble(ySize);
        buf.writeDouble(zSize);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
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
