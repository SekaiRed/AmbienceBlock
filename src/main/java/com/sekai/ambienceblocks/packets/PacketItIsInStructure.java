package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItIsInStructure {
    public String structure;
    public double range;
    public boolean full;
    public boolean result;

    public PacketItIsInStructure(String structure, double range, boolean full, boolean result) {
        this.structure = structure;
        this.range = range;
        this.full = full;
        this.result = result;
    }

    public static PacketItIsInStructure decode(PacketBuffer buf) {
        /*UUID source = buf.readUniqueId();
        UUID target = buf.readUniqueId();
        return new PacketTargeting(source, target);*/
        String source = buf.readString(30);
        double range = buf.readDouble();
        boolean full = buf.readBoolean();
        boolean result = buf.readBoolean();
        return new PacketItIsInStructure(source, range, full, result);
    }

    public static void encode(PacketItIsInStructure msg, PacketBuffer buf) {
        buf.writeString(msg.structure);
        buf.writeDouble(msg.range);
        buf.writeBoolean(msg.full);
        buf.writeBoolean(msg.result);
    }

    public static void handle(final PacketItIsInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketItIsInStructure(pkt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
