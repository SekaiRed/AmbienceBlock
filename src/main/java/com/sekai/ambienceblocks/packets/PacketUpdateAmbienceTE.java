package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.tileentity.AmbienceData;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.ClientPacketHandler;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketUpdateAmbienceTE {
    public BlockPos pos;
    public AmbienceData data;

    public PacketUpdateAmbienceTE(BlockPos pos, AmbienceData data) {
        this.pos = pos;
        this.data = data;
    }

    public static PacketUpdateAmbienceTE decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        AmbienceData data = new AmbienceData();
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
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketAmbienceTE(pkt));
            }

            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                //Overkill, if I use safeRunWhenOn I only have the dedicated server option and none of the loaded classes use OnlyIn
                //DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerPacketHandler.handlePacketAmbienceTE(pkt));
                //ServerPacketHandler.handlePacketAmbienceTE(pkt, ctx.get().getSender());
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
    }
}
