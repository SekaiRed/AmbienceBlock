package com.sekai.ambienceblocks.packets.ambiencedata;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ServerAmbienceDataHandler implements IMessageHandler<PacketAmbienceData, IMessage> {
    @Override
    public IMessage onMessage(final PacketAmbienceData msg, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER)
            return null;

        WorldServer serverWorld = ctx.getServerHandler().player.getServerWorld();
        serverWorld.addScheduledTask(() -> {
            //serverPlayer.inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND, amount));
            if(serverWorld == null)
                return;

            if(serverWorld.isRemote)
                return;

            if(!serverWorld.isBlockLoaded(msg.pos))
                return;

            TileEntity tile = serverWorld.getTileEntity(msg.pos);

            if (tile == null)
                return;

            if (!(tile instanceof AmbienceTileEntity))
                return;

            AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
            finalTile.data = msg.data;

            serverWorld.markBlockRangeForRenderUpdate(msg.pos, msg.pos);
            tile.markDirty();

            //sends an update back to update all clients within range
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(serverWorld.provider.getDimension(), msg.pos.getX() + 0.5D, msg.pos.getY() + 0.5D, msg.pos.getZ() + 0.5D, 512);
            PacketHandler.NETWORK.sendToAllAround(new PacketAmbienceData(msg.pos, msg.data), point);
            //PacketHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getPos(), finalTile.data));
        });

        return null;
    }
}
