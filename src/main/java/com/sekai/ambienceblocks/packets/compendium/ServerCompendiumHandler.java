package com.sekai.ambienceblocks.packets.compendium;

import com.sekai.ambienceblocks.ambience.compendium.ServerCompendium;
import com.sekai.ambienceblocks.packets.ambiencedata.PacketAmbienceData;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ServerCompendiumHandler implements IMessageHandler<PacketCompendium, IMessage> {
    @Override
    public IMessage onMessage(final PacketCompendium msg, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER)
            return null;

        if(ServerCompendium.instance == null)
            return null;

        ServerCompendium.instance.clear();
        ServerCompendium.instance.addAllEntries(msg.entries);
        ServerCompendium.instance.updateAllCompendiums();

        return null;
    }
}
