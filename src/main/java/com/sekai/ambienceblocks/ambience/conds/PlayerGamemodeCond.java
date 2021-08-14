package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import net.minecraft.client.Minecraft;
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
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
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
        equal = AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        type = GameType.values()[nbt.getInt(TYPE) < GameType.values().length ? nbt.getInt(TYPE) : 0];
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeInt(type.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        equal = AmbienceEquality.values()[buf.readInt()];
        type = GameType.values()[buf.readInt()];
    }
}
