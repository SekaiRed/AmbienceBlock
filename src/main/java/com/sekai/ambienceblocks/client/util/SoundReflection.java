package com.sekai.ambienceblocks.client.util;

import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class SoundReflection {
    //Reflection
    public static final Field SND_ENGINE_FIELD = ObfuscationReflectionHelper.findField(SoundManager.class, "f_120349_");
    public static final Field INSTANCE_TO_CHANNEL_FIELD = ObfuscationReflectionHelper.findField(SoundEngine.class, "f_120226_");

    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannelMap;

    @SuppressWarnings("unchecked")
    public SoundReflection(SoundManager handler) {
        try {
            SoundEngine sndEngine = (SoundEngine) SND_ENGINE_FIELD.get(handler); //sndEngine
            instanceToChannelMap = (Map<SoundInstance, ChannelAccess.ChannelHandle>) INSTANCE_TO_CHANNEL_FIELD.get(sndEngine); //instanceToChannel
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*public void fetchReflection() {
        try {
            //engineSoundStopTime = (Map<ISound, Integer>) playingSoundsStopTime.get(sndEngine);
            instanceToChannelMap = (Map<ISound, ChannelManager.Entry>) playingSoundsChannel.get(sndEngine);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/

    public boolean isPlaying(String sound) {
        for(SoundInstance iSound : instanceToChannelMap.keySet()) {
            if(ParsingUtil.validateString(iSound.getLocation().toString(), sound))
                return true;
        }
        return false;
    }
}
