package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
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
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(type.equals(Minecraft.getMinecraft().playerController.getCurrentGameType()));
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
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setInteger(TYPE, type.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInteger(EQUAL), AmbienceEquality.values());
        type = StaticUtil.getEnumValue(nbt.getInteger(TYPE), GameType.values());
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(type.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        type = StaticUtil.getEnumValue(buf.readInt(), GameType.values());
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
