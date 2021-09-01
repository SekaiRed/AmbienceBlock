package com.sekai.ambienceblocks.packets.sync.target.nottargeting;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.packets.sync.target.targeting.PacketTargeting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketNotTargetingHandler implements IMessageHandler<PacketNotTargeting, IMessage> {
    @Override
    public IMessage onMessage(final PacketNotTargeting msg, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT)
            return null;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null)
            return null;

        //I know this is overkill but I'm scared
        if (!mc.world.isRemote)
            return null;

        Entity source = mc.world.getEntityByID(msg.source);

        if (source == null)
            return null;

        if(source instanceof EntityMob) {
            EntityMob mob = (EntityMob) source;

            AmbienceController.instance.target.updateStopTargeting(mob);
        }

        return null;
    }
}