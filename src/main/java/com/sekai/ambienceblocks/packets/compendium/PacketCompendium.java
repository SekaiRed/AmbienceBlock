package com.sekai.ambienceblocks.packets.compendium;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.compendium.CompendiumEntry;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

public class PacketCompendium implements IMessage {
    public List<CompendiumEntry> entries = new ArrayList<>();

    //needed
    public PacketCompendium() {}

    public PacketCompendium(List<CompendiumEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        entries.clear();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            AmbienceData data = new AmbienceData();
            data.readBuff(pkt);
            entries.add(new CompendiumEntry(data));
        }
        /*pos = pkt.readBlockPos();
        data = new AmbienceData();
        data.readBuff(pkt);*/
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        int size = Math.min(entries.size(), AmbienceConfig.maxAmountOfCompendiumEntries);
        buf.writeInt(size);
        for(int i = 0; i < entries.size(); i++) {
            //We have surpassed the config limit, stop
            if(i == AmbienceConfig.maxAmountOfCompendiumEntries)
                return;

            entries.get(i).getData().writeBuff(pkt);
        }
        /*pkt.writeBlockPos(pos);
        data.writeBuff(pkt);*/
        /*pos = buf.read
        data.writeBuff(buf);*/
    }
}
