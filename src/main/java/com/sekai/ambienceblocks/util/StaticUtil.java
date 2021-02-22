package com.sekai.ambienceblocks.util;

import net.minecraft.util.SoundCategory;

import java.util.ArrayList;

public class StaticUtil {
    public static ArrayList<String> getListOfSoundCategories() {
        ArrayList<String> list = new ArrayList<String>();
        for(SoundCategory value : SoundCategory.values())
            list.add(value.getName());
        return list;
    }
}
