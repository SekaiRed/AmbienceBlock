package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerHealthCond extends AbstractCond {
    private AmbienceTest test;
    private double value;

    private static final String TEST = "test";
    private static final String VALUE = "value";

    //gui
    /*private static Button bTest;
    private static Button bAxis;
    private static TextFieldWidget textValue;*/

    public PlayerHealthCond(AmbienceTest test, double value) {
        this.test = test;
        this.value = value;
    }

    @Override
    public AbstractCond clone() {
        PlayerHealthCond cond = new PlayerHealthCond(test, value);
        return cond;
    }

    @Override
    public String getName() {
        return "player.health";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + value;
    }

    @Override
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        float val = Minecraft.getInstance().player.getHealth();
        return test.testForDouble(val, value);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        list.add(new AmbienceWidgetString(VALUE, "Health :", 50, Double.toString(value), 8, ParsingUtil.decimalNumberFilter));
        return list;
        /*List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + TEST, new Button(0, 0, 20, 20, new StringTextComponent(test.getName()), button -> {
            test = test.next();
            button.setMessage(new StringTextComponent(test.getName()));
        })));
        list.add(new AmbienceWidgetHolder(getName() + "." + VALUE, new CustomTextField(0, 0, 50, 20, "")));
        ((CustomTextField) list.get(list.size() - 1).get()).setText(Double.toString(value));
        return list;*/
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if(VALUE.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                value = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            /*if(widgetHolder.isKey(getName() + "." + VALUE) && widgetHolder.get() instanceof CustomTextField) {
                value = ParsingUtil.tryParseDouble(((CustomTextField) widgetHolder.get()).getText());
            }*/
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
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values()); //AmbienceTest.values()[nbt.getInt(TEST) < AmbienceTest.values().length ? nbt.getInt(TEST) : 0];
        value = nbt.getDouble(VALUE);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(test.ordinal());
        buf.writeDouble(value);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values()); //AmbienceTest.values()[buf.readInt()];
        this.value = buf.readDouble();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(TEST, test.name());
        json.addProperty(VALUE, value);
    }

    @Override
    public void fromJson(JsonObject json) {
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        value = json.get(VALUE).getAsDouble();
    }
}
