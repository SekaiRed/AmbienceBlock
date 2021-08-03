package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.util.AmbienceTest;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class WorldDaytimeCond extends AbstractCond {
    private AmbienceTest test;
    private long value;

    private static final String TEST = "test";
    private static final String VALUE = "value";

    public WorldDaytimeCond(AmbienceTest test, long value) {
        this.test = test;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        WorldDaytimeCond cond = new WorldDaytimeCond(test, value);
        return cond;
    }

    @Override
    public String getName() {
        return "world.daytime";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value;
    }

    @Override
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn) {
        WorldInfo info = worldIn.getWorldInfo();
        //System.out.println(info.getWorldTime());
        return test.testForLong(info.getWorldTime()%24000, value);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetString(VALUE, "Daytime :", 50, Long.toString(value), 10, ParsingUtil.numberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseLong(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(TEST, test.ordinal());
        nbt.setLong(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        test = AmbienceTest.values()[nbt.getInteger(TEST) < AmbienceTest.values().length ? nbt.getInteger(TEST) : 0];
        value = nbt.getLong(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeLong(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = AmbienceTest.values()[buf.readInt()];
        this.value = buf.readLong();
    }
}
