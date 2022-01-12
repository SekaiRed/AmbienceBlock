package com.sekai.ambienceblocks.client.particles;

import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpeakerParticle extends TextureSheetParticle {
    SpeakerParticle(ClientLevel level, double x, double y, double z, BlockState state) {
        super(level, x, y, z);
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state));
        this.gravity = 0.0F;
        this.lifetime = 80;
        this.hasPhysics = false;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public float getQuadSize(float p_172363_) {
        return 0.5F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class SpeakerProvider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpeakerParticle(level, x, y, z, RegistryHandler.INVISIBLE_AMBIENCE_BLOCK.get().defaultBlockState());
        }
    }
}
