package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.compendium.ServerCompendium;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.util.TargetSyncHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ambienceblocks")
public class Main
{
    public static final String MODID = "ambienceblocks";

    private static final Logger LOGGER = LogManager.getLogger();

    private TargetSyncHandler targetSyncHandler;
    //private ServerCompendium serverCompendium;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        new AmbienceController(); //init the ambience controller
        MinecraftForge.EVENT_BUS.register(AmbienceController.instance);

        //Register client compendium
        MinecraftForge.EVENT_BUS.register(AmbienceController.instance.compendium);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        targetSyncHandler = new TargetSyncHandler();
        MinecraftForge.EVENT_BUS.register(targetSyncHandler);

        //Register server compendium
        MinecraftForge.EVENT_BUS.register(new ServerCompendium(LOGGER));
    }

    @SubscribeEvent
    public void onServerStop(FMLServerStoppingEvent event) {
        MinecraftForge.EVENT_BUS.unregister(targetSyncHandler);
        targetSyncHandler = null;

        //Unregister server compendium
        //MinecraftForge.EVENT_BUS.unregister(serverCompendium);
        //serverCompendium = null;
    }

    public static final ItemGroup TAB = new ItemGroup("ambiencetab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.AMBIENCE_BLOCK.get());
        }
    };
}
