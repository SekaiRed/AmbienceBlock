package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register()
    {
        //NET_CHANNEL.registerMessage();
        //NET.registerMessage(0, PacketCapabilitiesWildCard.class, PacketCapabilitiesWildCard::encode, PacketCapabilitiesWildCard::decode, PacketCapabilitiesWildCard::handle);
        //NET.registerMessage(1, PacketDebug.class, PacketDebug::encode, PacketDebug::decode, PacketDebug::handle);
        //NET.registerMessage(0, PacketDebug.class, PacketDebug::encode, PacketDebug::decode, PacketDebug::handle);
        NET.registerMessage(0, PacketUpdateAmbienceTE.class, PacketUpdateAmbienceTE::encode, PacketUpdateAmbienceTE::decode, PacketUpdateAmbienceTE::handle);
        //NET.registerMessage(2, PacketOpenAmbienceGui.class, PacketOpenAmbienceGui::encode, PacketOpenAmbienceGui::decode, PacketOpenAmbienceGui::handle);
    }
}
