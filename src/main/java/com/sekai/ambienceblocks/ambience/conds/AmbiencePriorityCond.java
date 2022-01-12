package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class AmbiencePriorityCond extends AbstractCond {
    private AmbienceTest test;
    private int priority;
    private int channel;

    private static final String TEST = "test";
    private static final String PRIORITY = "priority";
    private static final String CHANNEL = "channel";

    public AmbiencePriorityCond(AmbienceTest test, int priority, int channel) {
        this.test = test;
        this.priority = priority;
        this.channel = channel;
    }

    @Override
    public AbstractCond clone() {
        AmbiencePriorityCond cond = new AmbiencePriorityCond(test, priority, channel);
        return cond;
    }

    @Override
    public String getName() {
        return "ambience.priority";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " priority " + priority + " on channel " + channel;
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        return test.testForInt(priority, AmbienceController.instance.getHighestPriorityByChannel(channel));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetString(PRIORITY, "Priority :", 50, Integer.toString(priority), 2, ParsingUtil.numberFilter));
        list.add(new AmbienceWidgetString(CHANNEL, "Channel :", 20, Integer.toString(channel), 1, ParsingUtil.numberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(PRIORITY.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                priority = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
            if(CHANNEL.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                channel = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TEST, test.ordinal());
        nbt.putInt(PRIORITY, priority);
        nbt.putInt(CHANNEL, channel);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values());
        priority = nbt.getInt(PRIORITY);
        channel = nbt.getInt(CHANNEL);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(priority);
        buf.writeInt(channel);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
        this.priority = buf.readInt();
        this.channel = buf.readInt();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(PRIORITY, priority);
        json.addProperty(CHANNEL, channel);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        priority = json.get(PRIORITY).getAsInt();
        channel = json.get(CHANNEL).getAsInt();
    }
}
