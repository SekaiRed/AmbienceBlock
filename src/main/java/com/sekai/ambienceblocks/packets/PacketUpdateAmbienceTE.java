package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.Unused;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

//just an artifact of 1.16.5
@Unused(type = Unused.Type.REMOVE)
public class PacketUpdateAmbienceTE {
    /*public BlockPos pos;
    public AmbienceTileEntityData data;

    public PacketUpdateAmbienceTE(BlockPos pos, AmbienceTileEntityData data) {
        this.pos = pos;
        this.data = data;
    }

    public static PacketUpdateAmbienceTE decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        AmbienceTileEntityData data = new AmbienceTileEntityData();
        data.fromBuff(buf);
        return new PacketUpdateAmbienceTE(pos, data);
    }

    public static void encode(PacketUpdateAmbienceTE msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        msg.data.toBuff(buf);
    }

    public static void handle(final PacketUpdateAmbienceTE pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                Minecraft mc = Minecraft.getInstance();

                if (mc.world == null)
                    return;

                if (!mc.world.isRemote())
                    return;

                if (!mc.world.isBlockPresent(pkt.pos))
                    return;

                TileEntity tile = mc.world.getTileEntity(pkt.pos);

                if (tile == null)
                    return;

                if (!(tile instanceof AmbienceTileEntity))
                    return;

                AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                finalTile.data = pkt.data;
                AmbienceController.instance.stopFromTile(finalTile);
            }

            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                if(ctx.get().getSender().getServerWorld() == null)
                    return;

                if(ctx.get().getSender().getServerWorld().isRemote())
                    return;

                if(!ctx.get().getSender().getServerWorld().isBlockPresent(pkt.pos))
                    return;

                TileEntity tile = ctx.get().getSender().getServerWorld().getTileEntity(pkt.pos);

                if (tile == null)
                    return;

                if (!(tile instanceof AmbienceTileEntity))
                    return;

                AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                finalTile.data = pkt.data;

                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getPos(), finalTile.data));
            }
        });
        ctx.get().setPacketHandled(true);
    }*/
}
