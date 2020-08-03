package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.blocks.AmbienceBlock;
import com.sekai.ambienceblocks.blocks.BlockItemBase;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Main.MODID);

    public static void init()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITY_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Items

    //Blocks
    public static final RegistryObject<Block> AMBIENCE_BLOCK = BLOCKS.register("ambience_block", AmbienceBlock::new);

    //Block Items
    public static final RegistryObject<Item> AMBIENCE_BLOCK_ITEM = ITEMS.register("ambience_block", () -> new BlockItemBase(AMBIENCE_BLOCK.get()));

    //Tile Entities
    public static final RegistryObject<TileEntityType<?>> AMBIENCE_TILE_ENTITY =
            TILE_ENTITY_TYPE.register("ambience_block", () -> TileEntityType.Builder.create(AmbienceTileEntity::new, AMBIENCE_BLOCK.get()).build(null));
}
