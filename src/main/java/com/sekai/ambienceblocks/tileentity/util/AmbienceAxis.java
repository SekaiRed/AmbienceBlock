package com.sekai.ambienceblocks.tileentity.util;

import com.sekai.ambienceblocks.util.Vector3d;

public enum AmbienceAxis {
    X(0, new Vector3d(0D, 1D, 1D)),
    Y(1, new Vector3d(1D, 0D, 1D)),
    Z(2, new Vector3d(1D, 1D, 0D));

    public Vector3d getIgnoreAxisMask() {
        return ignoreAxisMask;
    }

    public Vector3d getAxisMask() {
        return new Vector3d(Math.abs(1 - ignoreAxisMask.x), Math.abs(1 - ignoreAxisMask.y), Math.abs(1 - ignoreAxisMask.z));
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
    private final Vector3d ignoreAxisMask;

    AmbienceAxis(int id, Vector3d ignoreAxisMask) {
        this.id = id;
        this.ignoreAxisMask = ignoreAxisMask;
    }
}
