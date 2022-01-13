package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.sync.structure.StructureSyncClient;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.*;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.StructureUtil;
import com.sekai.ambienceblocks.util.Unused;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

//TODO add something similar to the InBattle sync system with more delay and a config option, it'd need a query packet too to ask for info on structures
@Unused(type = Unused.Type.TO_FIX)
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
        return "[" + getName() + "] " + equal.getName() + " " + structure;
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(StructureSyncClient.instance.isPlayerInStructure(structure, range, full));
        //worldIn.findNearestStructure()
        //Map<Structure<?>, LongSet> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureReferences();
        /*Map<Structure<?>, StructureStart<?>> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureStarts();
        if(structureReferences.size() != 0)
            System.out.println("==================");
        structureReferences.entrySet().forEach(entry -> {
            System.out.println(entry.getKey().toString() + " : " + entry.getValue().toString());
        });
        return true;*/
        //return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(structure));
        //return true;
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(RANGE, "Range :", 60, Double.toString(range), 10, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetCheckbox(FULL, "Full? :", 20, full));
        list.add(new AmbienceWidgetScroll(STRUCTURE, "Struct :", 180, new ArrayList<>(StructureUtil.getListOfStructureTypes()), structure));
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
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setString(STRUCTURE, structure);
        nbt.setDouble(RANGE, range);
        nbt.setBoolean(FULL, full);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInteger(EQUAL), AmbienceEquality.values());
        structure = nbt.getString(STRUCTURE);
        range = nbt.getDouble(RANGE);
        full = nbt.getBoolean(FULL);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(structure);
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
