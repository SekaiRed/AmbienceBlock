package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerDimensionCond extends AbstractCond {
    private AmbienceEquality equal;
    private String dimension;

    private static final String EQUAL = "equal";
    private static final String DIMENSION = "dimension";

    public PlayerDimensionCond(AmbienceEquality equal, String dimension) {
        this.equal = equal;
        this.dimension = dimension;
    }

    @Override
    public AbstractCond clone() {
        PlayerDimensionCond cond = new PlayerDimensionCond(equal, dimension);
        return cond;
    }

    @Override
    public String getName() {
        return "player.dimension";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + dimension;
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        //return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(dimension));
        //return equal.testFor(stringValidation(worldIn.getDimensionKey().getLocation().toString(), dimension));
        return equal.testFor(stringValidation(worldIn.provider.getDimensionType().getName(), dimension));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(DIMENSION, "Dimension :", 130, dimension, StaticUtil.LENGTH_COND_INPUT));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(DIMENSION.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                dimension = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setString(DIMENSION, dimension);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInteger(EQUAL), AmbienceEquality.values());
        dimension = nbt.getString(DIMENSION);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(dimension);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.dimension = buf.readString(StaticUtil.LENGTH_COND_INPUT);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(DIMENSION, dimension);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        dimension = json.get(DIMENSION).getAsString();
    }
}
