package com.sekai.ambienceblocks.tileentity.util;

public enum AmbienceEquality implements AmbienceEnumName {
    EQUAL_TO(0, "=="),
    NOT_EQUAL_TO(1, "!=");

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean testForInt(int from, int comparedTo) {
        if(this == EQUAL_TO)
            return from == comparedTo;
        if(this == NOT_EQUAL_TO)
            return from != comparedTo;
        return false;
    }

    public boolean testForDouble(double from, double comparedTo) {
        if(this == EQUAL_TO)
            return from == comparedTo;
        if(this == NOT_EQUAL_TO)
            return from != comparedTo;
        return false;
    }

    public boolean testFor(boolean result) {
        if(this == EQUAL_TO)
            return result;
        if(this == NOT_EQUAL_TO)
            return !result;
        return result;
    }

    private static final AmbienceEquality[] vals = values();
    public AmbienceEquality next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final String name;

    AmbienceEquality(int id, String name) {
        this.id = id;
        this.name = name;
    }
}