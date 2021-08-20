package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class CapsuleBounds extends AbstractBounds {
    public static final int id = 2;

    private double radius;
    private double length;
    private AmbienceAxis axis;

    public CapsuleBounds(double radius, double length, AmbienceAxis axis) {
        this.radius = radius;
        this.length = length;
        this.axis = axis;
    }

    public CapsuleBounds() {
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
        return "Capsule";
    }

    @Override
    public boolean isWithinBounds(PlayerEntity player, Vector3d origin) {
        double dist = distanceFromCenter(player, origin);

        boolean upperFlag = false, bottomFlag = false;

        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);

        if(playerPos > originPos + length / 2D)
            upperFlag = true;

        if(playerPos < originPos - length / 2D)
            bottomFlag = true;

        Vector3d vecPlayer = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());

        if(upperFlag) {
            Vector3d axisMask = axis.getAxisMask();

            Vector3d shiftedOrigin = new Vector3d(
                    origin.getX() + length / 2D * axisMask.x,
                    origin.getY() + length / 2D * axisMask.y,
                    origin.getZ() + length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin) < radius;
        }

        if(bottomFlag) {
            Vector3d axisMask = axis.getAxisMask();

            Vector3d shiftedOrigin = new Vector3d(
                    origin.getX() - length / 2D * axisMask.x,
                    origin.getY() - length / 2D * axisMask.y,
                    origin.getZ() - length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin) < radius;
        }

        return dist < radius && playerPos < originPos + length / 2D && playerPos > originPos - length / 2D;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(PlayerEntity player, Vector3d origin) {
        boolean upperFlag = false, bottomFlag = false;

        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);

        if(playerPos > originPos + length / 2D)
            upperFlag = true;

        if(playerPos < originPos - length / 2D)
            bottomFlag = true;

        Vector3d vecPlayer = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());

        if(upperFlag) {
            Vector3d axisMask = axis.getAxisMask();

            Vector3d shiftedOrigin = new Vector3d(
                    origin.getX() + length / 2D * axisMask.x,
                    origin.getY() + length / 2D * axisMask.y,
                    origin.getZ() + length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin);
        }

        if(bottomFlag) {
            Vector3d axisMask = axis.getAxisMask();

            Vector3d shiftedOrigin = new Vector3d(
                    origin.getX() - length / 2D * axisMask.x,
                    origin.getY() - length / 2D * axisMask.y,
                    origin.getZ() - length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin);
        }

        Vector3d axisMask = axis.getIgnoreAxisMask();
        //Vector3d vecTile = new Vector3d(origin.getX() * axisMask.x, origin.getY() * axisMask.y, origin.getZ() * axisMask.z).add(new Vector3d(blockOffset.x, blockOffset.y, blockOffset.z));
        Vector3d vecMaskedTile = origin.mul(axisMask);
        Vector3d vecMaskedPlayer = vecPlayer.mul(axisMask);

        return vecMaskedPlayer.distanceTo(vecMaskedTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player, Vector3d origin) {
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
        this.axis = AmbienceAxis.getAxisFromInt(compound.getInt("axis"));
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
    public void toJson(JsonObject json) {
        json.addProperty("radius", radius);
        json.addProperty("length", length);
        json.addProperty("axis", axis.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        radius = json.get("radius").getAsDouble();
        length = json.get("length").getAsDouble();
        axis = StaticUtil.getEnumValue(json.get("axis").getAsString(), AmbienceAxis.values());
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                ", length=" + length +
                '}';
    }
}
