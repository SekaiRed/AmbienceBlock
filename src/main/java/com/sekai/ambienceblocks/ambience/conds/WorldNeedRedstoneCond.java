package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        if(sourceIn instanceof AmbienceTileEntity)
            return equal.testFor(worldIn.isBlockPowered(((AmbienceTileEntity) sourceIn).getPos()));
        else
            return false;
    }

    //gui

    /*@Override
    public List<AmbienceWidgetHolder> getWidgets() {
        List<AmbienceWidgetHolder> list = new ArrayList<>();
        list.add(new AmbienceWidgetHolder(getName() + "." + EQUAL, new Button(0, 0, 20, 20, new StringTextComponent(equal.getName()), button -> {
            equal = equal.next();
            button.setMessage(new StringTextComponent(equal.getName()));
        })));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets) {

    }*/

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
        }
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
