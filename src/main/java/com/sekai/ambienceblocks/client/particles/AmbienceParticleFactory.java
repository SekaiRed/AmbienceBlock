package com.sekai.ambienceblocks.client.particles;

import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.particles.BasicParticleType;

import javax.annotation.Nullable;

public class AmbienceParticleFactory implements IParticleFactory<BasicParticleType> {
    @Nullable
    @Override
    public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new AmbienceParticle(worldIn, x, y, z, RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get());
    }
}
