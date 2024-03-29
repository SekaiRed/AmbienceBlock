package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class PlayerEntityInRangeCond extends AbstractCond {
    private AmbienceEquality equal;
    private String entity;
    private double range;

    private static final String EQUAL = "equal";
    private static final String ENTITY = "entity";
    private static final String RANGE = "range";

    public PlayerEntityInRangeCond(AmbienceEquality equal, String entity, double range) {
        this.equal = equal;
        this.entity = entity;
        this.range = range;
    }

    @Override
    public AbstractCond clone() {
        PlayerEntityInRangeCond cond = new PlayerEntityInRangeCond(equal, entity, range);
        return cond;
    }

    @Override
    public String getName() {
        return "player.entity_in_range";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + entity + " in " + range;
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        //worldIn.getWorldInfo()
        //worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureStarts()
        double searchRange = Math.min(range, AmbienceConfig.maxEntitySearchRange);
        List<Entity> entities = worldIn.getEntities(null, AABB.unitCubeFromLowerCorner(getPlayerPos(player)).inflate(searchRange));
        for (Entity entity : entities) {
            if (entity != player && stringValidation(entity.getType().getRegistryName().toString(), this.entity))
                return equal.testFor(true);
        }
        return equal.testFor(false);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(ENTITY, "Entity :", 160, entity, StaticUtil.LENGTH_COND_INPUT));
        list.add(new AmbienceWidgetString(RANGE, "Range :", 80, Double.toString(range), 8, ParsingUtil.negativeDecimalNumberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(ENTITY.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                entity = ((AmbienceWidgetString) widget).getValue();
            if (RANGE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                range = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(ENTITY, entity);
        nbt.putDouble(RANGE, range);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        entity = nbt.getString(ENTITY);
        range = nbt.getDouble(RANGE);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(equal.ordinal());
        buf.writeUtf(entity, StaticUtil.LENGTH_COND_INPUT);
        buf.writeDouble(range);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.entity = buf.readUtf(StaticUtil.LENGTH_COND_INPUT);
        this.range = buf.readDouble();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(ENTITY, entity);
        json.addProperty(RANGE, range);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        entity = json.get(ENTITY).getAsString();
        range = json.get(RANGE).getAsDouble();
    }
}
