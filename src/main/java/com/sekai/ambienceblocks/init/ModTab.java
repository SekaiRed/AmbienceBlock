package com.sekai.ambienceblocks.init;

import com.sekai.ambienceblocks.Main;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModTab extends CreativeTabs {

    public ModTab() {
        super(Main.MODID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.AMBIENCE_BLOCK);
    }

}