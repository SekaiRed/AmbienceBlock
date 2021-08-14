package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.client.particles.AmbienceParticleFactory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//having only one class for this is kinda sad, alexa play ungravel
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleRegistryHandler {
    @SubscribeEvent
    public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(RegistryHandler.PARTICLE_SPEAKER.get(), new AmbienceParticleFactory());
    }
}
