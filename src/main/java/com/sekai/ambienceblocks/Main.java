package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.ambience.compendium.ServerCompendium;
import com.sekai.ambienceblocks.ambience.sync.target.TargetSyncServer;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.client.rendering.RenderTypeHelper;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ambienceblocks")
public class Main
{
    public static final String MODID = "ambienceblocks";

    private static final Logger LOGGER = LogManager.getLogger();

    //private TargetSyncHandler targetSyncHandler;
    //private ServerCompendium serverCompendium;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::registerParticles);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AmbienceConfig.COMMON_SPEC);

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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderTypeHelper::registerShadersEvent);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::registerParticles);
        //FMLJavaModLoadingContext.get().getModEventBus().register(RenderTypeHelper.class);
        //Mod.EventBusSubscriber.Bus.MOD.bus().get().register(RenderTypeHelper::registerShadersEvent);
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        //Register targetting system for battle conds
        MinecraftForge.EVENT_BUS.register(new TargetSyncServer());

        //Register server compendium
        MinecraftForge.EVENT_BUS.register(new ServerCompendium(LOGGER));
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        /*MinecraftForge.EVENT_BUS.unregister(targetSyncHandler);
        targetSyncHandler = null;*/
    }

    public static final CreativeModeTab TAB = new CreativeModeTab("ambiencetab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryHandler.AMBIENCE_BLOCK.get());
        }
    };
}
