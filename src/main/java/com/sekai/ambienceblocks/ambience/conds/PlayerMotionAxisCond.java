package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PlayerMotionAxisCond extends AbstractCond {
    private AmbienceTest test;
    private AmbienceAxis axis;
    private double value;

    private static final String TEST = "test";
    private static final String AXIS = "axis";
    private static final String VALUE = "value";

    public PlayerMotionAxisCond(AmbienceTest test, AmbienceAxis axis, double value) {
        this.test = test;
        this.axis = axis;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        PlayerMotionAxisCond cond = new PlayerMotionAxisCond(test, axis, value);
        return cond;
    }

    @Override
    public String getName() {
        return "player.motion.axis";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value + " " + axis.toString();
    }

    //TODO Does this work?
    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        //double playerValue = 0.0D;
        switch (axis) {
            case X: return test.testForDouble(player.getX() - player.xo, value);
            case Y: return test.testForDouble(player.getY() - player.yo, value);
            case Z: return test.testForDouble(player.getZ() - player.zo, value);
        }
        /*if (axis == AmbienceAxis.X) playerValue = player.getPosX() - player.prevPosX;
        if (axis == AmbienceAxis.Y) playerValue = player.getPosY() - player.prevPosY;
        if (axis == AmbienceAxis.Z) playerValue = player.getPosZ() - player.prevPosZ;
        return test.testForDouble(playerValue, value);*/
        return false;
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetEnum<>(AXIS, "Axis :",20, axis));
        list.add(new AmbienceWidgetString(VALUE, "Pos :", 50, Double.toString(value), 12, ParsingUtil.negativeDecimalNumberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(AXIS.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                axis = (AmbienceAxis) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TEST, test.ordinal());
        nbt.putInt(AXIS, axis.ordinal());
        nbt.putDouble(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values());
        axis = StaticUtil.getEnumValue(nbt.getInt(AXIS), AmbienceAxis.values());
        value = nbt.getDouble(VALUE);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(axis.ordinal());
        buf.writeDouble(value);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
        this.axis = StaticUtil.getEnumValue(buf.readInt(), AmbienceAxis.values());
        this.value = buf.readDouble();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(AXIS, axis.name());
        json.addProperty(VALUE, value);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        axis = StaticUtil.getEnumValue(json.get(AXIS).getAsString(), AmbienceAxis.values());
        value = json.get(VALUE).getAsDouble();
    }
}
