package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.client.particles.AmbienceParticleFactory;
import com.sekai.ambienceblocks.util.EventHandler;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.client.gui.DebugOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ambienceblocks")
public class Main
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "ambienceblocks";

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
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        //new MusicController();
        //MinecraftForge.EVENT_BUS.register(MusicController.instance);
        new AmbienceController();
        MinecraftForge.EVENT_BUS.register(AmbienceController.instance);
        //MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(new DebugOverlay());
    }

    /*@SubscribeEvent
    public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
        System.out.println("I am a unique string");
        Minecraft.getInstance().particles.registerFactory(RegistryHandler.PARTICLE_SPEAKER.get(), new AmbienceParticleFactory());
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }

    public static final ItemGroup TAB = new ItemGroup("ambiencetab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.AMBIENCE_BLOCK.get());
        }
    };
}
