package com.sekai.ambienceblocks.ambience.compendium;

import java.util.ArrayList;
import java.util.List;

public class BaseCompendium {
    //maybe this could have extended list...

    private final List<CompendiumEntry> entries = new ArrayList<>();

    public List<CompendiumEntry> getAllEntries() {
        return entries;
    }

    public void addEntry(CompendiumEntry entry) {
        entries.add(entry);
    }

    public void addAllEntries(List<CompendiumEntry> entries) {
        this.entries.addAll(entries);
    }

    public void removeEntry(int index) {
        entries.remove(index);
    }

    public void removeEntry(CompendiumEntry entry) {
        entries.remove(entry);
    }

    public void clear() {
        entries.clear();
    }

    //This compendium's entries are flushed to adopt the parent's registry, pretty much clone but I'm scared of default functions
    public void adopt(BaseCompendium compendium) {
        clear();
        addAllEntries(compendium.getAllEntries());
    }

    public int size() {
        return entries.size();
    }

    public boolean contains(CompendiumEntry entry) {
        return entries.contains(entry);
    }
}
