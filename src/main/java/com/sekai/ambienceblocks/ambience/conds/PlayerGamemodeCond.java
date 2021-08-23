package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerGamemodeCond extends AbstractCond {
    private AmbienceEquality equal;
    private GameType type;

    private static final String EQUAL = "equal";
    private static final String TYPE = "type";

    public PlayerGamemodeCond(AmbienceEquality equal, GameType type) {
        this.equal = equal;
        this.type = type;
    }

    @Override
    public AbstractCond clone() {
        PlayerGamemodeCond cond = new PlayerGamemodeCond(equal, type);
        return cond;
    }

    @Override
    public String getName() {
        return "player.gamemode";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + type.getName();
    }

    @Override
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(type.equals(Minecraft.getInstance().playerController.getCurrentGameType()));
    }

    //gui

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetEnum<>(TYPE, "Gamemode :", 60, type));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(TYPE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                type = (GameType) ((AmbienceWidgetEnum) widget).getValue();
        }
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putInt(TYPE, type.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());//AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        type = StaticUtil.getEnumValue(nbt.getInt(TYPE), GameType.values());//GameType.values()[nbt.getInt(TYPE) < GameType.values().length ? nbt.getInt(TYPE) : 0];
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(type.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values()); //AmbienceEquality.values()[buf.readInt()];
        type = StaticUtil.getEnumValue(buf.readInt(), GameType.values()); //GameType.values()[buf.readInt()];
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(TYPE, type.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        type = StaticUtil.getEnumValue(json.get(TYPE).getAsString(), GameType.values());
    }
}
