package com.sekai.ambienceblocks.ambience.sync.target;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.packets.PacketNotTargeting;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;

public class TargetSyncServer {
    private final HashMap<LivingEntity, Countdown> entityLastTargetCooldown = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if(event.world.isRemote && event.phase.equals(TickEvent.Phase.END))
            return;

        //only stops if the countdown reaches 0
        entityLastTargetCooldown.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    @SubscribeEvent
    public void onTargetingEvent(LivingSetAttackTargetEvent event) {
        //Config decided we should skip this
        if(!AmbienceConfig.shouldTrackBattles)
            return;

        if(event.getEntityLiving().world.isRemote)
            return;

        LivingEntity source = event.getEntityLiving();

        //System.out.println(event.getTarget() + "targeted by " + source);

        if(event.getTarget() instanceof ServerPlayerEntity) {
            //A player is targeted, notify their client if it's not on cooldown
            if(!entityLastTargetCooldown.containsKey(source)) {
                entityLastTargetCooldown.put(source, new Countdown(getTickTime()));
                PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getTarget()), new PacketTargeting(source.getEntityId()));
            }
        } else if(event.getTarget() == null) {
            entityLastTargetCooldown.remove(source);
            PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new PacketNotTargeting(source.getEntityId()));
        }
    }

    @SubscribeEvent
    public void serverClose(FMLServerStoppingEvent e) {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private int getTickTime() {
        //I guess this is a way to make it resets before the client countdown
        //Probably a bad idea, but I mean what other option do I have lol
        return AmbienceConfig.targetCountdownAmount / 2;
    }
}
