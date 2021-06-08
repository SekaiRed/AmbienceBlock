package com.sekai.ambienceblocks.tileentity.util;

public enum AmbiencePosition implements AmbienceEnumName {
    RELATIVE(0, "Rel"),
    ABSOLUTE(1, "Abs");

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final AmbiencePosition[] vals = values();
    public AmbiencePosition next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final String name;

    AmbiencePosition(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
