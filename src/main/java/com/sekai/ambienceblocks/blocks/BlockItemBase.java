package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.Main;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Properties().group(Main.TAB));
    }
}
