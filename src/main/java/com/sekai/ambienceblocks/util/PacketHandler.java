package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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
        NET.registerMessage(2, PacketNotTargeting.class, PacketNotTargeting::encode, PacketNotTargeting::decode, PacketNotTargeting::handle);
        NET.registerMessage(3, PacketCompendium.class, PacketCompendium::encode, PacketCompendium::decode, PacketCompendium::handle);
        NET.registerMessage(4, PacketAskCompendiumGui.class, PacketAskCompendiumGui::encode, PacketAskCompendiumGui::decode, PacketAskCompendiumGui::handle);
        NET.registerMessage(5, PacketIsItInStructure.class, PacketIsItInStructure::encode, PacketIsItInStructure::decode, PacketIsItInStructure::handle);
        NET.registerMessage(6, PacketItIsInStructure.class, PacketItIsInStructure::encode, PacketItIsInStructure::decode, PacketItIsInStructure::handle);
    }
}
