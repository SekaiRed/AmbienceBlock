package com.sekai.ambienceblocks.tileentity.util;

public enum AmbienceWeather {
    CLEAR(0, "clear"),
    RAIN(1, "rain"),
    STORM(2, "storm");

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final AmbienceWeather[] vals = values();
    public AmbienceWeather next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    private final int id;
    private final String name;

    AmbienceWeather(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
