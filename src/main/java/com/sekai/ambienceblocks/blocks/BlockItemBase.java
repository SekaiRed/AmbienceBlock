package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.Main;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Properties().tab(Main.TAB));
    }
}
