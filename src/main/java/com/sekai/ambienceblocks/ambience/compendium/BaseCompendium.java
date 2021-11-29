package com.sekai.ambienceblocks.ambience.compendium;

import com.sekai.ambienceblocks.config.AmbienceConfig;

import java.util.ArrayList;
import java.util.List;

public class BaseCompendium {
    //maybe this could have extended list...

    private final List<CompendiumEntry> entries = new ArrayList<>();

    public List<CompendiumEntry> getAllEntries() {
        return entries;
    }

    public void addEntry(CompendiumEntry entry) {
        if(entries.size() < AmbienceConfig.maxAmountOfCompendiumEntries)
            entries.add(entry);
    }

    public void addAllEntries(List<CompendiumEntry> entries) {
        if(this.entries.size() + entries.size() <= AmbienceConfig.maxAmountOfCompendiumEntries)
            this.entries.addAll(entries);
        else {
            this.entries.addAll(entries.subList(0, AmbienceConfig.maxAmountOfCompendiumEntries - this.entries.size() - 1));
        }
    }

    public void cloneAllEntries(List<CompendiumEntry> entries) {
        for(int i = 0; i < entries.size() && i < AmbienceConfig.maxAmountOfCompendiumEntries; i++) {
            this.entries.add(entries.get(i).copy());
        }
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
        cloneAllEntries(compendium.getAllEntries());
    }

    public int size() {
        return entries.size();
    }

    public boolean contains(CompendiumEntry entry) {
        return entries.contains(entry);
    }
}
