package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.compendium.CompendiumEntry;
import com.sekai.ambienceblocks.compendium.ServerCompendium;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.ClientPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketCompendium {
    public List<CompendiumEntry> entries;

    public PacketCompendium(List<CompendiumEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public static PacketCompendium decode(PacketBuffer buf) {
        List<CompendiumEntry> entries = new ArrayList<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            AmbienceData data = new AmbienceData();
            data.fromBuff(buf);
            entries.add(new CompendiumEntry(data));
        }
        return new PacketCompendium(entries);
    }

    public static void encode(PacketCompendium msg, PacketBuffer buf) {
        buf.writeInt(msg.entries.size());
        for(int i = 0; i < msg.entries.size(); i++) {
            /*AmbienceData data = new AmbienceData();
            data.toBuff(buf);
            entries.add(new CompendiumEntry(data));*/
            msg.entries.get(i).getData().toBuff(buf);
        }
        //buf.writeInt(msg.source);
    }

    public static void handle(final PacketCompendium pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                //Used to protect from accessing Minecraft class
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketCompendium(pkt));
            }

            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                if(ServerCompendium.instance == null)
                    return;

                ServerCompendium.instance.clear();
                ServerCompendium.instance.addAllEntries(pkt.entries);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
