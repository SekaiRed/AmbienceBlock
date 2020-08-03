package com.sekai.ambienceblocks.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDebug {
    public int data;

    public PacketDebug(int data) {
        System.out.println("constructor works : " + data);
        this.data = data;
    }

    public static PacketDebug decode(PacketBuffer buf) {
        int data = buf.readInt();
        System.out.println("decoding works : " + data);
        return new PacketDebug(data);
    }

    public static void encode(PacketDebug msg, PacketBuffer buf) {
        System.out.println("encoder works : " + msg.data);
        buf.writeInt(msg.data);
        System.out.println("after encoder");
    }

    public static void handle(final PacketDebug pkt, Supplier<NetworkEvent.Context> ctx) {
        /*ctx.get().enqueueWork(() -> {
            System.out.println("bruh" + pkt.data);
        });
        ctx.get().setPacketHandled(true);*/
        ctx.get().enqueueWork(() -> {
            System.out.println("handler works : " + pkt.data);
            System.out.println(ctx.get().getDirection());
        });
        ctx.get().setPacketHandled(true);
    }
}