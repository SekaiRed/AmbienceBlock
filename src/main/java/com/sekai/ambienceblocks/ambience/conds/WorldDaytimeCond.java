package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.IWorldInfo;

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
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        IWorldInfo info = worldIn.getWorldInfo();
        return test.testForLong(info.getDayTime()%24000, (long) value);
    }

    //gui
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
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(TEST, test.ordinal());
        nbt.putLong(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values()); //AmbienceTest.values()[nbt.getInt(TEST) < AmbienceTest.values().length ? nbt.getInt(TEST) : 0];
        value = nbt.getLong(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeLong(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values()); //AmbienceTest.values()[buf.readInt()];
        this.value = buf.readLong();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(VALUE, value);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        value = json.get(VALUE).getAsLong();
    }
}
