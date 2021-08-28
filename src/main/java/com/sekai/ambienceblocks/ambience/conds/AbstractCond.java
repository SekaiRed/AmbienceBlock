package com.sekai.ambienceblocks.ambience.conds;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractCond {
    public AbstractCond() {
    }
    public abstract AbstractCond clone();

    public abstract String getName();
    public abstract String getListDescription();
    public abstract boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn);

    //gui
    public abstract List<AbstractAmbienceWidgetMessenger> getWidgets();
    public abstract void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets);
    //public abstract List<AmbienceWidgetHolder> getWidgets();
    //public abstract void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets);

    //nbt
    public abstract NBTTagCompound toNBT();
    public abstract void fromNBT(NBTTagCompound nbt);

    //buff
    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);
}