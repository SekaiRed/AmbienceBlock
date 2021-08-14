package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
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
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(TEST, test.ordinal());
        nbt.putInt(PRIORITY, priority);
        nbt.putInt(CHANNEL, channel);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        test = AmbienceTest.values()[nbt.getInt(TEST) < AmbienceTest.values().length ? nbt.getInt(TEST) : 0];
        priority = nbt.getInt(PRIORITY);
        channel = nbt.getInt(CHANNEL);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(priority);
        buf.writeInt(channel);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = AmbienceTest.values()[buf.readInt()];
        this.priority = buf.readInt();
        this.channel = buf.readInt();
    }
}
