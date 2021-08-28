package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
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
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn) {
        return test.testForInt(priority, AmbienceController.instance.getHighestPriorityByChannel(channel));
        //float val = Minecraft.getInstance().player.getHealth();
        //return test.testForDouble(val, value);
    }

    //gui

    /*@Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + TEST, new Button(0, 0, 20, 20, new StringTextComponent(test.getName()), button -> {
            test = test.next();
            button.setMessage(new StringTextComponent(test.getName()));
        })));
        list.add(new AmbienceWidgetHolder(getName() + "." + VALUE, new CustomTextField(0, 0, 50, 20, "")));
        ((CustomTextField) list.get(list.size() - 1).get()).setText(Double.toString(value));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets) {
        for(AmbienceWidgetHolder widgetHolder : allWidgets) {
            if(widgetHolder.isKey(getName() + "." + VALUE) && widgetHolder.get() instanceof CustomTextField) {
                value = ParsingUtil.tryParseDouble(((CustomTextField) widgetHolder.get()).getText());
            }
        }
    }*/

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
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(TEST, test.ordinal());
        nbt.setInteger(PRIORITY, priority);
        nbt.setInteger(CHANNEL, channel);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        test = AmbienceTest.values()[nbt.getInteger(TEST) < AmbienceTest.values().length ? nbt.getInteger(TEST) : 0];
        priority = nbt.getInteger(PRIORITY);
        channel = nbt.getInteger(CHANNEL);
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
