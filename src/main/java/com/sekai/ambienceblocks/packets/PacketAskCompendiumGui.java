package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketAskCompendiumGui {
    public PacketAskCompendiumGui() {}

    public static PacketAskCompendiumGui decode(FriendlyByteBuf buf) {
        return new PacketAskCompendiumGui();
    }

    public static void encode(PacketAskCompendiumGui msg, FriendlyByteBuf buf) {

    }

    public static void handle(final PacketAskCompendiumGui pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, ClientPacketHandler::handlePacketAskForCompendium);
            }

            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                //Please can I get the compendium GUI
                //PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()), new PacketCompendium(getAllEntries()));
                PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketAskCompendiumGui());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
