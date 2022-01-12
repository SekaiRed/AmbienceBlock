package com.sekai.ambienceblocks.ambience.util;


import net.minecraft.world.phys.Vec3;

public enum AmbienceAxis {
    X(0, new Vec3(0D, 1D, 1D)),
    Y(1, new Vec3(1D, 0D, 1D)),
    Z(2, new Vec3(1D, 1D, 0D));

    public Vec3 getIgnoreAxisMask() {
        return ignoreAxisMask;
    }

    public Vec3 getAxisMask() {
        return new Vec3(Math.abs(1 - ignoreAxisMask.x), Math.abs(1 - ignoreAxisMask.y), Math.abs(1 - ignoreAxisMask.z));
    }

    public int getId() {
        return id;
    }

    public static AmbienceAxis getAxisFromInt(int id) {
        if(id == X.getId()) return X;
        if(id == Y.getId()) return Y;
        if(id == Z.getId()) return Z;
        return Y;
    }

    private static final AmbienceAxis[] vals = values();
    public AmbienceAxis next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final Vec3 ignoreAxisMask;

    AmbienceAxis(int id, Vec3 ignoreAxisMask) {
        this.id = id;
        this.ignoreAxisMask = ignoreAxisMask;
    }
}
