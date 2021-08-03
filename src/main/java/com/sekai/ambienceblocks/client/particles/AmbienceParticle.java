package com.sekai.ambienceblocks.client.particles;

import com.sekai.ambienceblocks.init.ModBlocks;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AmbienceParticle extends Barrier {
    public AmbienceParticle(World worldIn, double p_i46286_2_, double p_i46286_4_, double p_i46286_6_, Item p_i46286_8_) {
        super(worldIn, p_i46286_2_, p_i46286_4_, p_i46286_6_, p_i46286_8_);
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
    {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new AmbienceParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(ModBlocks.INVISIBLE_AMBIENCE_BLOCK));
        }
    }
}
