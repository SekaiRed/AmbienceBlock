package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.ambiencedata.ClientAmbienceDataHandler;
import com.sekai.ambienceblocks.packets.ambiencedata.PacketAmbienceData;
import com.sekai.ambienceblocks.packets.ambiencedata.ServerAmbienceDataHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static SimpleNetworkWrapper NETWORK;

    public static void registerPackets()
    {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
        NETWORK.registerMessage(ClientAmbienceDataHandler.class, PacketAmbienceData.class, 0, Side.CLIENT);
        NETWORK.registerMessage(ServerAmbienceDataHandler.class, PacketAmbienceData.class, 1, Side.SERVER);
    }
}
