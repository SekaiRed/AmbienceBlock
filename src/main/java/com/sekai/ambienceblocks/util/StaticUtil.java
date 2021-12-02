package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.ambience.util.AmbienceEnumName;
import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class StaticUtil {
    public static <T extends Enum<T>> T getEnumValue(int index, T[] values) {
        //index %= values.length;
        //return values[index >= 0 ? index : -index];
        return values[index >= 0 && index < values.length ? index : 0];
    }

    public static <T extends Enum<T>> T getEnumValueIncludingCustom(String name, T[] values) {
        for(T value : values) {
            if(value instanceof AmbienceEnumName) {
                if(((AmbienceEnumName) value).getName().equals(name))
                    return value;
            } else {
                if(value.name().equals(name))
                    return value;
            }
        }
        return values[0];
        //TODO make a version with a default value and one without
    }

    public static <T extends Enum<T>> T getEnumValue(String name, T[] values) {
        for(T value : values) {
            if(value.name().equals(name))
                return value;
        }
        return values[0];
        //TODO make a version with a default value and one without
    }

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

    public static ArrayList<String> getListOfStructureTypes() {
        ArrayList<String> list = new ArrayList<>();
        ForgeRegistries.STRUCTURE_FEATURES.getKeys().forEach(resource -> list.add(resource.toString()));
        return list;
    }
}
