package com.sekai.ambienceblocks.util;

import net.minecraft.dispenser.IPosition;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

//Remove this class and instead use the vanilla Vec3d
@Unused(type = Unused.Type.TO_FIX)
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

    //todo ....for real, why did i recreate it
    public Vector3d(Vec3d vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
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

    public Vector3d subtract(Vector3d val) {
        return new Vector3d(x - val.x, y - val.y, z - val.z);
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

    public Vector3d scale(double v) {
        return mul(v, v, v);
    }

    //TODO please fix this
    public Vec3d toVec3d() {
        return new Vec3d(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
