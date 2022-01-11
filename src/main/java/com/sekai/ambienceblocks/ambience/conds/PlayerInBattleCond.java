package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PlayerInBattleCond extends AbstractCond {
    //public static final int SEARCH_RANGE = 40;

    private AmbienceEquality equal;
    private String entity;
    private int time;

    private static final String EQUAL = "equal";
    private static final String ENTITY = "entity";
    private static final String TIME = "time";

    public PlayerInBattleCond(AmbienceEquality equal, String entity, int time) {
        this.equal = equal;
        this.entity = entity;
        this.time = time;
    }

    @Override
    public AbstractCond clone() {
        PlayerInBattleCond cond = new PlayerInBattleCond(equal, entity, time);
        return cond;
    }

    @Override
    public String getName() {
        return "player.in_battle";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + entity + " for " + time;
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        int countdown = AmbienceController.instance.target.getEntityCountdown(entity);

        //No result found
        if(countdown == -1)
            return equal.testFor(false);

        return equal.testFor(countdown > AmbienceConfig.targetCountdownAmount - time);

        /*if(countdown < AmbienceConfig.targetCountdownAmount - time) {
            return equal.testFor(true);
        }
        return equal.testFor(false);*/
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(ENTITY, "Entity :", 160, entity));
        list.add(new AmbienceWidgetString(TIME, "Time :", 40, Integer.toString(time), 5, ParsingUtil.numberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(ENTITY.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                entity = ((AmbienceWidgetString) widget).getValue();
            if (TIME.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                time = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(ENTITY, entity);
        nbt.putInt(TIME, time);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        entity = nbt.getString(ENTITY);
        time = nbt.getInt(TIME);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(equal.ordinal());
        buf.writeUtf(entity, 50);
        buf.writeInt(time);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.entity = buf.readUtf(50);
        this.time = buf.readInt();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(ENTITY, entity);
        json.addProperty(TIME, time);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        entity = json.get(ENTITY).getAsString();
        time = json.get(TIME).getAsInt();
    }
}
