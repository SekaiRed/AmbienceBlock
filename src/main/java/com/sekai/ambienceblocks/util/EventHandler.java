package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.client.particles.AmbienceParticleFactory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {
    @SubscribeEvent
    public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
        System.out.println("aaaaaaaaaa");
        Minecraft.getInstance().particles.registerFactory(RegistryHandler.PARTICLE_SPEAKER.get(), new AmbienceParticleFactory());
    }
}
