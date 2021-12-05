package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerLuminosityCond extends AbstractCond {
    private AmbienceTest test;
    private int value;

    private static final String TEST = "test";
    private static final String VALUE = "value";

    public PlayerLuminosityCond(AmbienceTest test, int value) {
        this.test = test;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        PlayerLuminosityCond cond = new PlayerLuminosityCond(test, value);
        return cond;
    }

    @Override
    public String getName() {
        return "player.luminosity";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value;
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        /*float val = player.getFoodStats().getFoodLevel();
        return test.testForDouble(val, value);*/
        /*System.out.println(worldIn.getLight(ParsingUtil.vec3DtoBlockpos(sourceIn.getOrigin().add(OFFSET.scale(player.getHeight()/2)))) + " " + test.getName() + " " + value + " " + sourceIn.getOrigin());
        return test.testForInt(worldIn.getLight(ParsingUtil.vec3DtoBlockpos(sourceIn.getOrigin().add(OFFSET.scale(player.getHeight()/2)))), value);*/
        //System.out.println(worldIn.getLight(player.getPosition()) + " " + test.getName() + " " + value + " " + sourceIn.getOrigin());
        return test.testForInt(worldIn.getLight(player.getPosition()), value);
    }

    //gui

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetString(VALUE, "Luminosity :", 50, Integer.toString(value), 4, ParsingUtil.numberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(TEST, test.ordinal());
        nbt.setInteger(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        test = StaticUtil.getEnumValue(nbt.getInteger(TEST), AmbienceTest.values()); //AmbienceTest.values()[nbt.getInt(TEST) < AmbienceTest.values().length ? nbt.getInt(TEST) : 0];
        value = nbt.getInteger(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values()); //AmbienceTest.values()[buf.readInt()];
        this.value = buf.readInt();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(VALUE, value);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        value = json.get(VALUE).getAsInt();
    }
}
