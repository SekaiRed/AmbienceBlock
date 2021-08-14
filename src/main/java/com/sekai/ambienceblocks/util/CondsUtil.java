package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.tileentity.ambiencetilecond.*;
import com.sekai.ambienceblocks.tileentity.util.*;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.structure.Structure;

import static net.minecraftforge.registries.ForgeRegistries.BIOMES;

public class CondsUtil {
    //nbt stuff
    public static CompoundNBT toNBT(AbstractCond cond) {
        CompoundNBT compound = cond.toNBT();
        compound.putString("condName", cond.getName()); //put type information
        return compound;
    }

    public static AbstractCond fromNBT(CompoundNBT nbt) {
        String name = nbt.getString("condName");
        AbstractCond cond = CondList.getCondFromName(name);
        cond.fromNBT(nbt);
        return cond;
    }

    public static void toBuff(AbstractCond cond, PacketBuffer buf) {
        buf.writeInt(CondList.getIntFromCond(cond));
        cond.toBuff(buf);
    }

    public static AbstractCond fromBuff(PacketBuffer buf) {
        int id = buf.readInt();
        AbstractCond cond = CondList.getCondFromInt(id);

        cond.fromBuff(buf);
        return cond;
    }

    public enum CondList {
        ALWAYS_TRUE(0, new AlwaysTrueCond()),
        PLAYER_POS_AXIS(100, new PlayerPosAxisCond(AmbienceTest.EQUAL_TO, AmbienceWorldSpace.ABSOLUTE, AmbienceAxis.X, 0)),
        PLAYER_POS_WITHIN_REGION(101, new PlayerPosWithinRegionCond(0, 0, 0, 0, 0, 0, AmbienceEquality.EQUAL_TO, AmbienceWorldSpace.RELATIVE)),
        PLAYER_POS_WITHIN_RADIUS(102, new PlayerPosWithinRadiusCond(0, 0, 0, 16, AmbienceTest.LESSER_THAN, AmbienceWorldSpace.RELATIVE)),
        PLAYER_BLOCK(103, new PlayerBlockCond(0, 0, 0, AmbienceEquality.EQUAL_TO, AmbienceWorldSpace.RELATIVE, "")),
        PLAYER_HEALTH(104, new PlayerHealthCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_HUNGER(105, new PlayerHungerCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_GAMEMODE(106, new PlayerGamemodeCond(AmbienceEquality.EQUAL_TO, GameType.SURVIVAL)),
        PLAYER_BIOME(107, new PlayerBiomeCond(AmbienceEquality.EQUAL_TO, Biomes.PLAINS.getLocation().toString())),
        //PLAYER_STRUCTURE(108, new PlayerStructureCond(AmbienceEquality.EQUAL_TO, Structure.VILLAGE.getStructureName())),
        PLAYER_INBATTLE(109, new PlayerInBattleCond(AmbienceEquality.EQUAL_TO, "")),
        WORLD_WEATHER(200, new WorldWeatherCond(AmbienceEquality.EQUAL_TO, AmbienceWeather.CLEAR)),
        WORLD_DAYTIME(201, new WorldDaytimeCond(AmbienceTest.GREATER_THAN, 0)),
        WORLD_ISDAY(202, new WorldNeedDayCond(AmbienceEquality.EQUAL_TO)),
        WORLD_REDSTONE(203, new WorldNeedRedstoneCond(AmbienceEquality.EQUAL_TO)),
        AMBIENCE_PRIORITY(300, new AmbiencePriorityCond(AmbienceTest.EQUAL_TO, 0, 0)),
        AMBIENCE_ISPLAYING(301, new AmbienceIsPlayingCond(AmbienceEquality.EQUAL_TO, ""));

        //You can freely edit the meta value because it's only used on packet transfers, which should be the same independently from the version
        int metaValue;
        AbstractCond defaultCond;

        public int getMetaValue() {
            return metaValue;
        }

        public AbstractCond getDefault() {
            return defaultCond.clone();
        }

        public boolean isOfType(AbstractCond cond) {
            return defaultCond.getName().equals(cond.getName());
        }

        public static AbstractCond getCondFromInt(int index) {
            CondList[] conds = CondList.values();
            for (CondList cond : conds) {
                if(cond.getMetaValue() == index) {
                    return cond.getDefault();
                }
            }
            return ALWAYS_TRUE.getDefault();
        }

        public static AbstractCond getCondFromName(String name) {
            CondList[] conds = CondList.values();
            for (CondList cond : conds) {
                if(cond.defaultCond.getName().equals(name)) {
                    return cond.getDefault();
                }
            }
            return ALWAYS_TRUE.getDefault();
        }

        public static int getIntFromCond(AbstractCond cond) {
            CondList[] conds = CondList.values();
            for (CondList conde : conds) {
                if(conde.defaultCond.getName().equals(cond.getName())) {
                    return conde.getMetaValue();
                }
            }
            return ALWAYS_TRUE.getMetaValue();
        }

        CondList(int metaValue, AbstractCond cond) {
            this.metaValue = metaValue;
            defaultCond = cond;
        }
    }
}
