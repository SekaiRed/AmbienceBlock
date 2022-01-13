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
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        //return equal.testFor(worldIn.getBiome(new BlockPos(playerPos)).getRegistryName().toString().contains(biome));
        return equal.testFor(stringValidation(worldIn.getBiome(player.getPosition()).getRegistryName().toString(), biome));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetString(BIOME, "Biome :", 160, biome, StaticUtil.LENGTH_COND_INPUT));
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
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setString(BIOME, biome);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInteger(EQUAL), AmbienceEquality.values());
        biome = nbt.getString(BIOME);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(biome);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.biome = buf.readString(StaticUtil.LENGTH_COND_INPUT);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(BIOME, biome);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        biome = json.get(BIOME).getAsString();
    }
}
