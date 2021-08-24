package com.sekai.ambienceblocks.client.ambience;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmbienceInstance extends TickableSound {
    private float internalVolume;
    private float internalPitch;
    private Vector3d internalPos;

    public AmbienceInstance(ResourceLocation soundId, SoundCategory categoryIn, Vector3d pos, float volume, float pitch, boolean repeat, boolean global) {
        super(new SoundEvent(soundId), categoryIn);
        this.volume = volume;
        internalVolume = volume;
        this.pitch = pitch;
        internalPitch = pitch;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        internalPos = pos;
        this.global = global;
        //experiment
        this.repeat = repeat;
        this.repeatDelay = 0;
        this.attenuationType = AttenuationType.NONE;
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

    public void setBlockPos(Vector3d pos) {
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

        if(internalPos.getX() != x || internalPos.getY() != y || internalPos.getZ() != z)
        {
            x = internalPos.getX(); y = internalPos.getY(); z = internalPos.getZ();
        }
    }

    @Override
    public boolean canBeSilent() {
        return true;
    }
}
