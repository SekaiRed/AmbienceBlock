package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.blocks.AmbienceBlock;
import com.sekai.ambienceblocks.blocks.BlockItemBase;
import com.sekai.ambienceblocks.blocks.InvisibleAmbienceBlock;
import com.sekai.ambienceblocks.items.ItemCompendium;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Main.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Main.MODID);

    public static void init()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITY_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
        PARTICLE_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Blocks
    public static final RegistryObject<Block> AMBIENCE_BLOCK = BLOCKS.register("ambience_block", AmbienceBlock::new);
    public static final RegistryObject<Block> INVISIBLE_AMBIENCE_BLOCK = BLOCKS.register("invisible_ambience_block", InvisibleAmbienceBlock::new);

    //Block Items
    public static final RegistryObject<Item> AMBIENCE_BLOCK_ITEM = ITEMS.register("ambience_block", () -> new BlockItemBase(AMBIENCE_BLOCK.get()));
    public static final RegistryObject<Item> INVISIBLE_AMBIENCE_BLOCK_ITEM = ITEMS.register("invisible_ambience_block", () -> new BlockItemBase(INVISIBLE_AMBIENCE_BLOCK.get()));

    //Items
    public static final RegistryObject<Item> AMBIENCE_BLOCK_FINDER = ITEMS.register("ambience_block_finder", () -> new Item(new Item.Properties().group(Main.TAB)));
    //public static final RegistryObject<Item> AMBIENCE_COMPENDIUM = ITEMS.register("ambience_compendium", () -> new Item(new Item.Properties().group(Main.TAB)));
    public static final RegistryObject<Item> AMBIENCE_COMPENDIUM = ITEMS.register("ambience_compendium", ItemCompendium::new);
    //TODO override item code to add custom right click behavior for the ambience compendium

    //Tile Entities
    public static final RegistryObject<TileEntityType<?>> AMBIENCE_TILE_ENTITY =
            TILE_ENTITY_TYPE.register("ambience_block", () -> TileEntityType.Builder.create(AmbienceTileEntity::new, AMBIENCE_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<?>> INVISIBLE_AMBIENCE_TILE_ENTITY =
            TILE_ENTITY_TYPE.register("invisible_ambience_block", () -> TileEntityType.Builder.create(AmbienceTileEntity::new, INVISIBLE_AMBIENCE_BLOCK.get()).build(null));

    //Particles
    public static final RegistryObject<BasicParticleType> PARTICLE_SPEAKER =
            PARTICLE_TYPE.register("invisible_ambience_block", () -> new BasicParticleType(false));
}
