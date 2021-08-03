package com.sekai.ambienceblocks.util;

public @interface Unused {
    Type type();
    enum Type {
        ARCHIVED, //kept for reference but not used
        REMOVE, //should be removed asap but first check it doesn't break stuff
        POTENTIAL, //isn't used now but could be useful later
        TO_FIX //needs to be fixed but isn't critical
    }
}
