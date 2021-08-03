package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.util.AmbienceAxis;
import com.sekai.ambienceblocks.tileentity.util.AmbienceTest;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerPosAxisCond extends AbstractCond {
    private AmbienceTest test;
    private AmbienceWorldSpace space;
    private AmbienceAxis axis;
    private double value;

    private static final String TEST = "test";
    private static final String SPACE = "space";
    private static final String AXIS = "axis";
    private static final String VALUE = "value";

    //gui
    /*private static Button bTest;
    private static Button bAxis;
    private static TextFieldWidget textValue;*/

    public PlayerPosAxisCond(AmbienceTest test, AmbienceWorldSpace space, AmbienceAxis axis, double value) {
        this.test = test;
        this.space = space;
        this.axis = axis;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        PlayerPosAxisCond cond = new PlayerPosAxisCond(test, space, axis, value);
        return cond;
    }

    @Override
    public String getName() {
        return "player.pos.axis";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + space.getName() + " " + value + " " + axis.toString();
    }

    @Override
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn) {
        double playerValue = 0.0D;
        if(AmbienceWorldSpace.ABSOLUTE.equals(space)) {
            if (axis == AmbienceAxis.X) playerValue = playerPos.x;
            if (axis == AmbienceAxis.Y) playerValue = playerPos.y;
            if (axis == AmbienceAxis.Z) playerValue = playerPos.z;
        } else {
            if (axis == AmbienceAxis.X) playerValue = playerPos.x - tileIn.getOrigin().x;
            if (axis == AmbienceAxis.Y) playerValue = playerPos.y - tileIn.getOrigin().y;
            if (axis == AmbienceAxis.Z) playerValue = playerPos.z - tileIn.getOrigin().z;
        }
        return test.testForDouble(playerValue, value);
    }

    //gui

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetEnum<>(SPACE, "",20, space));
        list.add(new AmbienceWidgetEnum<>(AXIS, "Axis :",20, axis));
        list.add(new AmbienceWidgetString(VALUE, "Pos :", 50, Double.toString(value), 12, ParsingUtil.negativeDecimalNumberFilter));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(SPACE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                space = (AmbienceWorldSpace) ((AmbienceWidgetEnum) widget).getValue();
            if(AXIS.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                axis = (AmbienceAxis) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(TEST, test.ordinal());
        nbt.setInteger(SPACE, space.ordinal());
        nbt.setInteger(AXIS, axis.ordinal());
        nbt.setDouble(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        test = AmbienceTest.values()[nbt.getInteger(TEST) < AmbienceTest.values().length ? nbt.getInteger(TEST) : 0];
        space = AmbienceWorldSpace.values()[nbt.getInteger(SPACE) < AmbienceWorldSpace.values().length ? nbt.getInteger(SPACE) : 0];
        axis = AmbienceAxis.values()[nbt.getInteger(AXIS) < AmbienceAxis.values().length ? nbt.getInteger(AXIS) : 0];
        value = nbt.getDouble(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeInt(space.ordinal());
        buf.writeInt(axis.ordinal());
        buf.writeDouble(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = AmbienceTest.values()[buf.readInt()];
        this.space = AmbienceWorldSpace.values()[buf.readInt()];
        this.axis = AmbienceAxis.values()[buf.readInt()];
        this.value = buf.readDouble();
    }

    /*@Override
    public void setFieldFromData() {

    }

    @Override
    public void setDataFromField() {

    }*/

    //only called when a button is pressed
}