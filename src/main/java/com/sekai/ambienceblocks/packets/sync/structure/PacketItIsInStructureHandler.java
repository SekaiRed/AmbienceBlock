package com.sekai.ambienceblocks.packets.sync.structure;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.packets.sync.target.targeting.PacketTargeting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketItIsInStructureHandler implements IMessageHandler<PacketItIsInStructure, IMessage> {
    @Override
    public IMessage onMessage(final PacketItIsInStructure msg, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT)
            return null;

        if(msg.result)
            AmbienceController.instance.structure.playerIsInStructure(msg.structure, msg.range, msg.full);
        else
            AmbienceController.instance.structure.playerIsntInStructure(msg.structure, msg.range, msg.full);

        return null;
    }
}
