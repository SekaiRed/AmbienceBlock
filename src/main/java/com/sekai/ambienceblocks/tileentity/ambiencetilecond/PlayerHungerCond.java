package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.client.gui.widgets.presets.textfield.CustomTextField;
import com.sekai.ambienceblocks.tileentity.util.AmbienceTest;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerHungerCond extends AbstractCond {
    private AmbienceTest test;
    private double value;

    private static final String TEST = "test";
    private static final String VALUE = "value";

    //gui
    /*private static Button bTest;
    private static Button bAxis;
    private static TextFieldWidget textValue;*/

    public PlayerHungerCond(AmbienceTest test, double value) {
        this.test = test;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        PlayerHungerCond cond = new PlayerHungerCond(test, value);
        return cond;
    }

    @Override
    public String getName() {
        return "player.hunger";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value;
    }

    @Override
    public boolean isTrue(Vec3d playerPos, BlockPos blockPos, World worldIn) {
        float val = Minecraft.getInstance().player.getFoodStats().getFoodLevel();
        return test.testForDouble(val, value);
    }

    //gui

    @Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + TEST, new Button(0, 0, 20, 20, test.getName(), button -> {
            test = test.next();
            button.setMessage(test.getName());
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
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(TEST, test.ordinal());
        nbt.putDouble(VALUE, value);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        test = AmbienceTest.values()[nbt.getInt(TEST) < AmbienceTest.values().length ? nbt.getInt(TEST) : 0];
        value = nbt.getDouble(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeDouble(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = AmbienceTest.values()[buf.readInt()];
        this.value = buf.readDouble();
    }
}
