package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.ambience.util.AmbienceEnumName;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static StructureBoundingBox growBoundingBox(StructureBoundingBox playerBB, double range) {
        StructureBoundingBox bb = new StructureBoundingBox(playerBB);
        bb.maxX += range/2;
        bb.maxY += range/2;
        bb.maxZ += range/2;
        bb.minX -= range/2;
        bb.minY -= range/2;
        bb.minZ -= range/2;
        return bb;
    }
}
