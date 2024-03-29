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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

//TODO I could add other parameters like looking for specific sound/tag/volume (separate cond?)
// I could even use that with a 'battle' tag to check if a battle music is already playing
public class AmbienceSlotAmountCond extends AbstractCond {
    private AmbienceTest test;
    private int value;
    private String sound;
    private String tag;

    private static final String TEST = "test";
    private static final String VALUE = "value";
    private static final String SOUND = "sound";
    private static final String TAG = "tag";

    public AmbienceSlotAmountCond(AmbienceTest test, int value, String sound, String tag) {
        this.test = test;
        this.value = value;
        this.sound = sound;
        this.tag = tag;
    }

    @Override
    public AbstractCond clone() {
        AmbienceSlotAmountCond cond = new AmbienceSlotAmountCond(test, value, sound, tag);
        return cond;
    }

    @Override
    public String getName() {
        return "ambience.amount";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value + " with " + sound + " and " + tag;
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        /*int amount = AmbienceController.instance.soundsList.size();


        if(AmbienceController.instance.isSourceAlreadyPlaying(sourceIn) != null)
            amount--;*/

        int amount = 0;

        for (AmbienceSlot slot : AmbienceController.instance.soundsList) {
            if (slot.getSource() != sourceIn && stringValidation(slot.getMusicString(), sound) && stringValidation(slot.getData().getTag(), tag)) {
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
        list.add(new AmbienceWidgetString(SOUND, "Name :", 110, sound, StaticUtil.LENGTH_COND_INPUT));
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
            if(SOUND.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                sound = ((AmbienceWidgetString) widget).getValue();
            if(TAG.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                tag = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TEST, test.ordinal());
        nbt.putInt(VALUE, value);
        nbt.putString(SOUND, sound);
        nbt.putString(TAG, tag);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values());
        value = nbt.getInt(VALUE);
        sound = nbt.getString(SOUND);
        tag = nbt.getString(TAG);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(value);
        buf.writeUtf(sound, StaticUtil.LENGTH_COND_INPUT);
        buf.writeUtf(tag, 5);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
        this.value = buf.readInt();
        this.sound = buf.readUtf(StaticUtil.LENGTH_COND_INPUT);
        this.tag = buf.readUtf(5);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(VALUE, value);
        json.addProperty(SOUND, sound);
        json.addProperty(TAG, tag);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        value = json.get(VALUE).getAsInt();
        sound = json.get(SOUND).getAsString();
        tag = json.get(TAG).getAsString();
    }
}
