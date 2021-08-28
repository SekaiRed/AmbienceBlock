package com.sekai.ambienceblocks.packets.ambiencedata;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ClientAmbienceDataHandler implements IMessageHandler<PacketAmbienceData, IMessage> {
    @Override
    public IMessage onMessage(final PacketAmbienceData msg, final MessageContext ctx)
    {
        if(ctx.side != Side.CLIENT)
            return null;

        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(() -> {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.world == null)
                return;

            if (!mc.world.isBlockLoaded(msg.pos))
                return;

            TileEntity tile = mc.world.getTileEntity(msg.pos);

            if (tile == null)
                return;

            if (!(tile instanceof AmbienceTileEntity))
                return;

            AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
            finalTile.data = msg.data;
            AmbienceController.instance.stopFromTile(finalTile);
        });

        return null;
    }
}