package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.util.AmbienceEquality;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWeather;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class WorldNeedRedstoneCond extends AbstractCond {
    private AmbienceEquality equal;

    private static final String EQUAL = "equal";

    public WorldNeedRedstoneCond(AmbienceEquality equal) {
        this.equal = equal;
    }

    @Override
    public AbstractCond clone() {
        return new WorldNeedRedstoneCond(equal);
    }

    @Override
    public String getName() {
        return "world.redstone";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName();
    }

    @Override
    public boolean isTrue(Vec3d playerPos, BlockPos blockPos, World worldIn) {
        return equal.testFor(worldIn.isBlockPowered(blockPos));
    }

    //gui

    @Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + EQUAL, new Button(0, 0, 20, 20, equal.getName(), button -> {
            equal = equal.next();
            button.setMessage(equal.getName());
        })));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets) {

    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        equal = AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
    }
}
