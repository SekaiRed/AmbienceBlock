package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.sync.structure.StructureSyncClient;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.*;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.Unused;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStructureCond extends AbstractCond {
    private AmbienceEquality equal;
    private String structure;
    private double range;
    private boolean full;

    private static final String EQUAL = "equal";
    private static final String STRUCTURE = "structure";
    private static final String RANGE = "range";
    private static final String FULL = "full";

    public PlayerStructureCond(AmbienceEquality equal, String structure, double range, boolean full) {
        this.equal = equal;
        this.structure = structure;
        this.range = range;
        this.full = full;
    }

    @Override
    public AbstractCond clone() {
        PlayerStructureCond cond = new PlayerStructureCond(equal, structure, range, full);
        return cond;
    }

    @Override
    public String getName() {
        return "player.structure";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + structure + " from " + range + " with full " + full;
    }

    @Override
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(StructureSyncClient.instance.isPlayerInStructure(structure, range, full));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(RANGE, "Range :", 60, Double.toString(range), 10, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetCheckbox(FULL, "Full? :", 20, full));
        list.add(new AmbienceWidgetScroll(STRUCTURE, "Struct :", 180, StaticUtil.getListOfStructureTypes(), structure));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(STRUCTURE.equals(widget.getKey()) && widget instanceof AmbienceWidgetScroll)
                structure = ((AmbienceWidgetScroll) widget).getValue();
            if (RANGE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                range = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (FULL.equals(widget.getKey()) && widget instanceof AmbienceWidgetCheckbox)
                full = ((AmbienceWidgetCheckbox) widget).getValue();
        }
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(STRUCTURE, structure);
        nbt.putDouble(RANGE, range);
        nbt.putBoolean(FULL, full);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        structure = nbt.getString(STRUCTURE);
        range = nbt.getDouble(RANGE);
        full = nbt.getBoolean(FULL);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(structure, StaticUtil.LENGTH_COND_INPUT);
        buf.writeDouble(range);
        buf.writeBoolean(full);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.structure = buf.readString(StaticUtil.LENGTH_COND_INPUT);
        this.range = buf.readDouble();
        this.full = buf.readBoolean();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(STRUCTURE, structure);
        json.addProperty(RANGE, range);
        json.addProperty(FULL, full);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        structure = json.get(STRUCTURE).getAsString();
        range = json.get(RANGE).getAsDouble();
        full = json.get(FULL).getAsBoolean();
    }
}
