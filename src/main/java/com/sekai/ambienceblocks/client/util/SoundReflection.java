package com.sekai.ambienceblocks.client.util;

import net.minecraft.client.audio.ChannelManager;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class SoundReflection {
    //Reflection
    public static final Field sndManager = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147694_f");
    //public static final Field playingSoundsStopTime = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_148624_n");
    public static final Field playingSoundsChannel = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_217942_m");
    /*public static final Field soundTicks = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_148618_g");
    public static final Field soundInit = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_148617_f");*/

    //Complex
    private SoundEngine sndEngine;
    //private Map<ISound, Integer> engineSoundStopTime;
    private Map<ISound, ChannelManager.Entry> engineSoundChannel;
    /*private int engineTick;
    private boolean engineInit;*/

    public SoundReflection(SoundHandler handler) {
        //sndManager.setAccessible(true);
        //playingSoundsStopTime.setAccessible(true);
        /*soundTicks.setAccessible(true);
        soundInit.setAccessible(true);*/
        try {
            sndEngine = (SoundEngine) sndManager.get(handler);
            //engineSoundStopTime = (Map<ISound, Integer>) playingSoundsStopTime.get(sndEngine);
            engineSoundChannel = (Map<ISound, ChannelManager.Entry>) playingSoundsChannel.get(sndEngine);
            /*engineTick = soundTicks.getInt(sndEngine);
            engineInit = soundInit.getBoolean(sndEngine);*/
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void fetchReflection() {
        try {
            //engineSoundStopTime = (Map<ISound, Integer>) playingSoundsStopTime.get(sndEngine);
            engineSoundChannel = (Map<ISound, ChannelManager.Entry>) playingSoundsChannel.get(sndEngine);
            /*engineTick = soundTicks.getInt(sndEngine);
            engineInit = soundInit.getBoolean(sndEngine);*/
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //TODO : now make a fix for intros
    /*public boolean isIntroOver(ISound sound) {
        fetchReflection();
        if(this.engineSoundStopTime.containsKey(sound))
            System.out.println(this.engineSoundStopTime.get(sound) + " at " + this.engineSoundStopTime.get(sound) + " <= " + this.engineTick);
        return this.engineSoundStopTime.containsKey(sound) && ((this.engineSoundStopTime.get(sound) - 18) <= this.engineTick);
    }*/

    public boolean isPlaying(String sound) {
        //return true;
        for(ISound iSound : engineSoundChannel.keySet()) {
            if(iSound.getSoundLocation().toString().contains(sound))
                return true;
        }
        return false;
    }
}
