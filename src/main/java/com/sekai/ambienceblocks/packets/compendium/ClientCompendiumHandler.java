package com.sekai.ambienceblocks.packets.compendium;

import com.sekai.ambienceblocks.ambience.compendium.ServerCompendium;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ClientCompendiumHandler implements IMessageHandler<PacketCompendium, IMessage> {
    @Override
    public IMessage onMessage(final PacketCompendium msg, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT)
            return null;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null)
            return null;

        if (!mc.world.isRemote)
            return null;

        AmbienceController.instance.compendium.clear();
        AmbienceController.instance.compendium.addAllEntries(msg.entries);

        return null;
    }
}