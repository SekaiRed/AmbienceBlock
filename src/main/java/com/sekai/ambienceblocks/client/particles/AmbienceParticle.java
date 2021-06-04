package com.sekai.ambienceblocks.client.particles;

import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AmbienceParticle extends SpriteTexturedParticle {
    /*protected AmbienceParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }*/
    public AmbienceParticle(ClientWorld world, double x, double y, double z, IItemProvider itemProvider) {
        super(world, x, y, z);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(itemProvider));
        this.particleGravity = 0.0F;
        this.maxAge = 80;
        this.canCollide = false;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    public float getScale(float scaleFactor) {
        return 0.5F;
    }

    /*@OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AmbienceParticle(worldIn, x, y, z, RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get());
        }
    }*/

    /*static Constructor<BarrierParticle> constructor;

    {
        try {
            constructor = BarrierParticle.class.getDeclaredConstructor(ClientWorld.class, double.class, double.class, double.class, IItemProvider.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Particle getParticle(ClientWorld world, double x, double y, double z, IItemProvider itemProvider) {
        try {
            return constructor.newInstance(world, x, y, z, itemProvider);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
