package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetMessenger;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public abstract class AbstractCond {
    public AbstractCond() {
    }
    public abstract AbstractCond clone();

    public abstract String getName();
    public abstract String getListDescription();
    public abstract boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn);

    //gui
    public abstract List<AmbienceWidgetMessenger> getWidgets();
    public abstract void getDataFromWidgets(List<AmbienceWidgetMessenger> allWidgets);
    //public abstract List<AmbienceWidgetHolder> getWidgets();
    //public abstract void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets);

    //nbt
    public abstract CompoundNBT toNBT();
    public abstract void fromNBT(CompoundNBT nbt);

    //buff
    public abstract void toBuff(PacketBuffer buf);
    public abstract void fromBuff(PacketBuffer buf);
}