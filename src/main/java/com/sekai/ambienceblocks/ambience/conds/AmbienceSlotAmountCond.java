package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.client.ambience.AmbienceSlot;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

//TODO I could add other parameters like looking for specific sound/tag/volume (separate cond?)
// I could even use that with a 'battle' tag to check if a battle music is already playing
public class AmbienceSlotAmountCond extends AbstractCond {
    private AmbienceTest test;
    private int value;
    private String name;
    private String tag;
    //add tag and music field

    private static final String TEST = "test";
    private static final String VALUE = "value";
    private static final String NAME = "name";
    private static final String TAG = "tag";

    public AmbienceSlotAmountCond(AmbienceTest test, int value, String name, String tag) {
        this.test = test;
        this.value = value;
        this.name = name;
        this.tag = tag;
    }

    @Override
    public AbstractCond clone() {
        AmbienceSlotAmountCond cond = new AmbienceSlotAmountCond(test, value, name, tag);
        return cond;
    }

    @Override
    public String getName() {
        return "ambience.amount";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value + " with " + name + " and " + tag;
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        /*int amount = AmbienceController.instance.soundsList.size();


        if(AmbienceController.instance.isSourceAlreadyPlaying(sourceIn) != null)
            amount--;*/

        int amount = 0;

        for (AmbienceSlot slot : AmbienceController.instance.soundsList) {
            if (slot.getSource() != sourceIn && stringValidation(slot.getMusicString(), name) && stringValidation(slot.getData().getTag(), tag)) {
                //Valid, add
                amount++;
            }
            //This AmbienceSource is already playing and shouldn't be counted
        }

        return test.testForDouble(amount, value);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetString(VALUE, "Amount of playing ambiences :", 30, Integer.toString(value), 3, ParsingUtil.numberFilter));
        list.add(new AmbienceWidgetString(NAME, "Name :", 110, name, 50));
        list.add(new AmbienceWidgetString(TAG, "Tag :", 40, tag, 5));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
            if(NAME.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                name = ((AmbienceWidgetString) widget).getValue();
            if(TAG.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                tag = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(TEST, test.ordinal());
        nbt.setInteger(VALUE, value);
        nbt.setString(NAME, name);
        nbt.setString(TAG, tag);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        test = StaticUtil.getEnumValue(nbt.getInteger(TEST), AmbienceTest.values());
        value = nbt.getInteger(VALUE);
        name = nbt.getString(NAME);
        tag = nbt.getString(TAG);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(value);
        buf.writeString(name);
        buf.writeString(tag);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
        this.value = buf.readInt();
        this.name = buf.readString(50);
        this.tag = buf.readString(5);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(VALUE, value);
        json.addProperty(NAME, name);
        json.addProperty(TAG, tag);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        value = json.get(VALUE).getAsInt();
        name = json.get(NAME).getAsString();
        tag = json.get(TAG).getAsString();
    }
}
