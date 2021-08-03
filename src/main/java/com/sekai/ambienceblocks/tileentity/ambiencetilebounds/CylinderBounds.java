package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import com.sekai.ambienceblocks.tileentity.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class CylinderBounds extends AbstractBounds {
    public static final int id = 1;

    private double radius;
    private double length;
    private AmbienceAxis axis;

    public CylinderBounds(double radius, double length, AmbienceAxis axis) {
        this.radius = radius;
        this.length = length;
        this.axis = axis;
    }

    public CylinderBounds() {
        this.radius = 0;
        this.length = 0;
        this.axis = AmbienceAxis.Y;
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

    public AmbienceAxis getAxis() {
        return axis;
    }

    public void setAxis(AmbienceAxis axis) {
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
    public boolean isWithinBounds(EntityPlayer player, Vector3d origin) {
        double dist = distanceFromCenter(player, origin);

        //return dist < radius && player.getPosY() < origin.getY() - 1 + length && player.getPosY() > origin.getY() - 1;
        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);
        return dist < radius && playerPos < originPos + length / 2D && playerPos > originPos - length / 2D;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(EntityPlayer player, Vector3d origin) {
        //Vector3d vecPlayer = new Vector3d(player.getPosX(), 0, player.getPosZ());
        //Vector3d vecTile = new Vector3d(origin.getX(), 0, origin.getZ()).add(new Vector3d(blockOffset.x, 0D, blockOffset.z));
        Vector3d axisMask = axis.getIgnoreAxisMask();

        Vector3d vecPlayer = ParsingUtil.entityVectorPos(player).mul(axisMask); //new Vector3d(player.posX * axisMask.x, player.posY * axisMask.y, player.posZ * axisMask.z);
        //Vector3d vecTile = getFixedOrigin(new Vector3d(origin.getX() * axisMask.x, origin.getY() * axisMask.y - blockOffset.y, origin.getZ() * axisMask.z));
        Vector3d vecTile = origin.mul(axisMask);

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setDouble("radius", radius);
        compound.setDouble("length", length);
        compound.setInteger("axis", axis.getId());

        return compound;
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
        this.radius = compound.getDouble("radius");
        this.length = compound.getDouble("length");
        this.axis = AmbienceAxis.getAxisFromInt(compound.getInteger("axis"));
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
        this.axis = AmbienceAxis.getAxisFromInt(buf.readInt());
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                ", length=" + length +
                '}';
    }
}
