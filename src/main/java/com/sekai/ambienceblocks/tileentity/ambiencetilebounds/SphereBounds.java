package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

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
    public boolean isWithinBounds(EntityPlayer player, Vector3d origin) {
        double dist = distanceFromCenter(player, origin);

        return dist < radius;
    }

    @Override
    public double distanceFromCenter(EntityPlayer player, Vector3d origin) {
        //Vector3d vecPlayer = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        //Vector3d vecTile = new Vector3d(origin.getX(), origin.getY(), origin.getZ()).add(blockOffset);
        Vector3d vecPlayer = ParsingUtil.entityVectorPos(player);

        return vecPlayer.distanceTo(origin);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setDouble("radius", radius);

        return compound;
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
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
