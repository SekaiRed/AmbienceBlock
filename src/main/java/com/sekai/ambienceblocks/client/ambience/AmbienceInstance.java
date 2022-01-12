package com.sekai.ambienceblocks.client.ambience;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmbienceInstance extends AbstractTickableSoundInstance {
    private float internalVolume;
    private float internalPitch;
    private Vec3 internalPos;

    public AmbienceInstance(ResourceLocation soundId, SoundSource categoryIn, Vec3 pos, float volume, float pitch, boolean repeat, boolean global) {
        super(new SoundEvent(soundId), categoryIn);
        this.volume = volume;
        internalVolume = volume;
        this.pitch = pitch;
        internalPitch = pitch;
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();
        internalPos = pos;
        this.relative = global;
        //experiment
        this.looping = repeat;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.NONE;
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

    public void setBlockPos(Vec3 pos) {
        if(pos != null)
            internalPos = pos;
    }

    @Override
    public void tick() {
        //update volume if it changed
        if(internalVolume != volume) {
            this.volume = internalVolume;
        }

        if(internalPitch != pitch)
            this.pitch = internalPitch;

        if(internalPos.x() != x || internalPos.y() != y || internalPos.z() != z)
        {
            x = internalPos.x(); y = internalPos.y(); z = internalPos.z();
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
