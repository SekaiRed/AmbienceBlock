package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

//syncs server mobs targeting players
public class PacketTargeting {
    public int source;
    public boolean removeTarget = false;

    public PacketTargeting(int source) {
        this.source = source;
    }

    public PacketTargeting(int source, boolean removeTarget) {
        this.source = source;
        this.removeTarget = removeTarget;
    }

    public static PacketTargeting decode(PacketBuffer buf) {
        /*UUID source = buf.readUniqueId();
        UUID target = buf.readUniqueId();
        return new PacketTargeting(source, target);*/
        return new PacketTargeting(buf.readInt(), buf.readBoolean());
    }

    public static void encode(PacketTargeting msg, PacketBuffer buf) {
        /*buf.writeUniqueId(msg.source);
        buf.writeUniqueId(msg.target);*/
        buf.writeInt(msg.source);
        buf.writeBoolean(msg.removeTarget);
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
