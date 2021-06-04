package com.sekai.ambienceblocks.client.ambience;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class AmbienceInstance {
    private AmbienceSound sndIntro;
    private AmbienceSound sndMain;
    private AmbienceSound sndOutro;

    private AmbienceSoundState stateSnd;
    private AmbienceFadeState stateFade;

    public AmbienceInstance(ResourceLocation soundId, SoundCategory categoryIn, BlockPos pos, float volume, float pitch, int fadeIn, boolean repeat) {
        //sndMain =
    }

    class AmbienceSound extends TickableSound {
        public AmbienceSound(SoundEvent soundIn, SoundCategory categoryIn) {
            super(soundIn, categoryIn);
        }

        @Override
        public void tick() {

        }
    }

    private enum AmbienceSoundState {
        INTRO,
        LOOP,
        OUTRO
    }

    private enum AmbienceFadeState {
        FADE_IN,
        MAIN,
        FADE_OUT
    }
}
