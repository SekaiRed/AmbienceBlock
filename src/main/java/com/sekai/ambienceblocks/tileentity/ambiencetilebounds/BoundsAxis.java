package com.sekai.ambienceblocks.tileentity.ambiencetilebounds;

import net.minecraft.util.math.Vec3d;

public enum BoundsAxis {
    X(0, new Vec3d(0D, 1D, 1D)),
    Y(1, new Vec3d(1D, 0D, 1D)),
    Z(2, new Vec3d(1D, 1D, 0D));

    public Vec3d getIgnoreAxisMask() {
        return ignoreAxisMask;
    }

    public Vec3d getAxisMask() {
        return new Vec3d(Math.abs(1 - ignoreAxisMask.x), Math.abs(1 - ignoreAxisMask.y), Math.abs(1 - ignoreAxisMask.z));
    }

    public int getId() {
        return id;
    }

    public static BoundsAxis getAxisFromInt(int id) {
        if(id == X.getId()) return X;
        if(id == Y.getId()) return Y;
        if(id == Z.getId()) return Z;
        return Y;
    }

    private static BoundsAxis[] vals = values();
    public BoundsAxis next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final Vec3d ignoreAxisMask;

    BoundsAxis(int id, Vec3d ignoreAxisMask) {
        this.id = id;
        this.ignoreAxisMask = ignoreAxisMask;
    }
}
