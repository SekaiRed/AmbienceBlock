package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class AmbienceSlot {
    private final SoundHandler handler;
    private AmbienceInstance instance;
    private IAmbienceSource source;

    private AmbienceSoundState stateSnd;
    private AmbienceFadeState stateFade;

    private boolean hasCachedVolume = false;
    private float cachedVolume = 0f;
    private boolean hasCachedPitch = false;
    private float cachedPitch = 0f;

    //exclusive to fusing, forces a certain volume
    private boolean hasForcedVolume = false;
    private float forcedVolume = 0f;
    private boolean hasForcedPitch = false;
    private float forcedPitch = 0f;

    //volume modifier for fade in and out
    private float multVolume = 1f;
    //fade tick counter
    private int fadeCounter = 0;

    private boolean markForDeletion = false;

    public AmbienceSlot(SoundHandler handler, IAmbienceSource source) {
        this.handler = handler;
        this.source = source;
    }

    public void play() {
        AmbienceData d = source.getData();

        if(d.getFadeIn() > 0)
            setFadeState(AmbienceFadeState.FADE_IN);
        else
            setFadeState(AmbienceFadeState.MAIN);

        ResourceLocation playingResource;
        if(d.getType().equals(AmbienceType.MUSIC.getName())) {
            if(AmbienceController.isValidSound(source.getData().getIntroName())) {
                playingResource = new ResourceLocation(source.getData().getIntroName());
                stateSnd = AmbienceSoundState.INTRO;
            } else {
                playingResource = new ResourceLocation(source.getData().getSoundName());
                stateSnd = AmbienceSoundState.LOOP;
            }
        } else {
            playingResource = new ResourceLocation(source.getData().getSoundName());
            stateSnd = AmbienceSoundState.LOOP;
        }

        if(d.isUsingDelay())
            stateSnd = AmbienceSoundState.SINGLE;

        if(AmbienceSoundState.LOOP.equals(stateSnd) && !d.isUsingDelay())
            instance = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), source.getOrigin(), getVolumeInternal(d), getPitchInternal(d), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        else
            instance = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), source.getOrigin(), getVolumeInternal(d), getPitchInternal(d), false);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);

        handler.playSound(instance);
    }

    public boolean isStopping() {
        return AmbienceSoundState.OUTRO.equals(stateSnd) || AmbienceFadeState.FADE_OUT.equals(stateFade);
    }

    public void stop() {
        AmbienceData d = source.getData();

        //don't apply stop logic if we're already in the outro or fade out state
        if(AmbienceSoundState.OUTRO.equals(stateSnd) || AmbienceFadeState.FADE_OUT.equals(stateFade))
            return;

        //is it a music type?
        if(d.getType().equals(AmbienceType.MUSIC.getName())) {
            //does it have an outro?
            if(AmbienceController.isValidSound(d.getOutroName())) {
                ResourceLocation playingResource = new ResourceLocation(d.getOutroName());
                handler.stopSound(instance);
                instance = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), source.getOrigin(), getVolumeInternal(d), d.getPitch(), false);
                handler.playSound(instance);
                stateSnd = AmbienceSoundState.OUTRO;
            }
        }

        //does it have a fade out?
        if(d.getFadeOut() > 0) {
            if(AmbienceFadeState.FADE_IN.equals(stateFade)) {
                //still fading in, keep the progress made by the fade in for fading out
                float cacheVolumeMult = multVolume;
                setFadeState(AmbienceFadeState.FADE_OUT);
                fadeCounter = (int) ((1 - cacheVolumeMult) * d.getFadeOut());
            } else {
                setFadeState(AmbienceFadeState.FADE_OUT);
            }
        }

        //no outro or fade out so just stop it now
        if(!AmbienceSoundState.OUTRO.equals(stateSnd) && !AmbienceFadeState.FADE_OUT.equals(stateFade))
            forceStop();
    }

    private void setFadeState(AmbienceFadeState state) {
        fadeCounter = 0;
        //if i don't do this it explodes in your ears when it starts playing
        if(AmbienceFadeState.FADE_IN.equals(state))
            multVolume = .00001f;
        else
            multVolume = 1f;
        stateFade = state;
    }

    public void forceStop() {
        handler.stopSound(instance);
        markForDeletion();
    }

    private boolean isPlaying(ISound sound) {
        //return AmbienceController.instance.isPlaying(sound);
        return handler.isSoundPlaying(instance);
    }

    public void tick() {
        AmbienceData d = source.getData();

        if(AmbienceSoundState.SINGLE.equals(stateSnd)) {
            if(!isPlaying(instance)) /*if(!handler.isPlaying(instance))*/ {
                //it's done playing the sound, let's just stop here
                forceStop();
                return;
            }
        }

        //intro/outro logic
        if(AmbienceSoundState.INTRO.equals(stateSnd)) {
            if(!isPlaying(instance)) /*if(!handler.isPlaying(instance))*/ {
                //System.out.println("end reached lol " + instance.canRepeat());
                ResourceLocation playingResource = new ResourceLocation(d.getSoundName());
                handler.stopSound(instance);
                instance = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), source.getOrigin(), getVolumeInternal(d), d.getPitch(), true);
                handler.playSound(instance);
                stateSnd = AmbienceSoundState.LOOP;
            }
        } else if(AmbienceSoundState.OUTRO.equals(stateSnd)) {
            if(!isPlaying(instance)) /*if(!handler.isPlaying(instance))*/ {
                //it's done playing the outro :(
                forceStop();
            }
        }

        //fade in and out logic
        if(!AmbienceFadeState.MAIN.equals(stateFade)) {
            fadeCounter++;
            if(AmbienceFadeState.FADE_IN.equals(stateFade)) {
                if(fadeCounter >= d.getFadeIn()) {
                    //it's done fading in :)
                    setFadeState(AmbienceFadeState.MAIN);
                } else {
                    multVolume = 1f * fadeCounter / d.getFadeIn();
                }
            } else if(AmbienceFadeState.FADE_OUT.equals(stateFade)) {
                if(fadeCounter >= d.getFadeOut()) {
                    //it's done fading out :(
                    forceStop();
                } else {
                    multVolume = 1f - 1f * fadeCounter / d.getFadeOut();
                }
            }
        }

        //volume pitch and other misc
        setVolume(getVolumeInternal(d));
        setPitch(getPitchInternal(d));//? shouldn't once be enough
    }

    public void forceVolumeAndPitchUpdate() {
        AmbienceData d = source.getData();
        setVolume(getVolumeInternal(d));
        setPitch(getPitchInternal(d));
    }

    private float getVolumeInternal(AmbienceData d) {
        if(hasForcedVolume) {
            return multVolume * getForcedVolume();
        } else {
            float volume;
            if(!hasCachedVolume)
                volume = d.getVolume();
            else
                volume = getCachedVolume();

            if (d.isGlobal())
                return multVolume * volume;
            else
                return (float) (multVolume * volume * d.getPercentageHowCloseIsPlayer(Minecraft.getMinecraft().player, source.getOrigin()));
        }
    }

    private float getPitchInternal(AmbienceData d) {
        if(hasForcedPitch) {
            return getForcedPitch();
        } else {
            float pitch;
            if(!hasCachedPitch)
                pitch = d.getPitch();
            else
                pitch = getCachedPitch();

            return pitch;
        }
    }

    public String getMusicString() { return instance.getSoundLocation().toString(); }
    public AmbienceInstance getMusicInstance() { return instance; }
    public IAmbienceSource getSource() { return source; }
    public AmbienceData getData() { return source.getData(); }

    public void setSource(IAmbienceSource source) {
        this.source = source;
        getMusicInstance().setBlockPos(source.getOrigin());
    }

    public boolean isMarkedForDeletion() {
        return markForDeletion;
    }

    public void markForDeletion() {
        this.markForDeletion = true;
    }

    public float getVolume() {
        return instance.getVolume();
    }
    public void setVolume(float volume) {
        instance.setVolume(volume);
    }
    public float getPitch() {
        return instance.getPitch();
    }
    public void setPitch(float pitch) {
        instance.setPitch(pitch);
    }

    public boolean hasCachedVolume() {
        return hasCachedVolume;
    }

    public float getCachedVolume() {
        return cachedVolume;
    }

    public void setCachedVolume(float cachedVolume) {
        this.cachedVolume = cachedVolume;
        hasCachedVolume = true;
    }

    public boolean hasCachedPitch() {
        return hasCachedPitch;
    }

    public float getCachedPitch() {
        return cachedPitch;
    }

    public void setCachedPitch(float cachedPitch) {
        this.cachedPitch = cachedPitch;
        hasCachedPitch = true;
    }

    public boolean hasForcedVolume() {
        return hasForcedVolume;
    }

    public void setHasForcedVolume(boolean hasForcedVolume) {
        this.hasForcedVolume = hasForcedVolume;
    }

    public float getForcedVolume() {
        return forcedVolume;
    }

    public void setForcedVolume(float forcedVolume) {
        this.forcedVolume = forcedVolume;
    }

    public boolean hasForcedPitch() {
        return hasForcedPitch;
    }

    public void setHasForcedPitch(boolean hasForcedPitch) {
        this.hasForcedPitch = hasForcedPitch;
    }

    public float getForcedPitch() {
        return forcedPitch;
    }

    public void setForcedPitch(float forcedPitch) {
        this.forcedPitch = forcedPitch;
    }

    public boolean isInMainLoop() {
        return AmbienceSoundState.LOOP.equals(stateSnd);
    }

    public boolean isNotFading() {
        return AmbienceFadeState.MAIN.equals(stateFade);
    }

    /*public CustomSoundSlot clone() {
        //todo fuck
        //return new CustomSoundSlot(this.musicName, this.musicRef, this.owner);
        return new CustomSoundSlot(this.musicName, this.owner);
    }*/

    @Override
    public String toString() {
        if(source instanceof AmbienceTileEntity)
            return instance.getSoundLocation().toString() + ", " + ParsingUtil.customBlockPosToString(((AmbienceTileEntity) source).getPos()) + ", volume " + getVolume() + ", pitch " + getPitch() + " (" + stateSnd.toString() + ", " + stateFade.toString() + ")";
        else
            return instance.getSoundLocation().toString() + ", volume " + getVolume() + ", pitch " + getPitch() + " (" + stateSnd.toString() + ", " + stateFade.toString() + ")";
    }

    private enum AmbienceSoundState {
        INTRO,
        LOOP,
        SINGLE,
        OUTRO
    }

    private enum AmbienceFadeState {
        FADE_IN,
        MAIN,
        FADE_OUT
    }
}
