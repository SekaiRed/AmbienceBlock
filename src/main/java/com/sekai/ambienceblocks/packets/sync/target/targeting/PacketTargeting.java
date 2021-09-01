package com.sekai.ambienceblocks.packets.sync.target.targeting;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTargeting implements IMessage {
    public int source;

    public PacketTargeting() {}

    public PacketTargeting(int source) {
        this.source = source;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        source = pkt.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        pkt.writeInt(source);
    }
}