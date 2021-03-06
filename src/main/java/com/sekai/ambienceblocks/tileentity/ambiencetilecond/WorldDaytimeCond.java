package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.presets.textfield.CustomTextField;
import com.sekai.ambienceblocks.tileentity.util.AmbienceAxis;
import com.sekai.ambienceblocks.tileentity.util.AmbienceTest;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWeather;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class WorldDaytimeCond extends AbstractCond {
    private AmbienceTest test;
    private double value;

    private static final String TEST = "test";
    private static final String VALUE = "value";

    public WorldDaytimeCond(AmbienceTest test, double value) {
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
    public boolean isTrue(Vec3d playerPos, BlockPos blockPos, World worldIn) {
        WorldInfo info = worldIn.getWorldInfo();
        return test.testForLong(info.getDayTime()%24000, (long) value);
    }

    //gui

    @Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();

        list.add(new AmbienceWidgetHolder(getName() + "." + TEST, new Button(0, 0, 20, 20, test.getName(), button -> {
            test = test.next();
            button.setMessage(test.getName());
        })));

        /*list.add(new AmbienceWidgetHolder(getName() + "." + TEST, new ScrollListWidget(16, 16, 20, 20, 4, 16, AmbienceTest.getStringValues(), Minecraft.getInstance().fontRenderer, new ScrollListWidget.IPressable() {
            @Override
            public void onChange(ScrollListWidget list, int index, String name) {
                test = AmbienceTest.getValueFromString(name);
            }
        })));
        ((ScrollListWidget) list.get(list.size() - 1).get()).setSelectionByString(test.getName());*/

        list.add(new AmbienceWidgetHolder(getName() + "." + VALUE, new CustomTextField(0, 0, 100, 20, "")));
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
