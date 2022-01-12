package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
    public boolean isWithinBounds(Player player, Vec3 origin) {
        double dist = distanceFromCenter(player, origin);

        boolean upperFlag = false, bottomFlag = false;

        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);

        if(playerPos > originPos + length / 2D)
            upperFlag = true;

        if(playerPos < originPos - length / 2D)
            bottomFlag = true;

        Vec3 vecPlayer = new Vec3(player.getX(), player.getY(), player.getZ());

        if(upperFlag) {
            Vec3 axisMask = axis.getAxisMask();

            Vec3 shiftedOrigin = new Vec3(
                    origin.x + length / 2D * axisMask.x,
                    origin.y + length / 2D * axisMask.y,
                    origin.z + length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin) < radius;
        }

        if(bottomFlag) {
            Vec3 axisMask = axis.getAxisMask();

            Vec3 shiftedOrigin = new Vec3(
                    origin.x() - length / 2D * axisMask.x,
                    origin.y() - length / 2D * axisMask.y,
                    origin.z() - length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin) < radius;
        }

        return dist < radius && playerPos < originPos + length / 2D && playerPos > originPos - length / 2D;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(Player player, Vec3 origin) {
        boolean upperFlag = false, bottomFlag = false;

        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);

        if(playerPos > originPos + length / 2D)
            upperFlag = true;

        if(playerPos < originPos - length / 2D)
            bottomFlag = true;

        Vec3 vecPlayer = new Vec3(player.getX(), player.getY(), player.getZ());

        if(upperFlag) {
            Vec3 axisMask = axis.getAxisMask();

            Vec3 shiftedOrigin = new Vec3(
                    origin.x() + length / 2D * axisMask.x,
                    origin.y() + length / 2D * axisMask.y,
                    origin.z() + length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin);
        }

        if(bottomFlag) {
            Vec3 axisMask = axis.getAxisMask();

            Vec3 shiftedOrigin = new Vec3(
                    origin.x() - length / 2D * axisMask.x,
                    origin.y() - length / 2D * axisMask.y,
                    origin.z() - length / 2D * axisMask.z
            );

            return vecPlayer.distanceTo(shiftedOrigin);
        }

        Vec3 axisMask = axis.getIgnoreAxisMask();
        //Vec3 vecTile = new Vec3(origin.getX() * axisMask.x, origin.getY() * axisMask.y, origin.getZ() * axisMask.z).add(new Vec3(blockOffset.x, blockOffset.y, blockOffset.z));
        Vec3 vecMaskedTile = origin.multiply(axisMask);
        Vec3 vecMaskedPlayer = vecPlayer.multiply(axisMask);

        return vecMaskedPlayer.distanceTo(vecMaskedTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putDouble("radius", radius);
        compound.putDouble("length", length);
        compound.putInt("axis", axis.getId());

        return compound;
    }

    @Override
    public void fromNBT(CompoundTag compound) {
        this.radius = compound.getDouble("radius");
        this.length = compound.getDouble("length");
        this.axis = AmbienceAxis.getAxisFromInt(compound.getInt("axis"));
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeDouble(this.radius);
        buf.writeDouble(this.length);
        buf.writeInt(this.axis.getId());
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
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
