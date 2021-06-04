package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class AmbienceController {
    public static AmbienceController instance;
    public static Minecraft mc;
    public static SoundHandler handler;
    private static final boolean debugMode = false;

    //System lists
    private HashMap<AmbienceTileEntity, AmbienceInstance> sounds = new HashMap<>();
}
