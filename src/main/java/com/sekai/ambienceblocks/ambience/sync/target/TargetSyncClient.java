package com.sekai.ambienceblocks.ambience.sync.target;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class TargetSyncClient {
    private final HashMap<Mob, Countdown> entityLastTargetCountdown = new HashMap<>();

    @SubscribeEvent
    public void tick() {
        //Only stops if the countdown reaches 0 or the entity is unloaded from the world (or killed)
        entityLastTargetCountdown.entrySet().removeIf(entry -> entry.getValue().tick() || isNotLoadedInWorld(entry.getKey()));
    }

    public void updateTargeting(Mob mob) {
        if(!entityLastTargetCountdown.containsKey(mob)) {
            //Doesn't have an entry yet, create it
            entityLastTargetCountdown.put(mob, new Countdown(getConfigTickTime()));
            entityLastTargetCountdown.get(mob).deactivate();
        } else {
            //Already exists, reset countdown
            entityLastTargetCountdown.get(mob).deactivate();
            entityLastTargetCountdown.get(mob).setTime(getConfigTickTime());
        }
    }

    public void updateStopTargeting(Mob mob) {
        if(entityLastTargetCountdown.containsKey(mob)) {
            //Exists, begin the countdown
            entityLastTargetCountdown.get(mob).activate();
            entityLastTargetCountdown.get(mob).setTime(getConfigTickTime());
        }
    }

    public int getEntityCountdown(String type) {
        int max = -1;
        for (Map.Entry<Mob, Countdown> entry : entityLastTargetCountdown.entrySet()) {
            if(isEntityTypeMatch(entry.getKey(), type)) {
                int time = entry.getValue().getTime();
                if(time > max)
                    max = time;
            }
        }
        return max;
    }

    private boolean isEntityTypeMatch(Mob mob, String type) {
        return ParsingUtil.validateString(mob.getType().getRegistryName().toString(), type);
    }

    private boolean isNotLoadedInWorld(Mob mob) {
        return Minecraft.getInstance().level.getEntity(mob.getId()) == null;
    }

    private int getConfigTickTime() {
        return AmbienceConfig.targetCountdownAmount;
    }
}
