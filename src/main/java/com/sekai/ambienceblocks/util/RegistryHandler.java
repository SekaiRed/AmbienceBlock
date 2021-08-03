package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.init.ModBlocks;
import com.sekai.ambienceblocks.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegistryHandler {
    /*public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Main.MODID);

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
            TILE_ENTITY_TYPE.register("ambience_block", () -> TileEntityType.Builder.create(AmbienceTileEntity::new, AMBIENCE_BLOCK.get()).build(null));*/

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
    }

    /*@SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }*/

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        for(Item item : ModItems.ITEMS)
        {
            if(item instanceof IHasModel)
            {
                ((IHasModel)item).registerModels();
            }
        }

        for(Block block : ModBlocks.BLOCKS)
        {
            if(block instanceof IHasModel)
            {
                ((IHasModel)block).registerModels();
            }
        }
    }
}
