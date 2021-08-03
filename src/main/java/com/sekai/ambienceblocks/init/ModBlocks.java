package com.sekai.ambienceblocks.init;

import com.sekai.ambienceblocks.blocks.AmbienceBlock;
import com.sekai.ambienceblocks.blocks.InvisibleAmbienceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block AMBIENCE_BLOCK = new AmbienceBlock("ambience_block", Material.IRON);
    public static final Block INVISIBLE_AMBIENCE_BLOCK = new InvisibleAmbienceBlock("invisible_ambience_block", Material.IRON);
}
