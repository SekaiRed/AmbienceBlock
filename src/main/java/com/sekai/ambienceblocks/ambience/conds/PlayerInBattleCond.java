package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerInBattleCond extends AbstractCond {
    //TODO Either move to a config or leave it as a parameter for the condition
    public static final int SEARCH_RANGE = 40;

    private AmbienceEquality equal;
    private String entity;

    private static final String EQUAL = "equal";
    private static final String ENTITY = "entity";

    public PlayerInBattleCond(AmbienceEquality equal, String entity) {
        this.equal = equal;
        this.entity = entity;
    }

    @Override
    public AbstractCond clone() {
        PlayerInBattleCond cond = new PlayerInBattleCond(equal, entity);
        return cond;
    }

    @Override
    public String getName() {
        return "player.in_battle";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + entity;
    }

    @Override
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        //worldIn.getWorldInfo()
        //worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureStarts()
        List<MobEntity> mobs = worldIn.getEntitiesWithinAABB(MobEntity.class, AxisAlignedBB.fromVector(playerPos).grow(SEARCH_RANGE));
        for (MobEntity mob : mobs) {
            if (mob.getAttackTarget() != null && mob.getType().getRegistryName().toString().contains(entity))
                return equal.testFor(true);
        }
        return equal.testFor(false);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(ENTITY, "Entity :", 160, entity));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(ENTITY.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                entity = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(ENTITY, entity);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        entity = nbt.getString(ENTITY);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(entity, 50);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.entity = buf.readString(50);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(ENTITY, entity);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        entity = json.get(ENTITY).getAsString();
    }
}
