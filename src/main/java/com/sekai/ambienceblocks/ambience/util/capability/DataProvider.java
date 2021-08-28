package com.sekai.ambienceblocks.ambience.util.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DataProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IData.class)
    public static final Capability<IData> DATA = null;

    private IData instance = DATA.getDefaultInstance();

    public DataProvider() {}

    public DataProvider(IData data)
    {
        this.instance = data;
    }

	/*public StatsProvider(Capability<IStats> stat, @Nullable EnumFacing facing, IStats instance)
	{
		this.STAT_CAP = stat;
		this.DEFAULT_FACING = facing;
		this.instance = instance;
		//return new StatsProvider(STAT_CAP, DEFAULT_FACING, level);
	}*/

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == DATA;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == DATA ? DATA.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return DATA.getStorage().writeNBT(DATA, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        DATA.getStorage().readNBT(DATA, this.instance, null, nbt);
    }
}