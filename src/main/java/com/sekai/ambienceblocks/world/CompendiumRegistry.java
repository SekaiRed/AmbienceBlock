package com.sekai.ambienceblocks.world;

import java.util.ArrayList;
import java.util.List;

//TODO don't make it client only, it needs to exist both client and server side and is only updated through packets sent on joining world and updating
public class CompendiumRegistry {
    public final List<CompendiumEntry> entries = new ArrayList<>();

    public CompendiumRegistry() {
    }

    //called when the world begins
    public void init(String savePath) {

    }

    //called when the world ends, write the entries saved to disk
    public void end(String savePath) {

    }

    public void clear() {
        entries.clear();
    }
}
