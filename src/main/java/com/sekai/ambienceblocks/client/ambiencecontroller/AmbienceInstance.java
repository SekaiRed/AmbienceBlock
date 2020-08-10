package com.sekai.ambienceblocks.client.ambiencecontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmbienceInstance extends TickableSound {
    private float internalVolume = 1f;
    private float internalPitch = 1f;
    private BlockPos internalPos;

    public AmbienceInstance(ResourceLocation soundId, SoundCategory categoryIn, BlockPos pos, float volume, float pitch, boolean repeat) {
        super(new SoundEvent(soundId), categoryIn);
        this.volume = volume;
        internalVolume = volume;
        this.pitch = pitch;
        internalPitch = pitch;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        internalPos = pos;
        //this.repeat = false;
        //this.repeatDelay = 0;
        this.attenuationType = AttenuationType.LINEAR;
        this.global = false;
        //experiment
        this.repeat = repeat;
        this.repeatDelay = 0;
    }

    public void setVolume(float volume) {
        if(volume < 0f) {
            internalVolume = 0f;
            return;
        }

        internalVolume = volume;
    }

    public void setPitch(float pitch) {
        if(pitch < 0f) {
            internalPitch = 0f;
            return;
        }

        internalPitch = pitch;
    }

    public void setBlockPos(BlockPos pos) {
        if(pos != null)
            internalPos = pos;
    }

    @Override
    public void tick() {
        //update volume if it changed
        if(internalVolume != volume)
            this.volume = internalVolume;

        if(internalPitch != pitch)
            this.pitch = internalPitch;

        if(internalPos.getX() != x || internalPos.getY() != y || internalPos.getZ() != z)
        {
            //update position if it changed
            x = internalPos.getX(); y = internalPos.getY(); z = internalPos.getZ();
        }
    }
}
