package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.StaticUtil;
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

    private static final String EQUAL = "equal";
    private static final String STRUCTURE = "structure";

    public PlayerStructureCond(AmbienceEquality equal, String structure) {
        this.equal = equal;
        this.structure = structure;
    }

    @Override
    public AbstractCond clone() {
        PlayerStructureCond cond = new PlayerStructureCond(equal, structure);
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
        //Map<Structure<?>, LongSet> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureReferences();
        /*Map<Structure<?>, StructureStart<?>> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureStarts();
        if(structureReferences.size() != 0)
            System.out.println("==================");
        structureReferences.entrySet().forEach(entry -> {
            System.out.println(entry.getKey().toString() + " : " + entry.getValue().toString());
        });
        return true;*/
        //return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(structure));
        return true;
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(STRUCTURE, "Biome :", 160, structure));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(STRUCTURE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                structure = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setString(STRUCTURE, structure);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInteger(EQUAL), AmbienceEquality.values());
        structure = nbt.getString(STRUCTURE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(structure);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.structure = buf.readString(50);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(STRUCTURE, structure);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        structure = json.get(STRUCTURE).getAsString();
    }
}
