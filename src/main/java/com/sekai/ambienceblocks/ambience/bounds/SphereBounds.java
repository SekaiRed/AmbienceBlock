package com.sekai.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
    public boolean isWithinBounds(Player player, Vec3 origin) {
        double dist = distanceFromCenter(player, origin);

        return dist < radius;
    }

    @Override
    public double distanceFromCenter(Player player, Vec3 origin) {
        Vec3 vecPlayer = new Vec3(player.getX(), player.getY(), player.getZ());

        return vecPlayer.distanceTo(origin);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putDouble("radius", radius);

        return compound;
    }

    @Override
    public void fromNBT(CompoundTag compound) {
        this.radius = compound.getDouble("radius");
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeDouble(this.radius);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.radius = buf.readDouble();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty("radius", radius);
    }

    @Override
    public void fromJson(JsonObject json) {
        radius = json.get("radius").getAsDouble();
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                '}';
    }
}
