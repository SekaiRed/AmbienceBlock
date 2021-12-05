package com.sekai.ambienceblocks.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StructureUtil {
    //func_175797_c
    final static Method getStructureAt = ObfuscationReflectionHelper.findMethod(MapGenStructure.class, "func_175797_c", StructureStart.class, BlockPos.class);
    final static Method getStructureStart = ObfuscationReflectionHelper.findMethod(MapGenStructure.class, "func_75049_b", StructureStart.class, int.class, int.class);

    // MapGenStructure
    final static Field structureMapField = ObfuscationReflectionHelper.findField(MapGenStructure.class, "field_75053_d");
    //Overworld
    final static Field strongholdGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_186004_w");
    final static Field villageGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_186005_x");
    final static Field mineshaftGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_186006_y");
    final static Field scatteredFeatureGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_186007_z");
    final static Field oceanMonumentGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_185980_B");
    final static Field woodlandMansionGeneratorField = ObfuscationReflectionHelper.findField(ChunkGeneratorOverworld.class, "field_191060_C");
    // Nether
    final static Field genNetherBridgeField = ObfuscationReflectionHelper.findField(ChunkGeneratorHell.class, "field_73172_c");
    // End
    final static Field endCityGenField = ObfuscationReflectionHelper.findField(ChunkGeneratorEnd.class, "field_185972_n");

    final static HashMap<String, Field> structureGensOverworld = new HashMap<>();
    static {
        structureGensOverworld.put("Stronghold", strongholdGeneratorField);
        structureGensOverworld.put("Village", villageGeneratorField);
        structureGensOverworld.put("Mineshaft", mineshaftGeneratorField);
        structureGensOverworld.put("Temple", scatteredFeatureGeneratorField);
        structureGensOverworld.put("Monument", oceanMonumentGeneratorField);
        structureGensOverworld.put("Mansion", woodlandMansionGeneratorField);
    }

    final static HashMap<String, Field> structureGensNether = new HashMap<>();
    static {
        structureGensNether.put("Fortress", genNetherBridgeField);
    }

    final static HashMap<String, Field> structureGensEnd = new HashMap<>();
    static {
        structureGensEnd.put("EndCity", endCityGenField);
    }

    public static StructureStart getStructureStart (WorldServer worldServer, BlockPos pos, String structureName) {
        //StructureStart structureStart = null;
        MapGenStructure genStructure = getStructureFromGen(worldServer.getChunkProvider().chunkGenerator, structureName);
        //System.out.println(pos);
        try {
            Long2ObjectMap<StructureStart> structureMap = (Long2ObjectMap<StructureStart>) structureMapField.get(genStructure);
            if(structureMap != null) {
                ObjectIterator<StructureStart> iterator = structureMap.values().iterator();
                while(iterator.hasNext()) {
                    StructureStart start = iterator.next();
                    if(start.getBoundingBox().isVecInside(pos))
                        return start;
                }
            }
            /*System.out.println(genStructure);
            structureStart = (StructureStart) getStructureAt.invoke(genStructure, pos);
            System.out.println(structureStart);*/
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
        //getStructureAt.invoke(genStructure, pos);
    }

    private static MapGenStructure getStructureFromGen(IChunkGenerator chunkGenerator, String structureName) {
        MapGenStructure genStructure = null;
        try {
            if(chunkGenerator instanceof ChunkGeneratorOverworld) {
                if(structureGensOverworld.containsKey(structureName)) {
                    genStructure = getStructureFromField(structureGensOverworld.get(structureName), (ChunkGeneratorOverworld) chunkGenerator);
                }
            } else if(chunkGenerator instanceof ChunkGeneratorHell) {
                if(structureGensNether.containsKey(structureName)) {
                    genStructure = getStructureFromField(structureGensNether.get(structureName), (ChunkGeneratorHell) chunkGenerator);
                }
            } else if(chunkGenerator instanceof ChunkGeneratorEnd) {
                if(structureGensEnd.containsKey(structureName)) {
                    genStructure = getStructureFromField(structureGensEnd.get(structureName), (ChunkGeneratorEnd) chunkGenerator);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return genStructure;
    }

    private static <T extends IChunkGenerator> MapGenStructure getStructureFromField(Field field, T chunkGen) throws IllegalAccessException {
        return (MapGenStructure) field.get(chunkGen);
    }

    public static List<String> getListOfStructureTypes() {
        return Arrays.asList("Stronghold", "Monument", "Village", "Mansion", "EndCity", "Fortress", "Temple", "Mineshaft");
    }
    //Field soundRegistryField = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147697_e");
    /*    soundRegistryField.setAccessible(true);
        try {
        soundRegistry = (SoundRegistry) soundRegistryField.get(mc.getSoundHandler());
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }*/
}
