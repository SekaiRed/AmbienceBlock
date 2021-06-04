package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AlwaysTrueCond extends AbstractCond  {
    public AlwaysTrueCond() {
    }

    @Override
    public AbstractCond clone() {
        return new AlwaysTrueCond();
    }

    @Override
    public String getName() {
        return "always_true";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "]";
    }

    @Override
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn) {
        return true;
    }

    //gui

    @Override
    public List<AmbienceWidgetHolder> getWidgets() {
        return new ArrayList<>();
    }

    @Override
    public void getDataFromWidgets(List<AmbienceWidgetHolder> allWidgets) {

    }

    @Override
    public CompoundNBT toNBT() {
        return new CompoundNBT();
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
    }

    @Override
    public void toBuff(PacketBuffer buf) {

    }

    @Override
    public void fromBuff(PacketBuffer buf) {

    }
}
