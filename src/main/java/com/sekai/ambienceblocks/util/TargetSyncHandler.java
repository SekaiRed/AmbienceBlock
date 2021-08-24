package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;

//This is responsible for keeping the targeted entities in sync with clients, hopefully the timer reduces the impact on the network
//Especially since this is enabled by default regardless of whether or not a battle is being tracked
//I could make this feature toggleable in configs if you don't plan to use it at all, but I'm scared of 1.12.2 configs
//TODO maybe split PacketTargeting into PacketStartTargeting and PacketStopTargeting to only pass around one int, maybe that's micro optimization but it's something right?
public class TargetSyncHandler {
    //TODO Could also be a config value?
    //Automatic wait time between packets (Note : StartTargeting and StopTargeting are independent from each other and will reset the other's timer when triggered)
    private static final int maxTick = 1000;

    private final HashMap<LivingEntity, EntityCountdown> entityLastTargetCountdown = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if(event.world.isRemote && event.phase.equals(TickEvent.Phase.END))
            return;

        //only stops if the countdown reaches 0
        entityLastTargetCountdown.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    @SubscribeEvent
    public void onTargetingEvent(LivingSetAttackTargetEvent event) {
        //Config decided we should skip this
        if(!AmbienceConfig.shouldTrackBattles)
            return;

        if(event.getEntityLiving().world.isRemote)
            return;

        LivingEntity source = event.getEntityLiving();

        if(event.getTarget() instanceof ServerPlayerEntity) {
            if(!entityLastTargetCountdown.containsKey(source) || entityLastTargetCountdown.get(source).removeTarget) {
                entityLastTargetCountdown.put(source, new EntityCountdown(maxTick, false));
                PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getTarget()), new PacketTargeting(event.getEntityLiving().getEntityId()));
            }
        }

        //The mob is no longer targeting the player, notify the clients that are looking at this entity
        if(event.getTarget() == null) {
            if(!entityLastTargetCountdown.containsKey(source) || !entityLastTargetCountdown.get(source).removeTarget) {
                entityLastTargetCountdown.put(source, new EntityCountdown(maxTick, true));
                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new PacketTargeting(event.getEntityLiving().getEntityId()));
            }
        }
    }

    private static class EntityCountdown {
        int countdown;
        boolean removeTarget;

        public EntityCountdown(int countdown, boolean removeTarget) {
            this.countdown = countdown;
            this.removeTarget = removeTarget;
        }

        public boolean tick() {
            countdown--;
            return countdown <= 0;
        }
    }
}
