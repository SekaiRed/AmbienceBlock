package com.sekai.ambienceblocks.ambience.sync.target;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class TargetSyncClient {
    private final HashMap<EntityMob, Countdown> entityLastTargetCountdown = new HashMap<>();

    @SubscribeEvent
    public void tick() {
        //Only stops if the countdown reaches 0 or the entity is unloaded from the world (or killed)
        entityLastTargetCountdown.entrySet().removeIf(entry -> entry.getValue().tick() || isNotLoadedInWorld(entry.getKey()));
    }

    public void updateTargeting(EntityMob mob) {
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

    public void updateStopTargeting(EntityMob mob) {
        if(entityLastTargetCountdown.containsKey(mob)) {
            //Exists, begin the countdown
            entityLastTargetCountdown.get(mob).activate();
            entityLastTargetCountdown.get(mob).setTime(getConfigTickTime());
        }
    }

    public int getEntityCountdown(String type) {
        int max = -1;
        for (Map.Entry<EntityMob, Countdown> entry : entityLastTargetCountdown.entrySet()) {
            if(isEntityTypeMatch(entry.getKey(), type)) {
                int time = entry.getValue().getTime();
                if(time > max)
                    max = time;
            }
        }
        return max;
    }

    private boolean isEntityTypeMatch(EntityMob mob, String type) {
        //ForgeRegistries.ENTITIES.getE()
        //EntityRegistry.getEntry(mob.getClass()).getName()
        return ParsingUtil.validateString(ParsingUtil.getEntityRegistryName(mob), type);
    }

    /*private String getEntityRegistryName(Entity mob) {
        return EntityRegistry.getEntry(mob.getClass()).getRegistryName().toString();
    }*/

    private boolean isNotLoadedInWorld(EntityMob mob) {
        return Minecraft.getMinecraft().world.getEntityByID(mob.getEntityId()) == null;
    }

    private int getConfigTickTime() {
        return AmbienceConfig.targetCountdownAmount;
    }
}
