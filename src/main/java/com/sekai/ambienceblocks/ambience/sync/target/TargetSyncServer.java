package com.sekai.ambienceblocks.ambience.sync.target;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.packets.PacketNotTargeting;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;

public class TargetSyncServer {
    private final HashMap<LivingEntity, Countdown> entityLastTargetCooldown = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if(event.world.isClientSide() && event.phase.equals(TickEvent.Phase.END))
            return;

        //only stops if the countdown reaches 0
        entityLastTargetCooldown.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    @SubscribeEvent
    public void onTargetingEvent(LivingSetAttackTargetEvent event) {
        //Config decided we should skip this
        if(!AmbienceConfig.shouldTrackBattles)
            return;

        if(event.getEntityLiving().level.isClientSide())
            return;

        LivingEntity source = event.getEntityLiving();

        //System.out.println(event.getTarget() + "targeted by " + source);

        if(event.getTarget() instanceof ServerPlayer) {
            //A player is targeted, notify their client if it's not on cooldown
            if(!entityLastTargetCooldown.containsKey(source)) {
                entityLastTargetCooldown.put(source, new Countdown(getTickTime()));
                PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getTarget()), new PacketTargeting(source.getId()));
            }
        } else if(event.getTarget() == null) {
            entityLastTargetCooldown.remove(source);
            PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new PacketNotTargeting(source.getId()));
        }
    }

    @SubscribeEvent
    public void serverClose(ServerStoppingEvent e) {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private int getTickTime() {
        //I guess this is a way to make it resets before the client countdown
        //Probably a bad idea, but I mean what other option do I have lol
        return AmbienceConfig.targetCountdownAmount / 2;
    }
}
