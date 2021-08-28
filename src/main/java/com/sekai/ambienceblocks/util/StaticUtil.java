package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import net.minecraft.util.SoundCategory;

import java.util.ArrayList;

public class StaticUtil {
    public static ArrayList<String> getListOfAmbienceType() {
        ArrayList<String> list = new ArrayList<String>();
        for(AmbienceType value : AmbienceType.values())
            list.add(value.getName());
        return list;
    }

    public static ArrayList<String> getListOfSoundCategories() {
        ArrayList<String> list = new ArrayList<String>();
        for(SoundCategory value : SoundCategory.values())
            list.add(value.getName());
        return list;
    }
}
