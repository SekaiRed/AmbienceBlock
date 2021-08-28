package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class AmbienceInstance extends MovingSound {
    private float internalVolume = 1f;
    private float internalPitch = 1f;
    private Vector3d internalPos;

    public AmbienceInstance(ResourceLocation soundId, SoundCategory categoryIn, Vector3d pos, float volume, float pitch, boolean repeat) {
        super(new SoundEvent(soundId), categoryIn);
        this.volume = volume;
        internalVolume = volume;
        this.pitch = pitch;
        internalPitch = pitch;
        this.xPosF = (float) pos.x;
        this.yPosF = (float) pos.y;
        this.zPosF = (float) pos.z;
        internalPos = pos;
        //this.global = false; This option doesn't exist in this version
        //experiment
        this.repeat = repeat;
        this.repeatDelay = 0;
        this.attenuationType = ISound.AttenuationType.NONE;
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
    public void update() {
        //update volume if it changed
        if(internalVolume != volume) {
            this.volume = internalVolume;
        }

        if(internalPitch != pitch)
            this.pitch = internalPitch;

        if(internalPos.getX() != xPosF || internalPos.getY() != yPosF || internalPos.getZ() != zPosF)
        {
            //update position if it changed
            xPosF = (float) internalPos.x; yPosF = (float) internalPos.y; zPosF = (float) internalPos.z;
        }
    }

    protected final void finishPlaying() {
        this.donePlaying = true;
        this.repeat = false;
    }
}
