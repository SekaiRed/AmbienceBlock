package com.sekai.ambienceblocks;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.init.ModTab;
import com.sekai.ambienceblocks.proxy.CommonProxy;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "ambienceblocks";
    public static final String NAME = "Ambience Blocks";
    public static final String VERSION = "1.2.1";
    public static final String CLIENT_PROXY_CLASS = "com.sekai.ambienceblocks.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.sekai.ambienceblocks.proxy.CommonProxy";

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

    @Mod.EventHandler
    public static void Postinit(FMLPostInitializationEvent event)
    {
        //MinecraftForge.EVENT_BUS.register(new GuiOverlay());
    }
}