package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.tileentity.ambiencetilecond.*;
import com.sekai.ambienceblocks.tileentity.util.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;

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
        PLAYER_POS_AXIS(10, new PlayerPosAxisCond(AmbienceTest.EQUAL_TO, AmbienceWorldSpace.ABSOLUTE, AmbienceAxis.X, 0)),
        PLAYER_HEALTH(11, new PlayerHealthCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_HUNGER(12, new PlayerHungerCond(AmbienceTest.GREATER_THAN, 10)),
        PLAYER_GAMEMODE(13, new PlayerGamemodeCond(AmbienceEquality.EQUAL_TO, GameType.SURVIVAL)),
        WORLD_WEATHER(20, new WorldWeatherCond(AmbienceEquality.EQUAL_TO, AmbienceWeather.CLEAR)),
        WORLD_DAYTIME(21, new WorldDaytimeCond(AmbienceTest.GREATER_THAN, 0)),
        WORLD_ISDAY(23, new WorldNeedDayCond(AmbienceEquality.EQUAL_TO)),
        WORLD_REDSTONE(22, new WorldNeedRedstoneCond(AmbienceEquality.EQUAL_TO)),
        AMBIENCE_PRIORITY(30, new AmbiencePriorityCond(AmbienceTest.EQUAL_TO, 0, 0)),
        AMBIENCE_ISPLAYING(31, new AmbienceIsPlayingCond(AmbienceEquality.EQUAL_TO, ""));

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
