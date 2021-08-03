package com.sekai.ambienceblocks.util;

import net.minecraft.dispenser.IPosition;
import net.minecraft.util.math.MathHelper;

public class Vector3d implements IPosition {
    public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);
    public final double x;
    public final double y;
    public final double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }

    public Vector3d add(Vector3d val) {
        return new Vector3d(x + val.x, y + val.y, z + val.z);
    }

    public double distanceTo(Vector3d vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public Vector3d mul(Vector3d vec) {
        return this.mul(vec.x, vec.y, vec.z);
    }

    public Vector3d mul(double factorX, double factorY, double factorZ) {
        return new Vector3d(this.x * factorX, this.y * factorY, this.z * factorZ);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
