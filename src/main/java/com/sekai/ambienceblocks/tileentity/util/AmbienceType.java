package com.sekai.ambienceblocks.tileentity.util;

public enum AmbienceType implements AmbienceEnumName {
    AMBIENT("ambient"),
    MUSIC("music");

    public String getName() {
        return name;
    }

    private final String name;

    AmbienceType(String name) {
        this.name = name;
    }
}
