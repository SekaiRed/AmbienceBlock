package com.sekai.ambienceblocks.items;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.init.ModBlocks;
import com.sekai.ambienceblocks.init.ModItems;
import com.sekai.ambienceblocks.util.IHasModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ItemBase extends Item implements IHasModel {
    public ItemBase(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(Main.MYTAB);

        //ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
