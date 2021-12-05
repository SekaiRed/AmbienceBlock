package com.sekai.ambienceblocks.packets.sync.structure;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketItIsInStructure implements IMessage {
    public String structure;
    public double range;
    public boolean full;
    public boolean result;

    public PacketItIsInStructure() {}

    public PacketItIsInStructure(String structure, double range, boolean full, boolean result) {
        this.structure = structure;
        this.range = range;
        this.full = full;
        this.result = result;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        structure = pkt.readString(30);
        range = pkt.readDouble();
        full = pkt.readBoolean();
        result = pkt.readBoolean();
    }

    /*public static PacketItIsInStructure decode(PacketBuffer buf) {
        String source = buf.readString(30);
        double range = buf.readDouble();
        boolean full = buf.readBoolean();
        boolean result = buf.readBoolean();
        return new PacketItIsInStructure(source, range, full, result);
    }*/

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        pkt.writeString(structure);
        pkt.writeDouble(range);
        pkt.writeBoolean(full);
        pkt.writeBoolean(result);
    }

    /*public static void encode(PacketItIsInStructure msg, PacketBuffer buf) {
        buf.writeString(msg.structure);
        buf.writeDouble(msg.range);
        buf.writeBoolean(msg.full);
        buf.writeBoolean(msg.result);
    }*/

    /*public static void handle(final PacketItIsInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketItIsInStructure(pkt));
            }
        });
        ctx.get().setPacketHandled(true);
    }*/
}
