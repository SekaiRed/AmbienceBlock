package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.ambience.compendium.ServerCompendium;
import com.sekai.ambienceblocks.ambience.sync.target.TargetSyncServer;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.init.ModTab;
import com.sekai.ambienceblocks.proxy.CommonProxy;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "ambienceblocks";
    public static final String NAME = "Ambience Blocks";
    public static final String VERSION = "1.3.1";
    public static final String CLIENT_PROXY_CLASS = "com.sekai.ambienceblocks.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.sekai.ambienceblocks.proxy.CommonProxy";

    private static final Logger LOGGER = LogManager.getLogger();

    public static final ModTab MYTAB = new ModTab();


    //public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @Mod.Instance
    public static Main instance;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
        //GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);
        //ModPackets.registerPackets();
        //MoveDatabase.init();
        GameRegistry.registerTileEntity(AmbienceTileEntity.class, new ResourceLocation(MODID, "ambience_block"));
        //PacketHandler.register();
        PacketHandler.registerPackets();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        proxy.registerModelVariants();
        proxy.init();

        //not sure if it works here
        //MinecraftForge.EVENT_BUS.register(new AmbienceController());
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public static void initClient(FMLInitializationEvent event)
    {
        //apparently this is a legal move
        MinecraftForge.EVENT_BUS.register(new AmbienceController());
    }

    /*@Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        //MinecraftForge.EVENT_BUS.register(new TargetSyncServer());

        //Register server compendium
        MinecraftForge.EVENT_BUS.register(new ServerCompendium(LOGGER));
        //FMLCommonHandler.instance().getMinecraftServerInstance()
        //System.out.println(((File) ObfuscationReflectionHelper.getPrivateValue(MinecraftServer.class, FMLCommonHandler.instance().getMinecraftServerInstance(), "field_71308_o")).getAbsolutePath());
        //System.out.println(FMLServerHandler.instance().getSavesDirectory());
    }*/

    @Mod.EventHandler
    public void worldLoad(FMLServerStartedEvent e) {
        MinecraftForge.EVENT_BUS.register(new ServerCompendium(LOGGER));
        ServerCompendium.instance.init();

        MinecraftForge.EVENT_BUS.register(new TargetSyncServer());
    }

    //@SubscribeEvent
    @Mod.EventHandler
    public void worldSave(FMLServerStoppingEvent e) {
        ServerCompendium.instance.end();
        MinecraftForge.EVENT_BUS.unregister(ServerCompendium.instance);
        ServerCompendium.instance = null;

        MinecraftForge.EVENT_BUS.unregister(TargetSyncServer.instance);
        TargetSyncServer.instance = null;
    }
}