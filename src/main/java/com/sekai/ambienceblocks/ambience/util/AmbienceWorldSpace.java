package com.sekai.ambienceblocks.ambience.util;

public enum AmbienceWorldSpace implements AmbienceEnumName {
    RELATIVE(0, "Rel"),
    ABSOLUTE(1, "Abs");

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final AmbienceWorldSpace[] vals = values();
    public AmbienceWorldSpace next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final String name;

    AmbienceWorldSpace(int id, String name) {
        this.id = id;
        this.name = name;
    }
}