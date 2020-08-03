package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.Main;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Item.Properties().group(Main.TAB));
    }
}
