package com.sekai.ambienceblocks.client.util;

import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class SoundReflection {
    //Reflection
    public static final Field sndManagerField = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147694_f");
    public static final Field playingSoundsField = ObfuscationReflectionHelper.findField(SoundManager.class, "field_148629_h");
    /*public static final Field soundTicks = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_148618_g");
    public static final Field soundInit = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_148617_f");*/

    //Complex
    private SoundManager sndManager;
    //private Map<ISound, Integer> engineSoundStopTime;
    //private Map<ISound, ChannelManager.Entry> engineSoundChannel;
    Map<String, ISound> playingSounds;
    /*private int engineTick;
    private boolean engineInit;*/

    public SoundReflection(SoundHandler handler) {
        //sndManager.setAccessible(true);
        //playingSoundsStopTime.setAccessible(true);
        /*soundTicks.setAccessible(true);
        soundInit.setAccessible(true);*/
        try {
            sndManager = (SoundManager) sndManagerField.get(handler);
            //engineSoundStopTime = (Map<ISound, Integer>) playingSoundsStopTime.get(sndEngine);
            playingSounds = (Map<String, ISound>) playingSoundsField.get(sndManager);
            /*engineTick = soundTicks.getInt(sndEngine);
            engineInit = soundInit.getBoolean(sndEngine);*/
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying(String sound) {
        //return true;
        for(ISound iSound : playingSounds.values()) {
            //if(iSound.getSoundLocation().toString().contains(sound))
            if(ParsingUtil.validateString(iSound.getSoundLocation().toString(), sound))
                return true;
        }
        //playingSounds.values().forEach(System.out::println);
        return false;
    }
}
