package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.PacketAskCompendiumGui;
import com.sekai.ambienceblocks.packets.PacketCompendium;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register()
    {
        NET.registerMessage(0, PacketUpdateAmbienceTE.class, PacketUpdateAmbienceTE::encode, PacketUpdateAmbienceTE::decode, PacketUpdateAmbienceTE::handle);
        NET.registerMessage(1, PacketTargeting.class, PacketTargeting::encode, PacketTargeting::decode, PacketTargeting::handle);
        NET.registerMessage(2, PacketCompendium.class, PacketCompendium::encode, PacketCompendium::decode, PacketCompendium::handle);
        NET.registerMessage(3, PacketAskCompendiumGui.class, PacketAskCompendiumGui::encode, PacketAskCompendiumGui::decode, PacketAskCompendiumGui::handle);
    }
}
