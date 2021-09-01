package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.ambiencedata.ClientAmbienceDataHandler;
import com.sekai.ambienceblocks.packets.ambiencedata.PacketAmbienceData;
import com.sekai.ambienceblocks.packets.ambiencedata.ServerAmbienceDataHandler;
import com.sekai.ambienceblocks.packets.compendium.ClientCompendiumHandler;
import com.sekai.ambienceblocks.packets.compendium.PacketCompendium;
import com.sekai.ambienceblocks.packets.compendium.ServerCompendiumHandler;
import com.sekai.ambienceblocks.packets.sync.target.nottargeting.PacketNotTargeting;
import com.sekai.ambienceblocks.packets.sync.target.nottargeting.PacketNotTargetingHandler;
import com.sekai.ambienceblocks.packets.sync.target.targeting.PacketTargeting;
import com.sekai.ambienceblocks.packets.sync.target.targeting.PacketTargetingHandler;
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

        NETWORK.registerMessage(ClientCompendiumHandler.class, PacketCompendium.class, 2, Side.CLIENT);
        NETWORK.registerMessage(ServerCompendiumHandler.class, PacketCompendium.class, 3, Side.SERVER);

        NETWORK.registerMessage(PacketTargetingHandler.class, PacketTargeting.class, 4, Side.CLIENT);
        NETWORK.registerMessage(PacketNotTargetingHandler.class, PacketNotTargeting.class, 5, Side.CLIENT);
    }
}
