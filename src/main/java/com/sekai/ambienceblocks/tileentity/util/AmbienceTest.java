package com.sekai.ambienceblocks.tileentity.util;

import java.util.ArrayList;

public enum AmbienceTest implements AmbienceEnumName {
    GREATER_THAN(0, ">"),
    LESSER_THAN(1, "<"),
    EQUAL_TO(2, "=");

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean testForInt(int from, int comparedTo) {
        if(this == GREATER_THAN)
            return from > comparedTo;
        if(this == LESSER_THAN)
            return from < comparedTo;
        if(this == EQUAL_TO)
            return from == comparedTo;
        return false;
    }

    public boolean testForDouble(double from, double comparedTo) {
        if(this == GREATER_THAN)
            return from > comparedTo;
        if(this == LESSER_THAN)
            return from < comparedTo;
        if(this == EQUAL_TO)
            return from == comparedTo;
        return false;
    }

    public boolean testForLong(long from, long comparedTo) {
        if(this == GREATER_THAN)
            return from > comparedTo;
        if(this == LESSER_THAN)
            return from < comparedTo;
        if(this == EQUAL_TO)
            return from == comparedTo;
        return false;
    }

    public static ArrayList<String> getStringValues() {
        ArrayList<String> list = new ArrayList<>();
        for(AmbienceTest test : values())
            list.add(test.name);
        return list;
    }

    public static AmbienceTest getValueFromString(String value) {
        for(AmbienceTest test : values())
            if(test.getName().equals(value))
                return test;
        return AmbienceTest.GREATER_THAN;
    }

    private static final AmbienceTest[] vals = values();
    public AmbienceTest next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final String name;

    AmbienceTest(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
