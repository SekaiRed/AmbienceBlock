package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.client.gui.DebugOverlay;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.util.TargetSyncHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
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
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        //new MusicController();
        //MinecraftForge.EVENT_BUS.register(MusicController.instance);
        new AmbienceController();
        MinecraftForge.EVENT_BUS.register(AmbienceController.instance);
        //MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(new DebugOverlay());
    }

    @SubscribeEvent
    public void doServerStuff(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(new TargetSyncHandler());
    }

    //I have to update mob target manually because vanilla minecraft doesn't sync that up with the client by default
    //TODO this event actually gets spammed and clogs up the system, add a new event handler with this that keep track of how long it has been since it last pinged clients
    /*@SubscribeEvent
    public void onTargettingEvent(LivingSetAttackTargetEvent event) {
        //System.out.println(event.getEntityLiving().world.isRemote + " " + event.getTarget());
        if(event.getTarget() instanceof ServerPlayerEntity)
            PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getTarget()), new PacketTargeting(event.getEntityLiving().getEntityId()));

        //The mob is no longer targetting the player, notify the clients that are looking at this entity
        if(event.getTarget() == null)
            PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new PacketTargeting(event.getEntityLiving().getEntityId(), true));
    }*/

    /*@SubscribeEvent
    public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
        System.out.println("I am a unique string");
        Minecraft.getInstance().particles.registerFactory(RegistryHandler.PARTICLE_SPEAKER.get(), new AmbienceParticleFactory());
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    /*@SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }*/

    public static final ItemGroup TAB = new ItemGroup("ambiencetab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.AMBIENCE_BLOCK.get());
        }
    };
}
