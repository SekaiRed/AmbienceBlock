package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.ClientPacketHandler;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketUpdateAmbienceTE {
    public BlockPos pos;
    public AmbienceData data;

    public PacketUpdateAmbienceTE(BlockPos pos, AmbienceData data) {
        this.pos = pos;
        this.data = data;
    }

    public static PacketUpdateAmbienceTE decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        AmbienceData data = new AmbienceData();
        data.fromBuff(buf);
        return new PacketUpdateAmbienceTE(pos, data);
    }

    public static void encode(PacketUpdateAmbienceTE msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        msg.data.toBuff(buf);
    }

    public static void handle(final PacketUpdateAmbienceTE pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //System.out.println("Received " + ctx.get().getDirection() + " : " + pkt.data);
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketAmbienceTE(pkt));
            }

            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                //Overkill, if I use safeRunWhenOn I only have the dedicated server option and none of the loaded classes use OnlyIn
                //DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerPacketHandler.handlePacketAmbienceTE(pkt));
                //ServerPacketHandler.handlePacketAmbienceTE(pkt, ctx.get().getSender());
                ServerLevel level = ctx.get().getSender().getLevel();

                if(level == null)
                    return;

                if(level.isClientSide())
                    return;

                if(!level.isLoaded(pkt.pos))
                    return;

                BlockEntity tile = level.getBlockEntity(pkt.pos);

                if (tile == null)
                    return;

                if (!(tile instanceof AmbienceTileEntity))
                    return;

                AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                finalTile.data = pkt.data;
                //level.setBlocksDirty(pkt.pos, level.getBlockState(pkt.pos), level.getBlockState(pkt.pos));
                finalTile.setChanged();

                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getBlockPos(), finalTile.data));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
