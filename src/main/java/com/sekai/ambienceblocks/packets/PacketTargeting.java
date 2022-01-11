package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

//syncs server mobs targeting players
public class PacketTargeting {
    public int source;

    public PacketTargeting(int source) {
        this.source = source;
    }

    public static PacketTargeting decode(FriendlyByteBuf buf) {
        /*UUID source = buf.readUniqueId();
        UUID target = buf.readUniqueId();
        return new PacketTargeting(source, target);*/
        return new PacketTargeting(buf.readInt());
    }

    public static void encode(PacketTargeting msg, FriendlyByteBuf buf) {
        /*buf.writeUniqueId(msg.source);
        buf.writeUniqueId(msg.target);*/
        buf.writeInt(msg.source);
    }

    public static void handle(final PacketTargeting pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketTargeting(pkt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
