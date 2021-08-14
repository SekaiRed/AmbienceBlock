package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerBiomeCond extends AbstractCond {
    //this.mc.world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(this.mc.world.getBiome(blockpos))

    private AmbienceEquality equal;
    private String biome;

    private static final String EQUAL = "equal";
    private static final String BIOME = "biome";

    public PlayerBiomeCond(AmbienceEquality equal, String biome) {
        this.equal = equal;
        this.biome = biome;
    }

    @Override
    public AbstractCond clone() {
        PlayerBiomeCond cond = new PlayerBiomeCond(equal, biome);
        return cond;
    }

    @Override
    public String getName() {
        return "player.biome";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + biome;
    }

    @Override
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(biome));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(BIOME, "Biome :", 160, biome));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(BIOME.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                biome = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(BIOME, biome);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        biome = nbt.getString(BIOME);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(biome, 50);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.biome = buf.readString(50);
    }
}
