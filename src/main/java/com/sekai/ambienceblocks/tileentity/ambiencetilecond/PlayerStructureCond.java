package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.tileentity.util.AmbienceEquality;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.Unused;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO add something similar to InBattle with more delay and a config option, it'd need a query packet too to ask for info on structures
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
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        //Map<Structure<?>, LongSet> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureReferences();
        Map<Structure<?>, StructureStart<?>> structureReferences = worldIn.getChunkAt(Minecraft.getInstance().player.getPosition()).getStructureStarts();
        if(structureReferences.size() != 0)
            System.out.println("==================");
        structureReferences.entrySet().forEach(entry -> {
            System.out.println(entry.getKey().toString() + " : " + entry.getValue().toString());
        });
        return true;
        //return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(structure));
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
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(STRUCTURE, structure);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        structure = nbt.getString(STRUCTURE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(structure, 50);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.structure = buf.readString(50);
    }
}
