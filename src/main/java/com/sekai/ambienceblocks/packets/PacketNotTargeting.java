package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketNotTargeting {
    public int source;

    public PacketNotTargeting(int source) {
        this.source = source;
    }

    public static PacketNotTargeting decode(FriendlyByteBuf buf) {
        return new PacketNotTargeting(buf.readInt());
    }

    public static void encode(PacketNotTargeting msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.source);
    }

    public static void handle(final PacketNotTargeting pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketNotTargeting(pkt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
