package com.sekai.ambienceblocks.init;

import com.sekai.ambienceblocks.blocks.AmbienceBlock;
import com.sekai.ambienceblocks.items.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item AMBIENCE_BLOCK_FINDER = new ItemBase("ambience_block_finder");
}
