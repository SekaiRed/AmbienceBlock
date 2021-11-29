package com.sekai.ambienceblocks.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.conds.*;
import com.sekai.ambienceblocks.ambience.util.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

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

    public static JsonObject toJson(AbstractCond cond) {
        JsonObject json = new JsonObject();

        json.addProperty("name", cond.getName());

        cond.toJson(json);

        return json;
    }

    public static AbstractCond fromJson(JsonObject json) {
        if(json.get("name") == null)
            return CondsUtil.getDefault();

        String name = json.get("name").getAsString();
        AbstractCond cond = CondsUtil.CondList.getCondFromName(name);

        cond.fromJson(json);

        return cond;
    }

    public static AbstractCond getDefault() {
        return CondList.ALWAYS_TRUE.getDefault();
    }

    public enum CondList {
        ALWAYS_TRUE(0, new AlwaysTrueCond()),
        AND(1, new AndCond(ALWAYS_TRUE.getDefault(), ALWAYS_TRUE.getDefault())),
        OR(2, new OrCond(ALWAYS_TRUE.getDefault(), ALWAYS_TRUE.getDefault())),
        PLAYER_POS_AXIS(100, new PlayerPosAxisCond(AmbienceTest.EQUAL_TO, AmbienceWorldSpace.ABSOLUTE, AmbienceAxis.X, 0)),
        PLAYER_POS_WITHIN_REGION(101, new PlayerPosWithinRegionCond(0, 0, 0, 0, 0, 0, AmbienceEquality.EQUAL_TO, AmbienceWorldSpace.RELATIVE)),
        PLAYER_POS_WITHIN_RADIUS(102, new PlayerPosWithinRadiusCond(0, 0, 0, 16, AmbienceTest.LESSER_THAN, AmbienceWorldSpace.RELATIVE)),
        PLAYER_MOTION_AXIS(103, new PlayerMotionAxisCond(AmbienceTest.EQUAL_TO, AmbienceAxis.Y, 0)),
        PLAYER_BLOCK(104, new PlayerBlockCond(0, 0, 0, AmbienceEquality.EQUAL_TO, AmbienceWorldSpace.RELATIVE, "")),
        PLAYER_BLOCK_DENSITY(105, new PlayerBlockDensityCond(1, 1, 1, 0, 0, 0, AmbienceWorldSpace.RELATIVE, AmbienceTest.GREATER_THAN, 0, "")),
        PLAYER_HEALTH(106, new PlayerHealthCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_HUNGER(107, new PlayerHungerCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_GAMEMODE(108, new PlayerGamemodeCond(AmbienceEquality.EQUAL_TO, GameType.SURVIVAL)),
        PLAYER_BIOME(109, new PlayerBiomeCond(AmbienceEquality.EQUAL_TO, Biomes.PLAINS.getLocation().toString())),
        PLAYER_DIMENSION(110, new PlayerDimensionCond(AmbienceEquality.EQUAL_TO, World.OVERWORLD.getLocation().toString())),
        //PLAYER_STRUCTURE(108, new PlayerStructureCond(AmbienceEquality.EQUAL_TO, Structure.VILLAGE.getStructureName())),
        PLAYER_IN_BATTLE(111, new PlayerInBattleCond(AmbienceEquality.EQUAL_TO, "", 50)),
        PLAYER_ENTITY_IN_RANGE(112, new PlayerEntityInRangeCond(AmbienceEquality.EQUAL_TO, "", 8D)),
        PLAYER_UNDERWATER(113, new PlayerUnderwaterCond(AmbienceEquality.EQUAL_TO)),
        WORLD_WEATHER(200, new WorldWeatherCond(AmbienceEquality.EQUAL_TO, AmbienceWeather.CLEAR)),
        WORLD_DAYTIME(201, new WorldDaytimeCond(AmbienceTest.GREATER_THAN, 0)),
        WORLD_IS_DAY(202, new WorldNeedDayCond(AmbienceEquality.EQUAL_TO)),
        WORLD_REDSTONE(203, new WorldNeedRedstoneCond(AmbienceEquality.EQUAL_TO)),
        AMBIENCE_PRIORITY(300, new AmbiencePriorityCond(AmbienceTest.EQUAL_TO, 0, 0)),
        AMBIENCE_ISPLAYING(301, new AmbienceIsPlayingCond(AmbienceEquality.EQUAL_TO, "")),
        AMBIENCE_AMOUNT(302, new AmbienceSlotAmountCond(AmbienceTest.GREATER_THAN, 0, "", ""));

        //You can freely edit the meta value because it's only used on packet transfers
        // which should be the same independently from the version
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
