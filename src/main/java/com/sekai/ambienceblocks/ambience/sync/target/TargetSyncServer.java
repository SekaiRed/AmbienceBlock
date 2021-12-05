package com.sekai.ambienceblocks.ambience.sync.target;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.packets.sync.target.nottargeting.PacketNotTargeting;
import com.sekai.ambienceblocks.packets.sync.target.targeting.PacketTargeting;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class TargetSyncServer {
    public static TargetSyncServer instance;

    private final HashMap<EntityLivingBase, Countdown> entityLastTargetCooldown = new HashMap<>();

    public TargetSyncServer() {
        instance = this;
    }

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

        EntityLivingBase source = event.getEntityLiving();

        //System.out.println(event.getTarget() + "targeted by " + source);

        if(event.getTarget() instanceof EntityPlayerMP) {
            //A player is targeted, notify their client if it's not on cooldown
            if(!entityLastTargetCooldown.containsKey(source)) {
                entityLastTargetCooldown.put(source, new Countdown(getTickTime()));
                //PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getTarget()), new PacketTargeting(source.getEntityId()));
                PacketHandler.NETWORK.sendTo(new PacketTargeting(source.getEntityId()), (EntityPlayerMP) event.getTarget());
            }
        } else if(event.getTarget() == null) {
            entityLastTargetCooldown.remove(source);
            //PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new PacketNotTargeting(source.getEntityId()));
            PacketHandler.NETWORK.sendToAllTracking(new PacketNotTargeting(source.getEntityId()), event.getEntityLiving());
        }
    }

    /*@SubscribeEvent
    public void serverClose(FMLServerStoppingEvent e) {
        MinecraftForge.EVENT_BUS.unregister(this);
    }*/

    private int getTickTime() {
        //I guess this is a way to make it resets before the client countdown
        //Probably a bad idea, but I mean what other option do I have lol
        //TODO Idea for a fix, when the timer ends it wait before being removed and send a packet to
        // ask the server what the status is, if the player is still targeted the timer resets
        // otherwise it just stops.
        return AmbienceConfig.targetCountdownAmount / 2;
    }
}
