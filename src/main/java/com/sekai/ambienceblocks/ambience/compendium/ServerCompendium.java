package com.sekai.ambienceblocks.ambience.compendium;

import com.google.gson.JsonSyntaxException;
import com.sekai.ambienceblocks.packets.compendium.PacketCompendium;
import com.sekai.ambienceblocks.util.JsonUtil;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class ServerCompendium extends BaseCompendium {
    private final Logger logger;

    /*private final static Field fieldStorage;
    static {
        fieldStorage = ObfuscationReflectionHelper.findField(MinecraftServer.class, "field_71310_m");
        fieldStorage.setAccessible(true);
    }*/

    public static ServerCompendium instance;

    public ServerCompendium(Logger logger) {
        instance = this;
        this.logger = logger;
    }

    //@SubscribeEvent
    /*@Mod.EventHandler
    public void worldLoad(FMLServerStartedEvent e) {
        //System.out.println("load");
        //simulateCompendiumLoad();

        //e.getModState()
        //SaveFormat.LevelSave storage = (SaveFormat.LevelSave) fieldStorage.get(e.getServer());
        //init(storage.getWorldDir());
        System.out.println("FUCKFUCKFUCK");
        init(getWorldDir());

        //System.out.println(e.getServer().getDataDirectory().getAbsolutePath());
        //SaveFormat.LevelSave anvilConverterForAnvilFile

        //((ServerWorld)e.getServer().getWorld(World.OVERWORLD)).getDa().getWorldDirectory();

        //PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getPos(), finalTile.data));
    }

    //@SubscribeEvent
    @Mod.EventHandler
    public void worldSave(FMLServerStoppingEvent e) {
        end(getWorldDir());
        MinecraftForge.EVENT_BUS.unregister(this);
    }*/

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        //System.out.println("player " + e.getPlayer().getName().getString() + " as joined the game and needs their compendium updated");
        //PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()), new PacketCompendium(getAllEntries()));
        PacketHandler.NETWORK.sendTo(new PacketCompendium(getAllEntries()), (EntityPlayerMP) e.player);
    }

    //called when the world begins
    public void init() {
        logger.info("Init was called");
        Path path = getWorldDir();
        File ambienceFolder = new File(path.toFile(), "ambience");
        if (!ambienceFolder.exists()){
            //The folder doesn't exist, I don't need to load anything
            return;
            //ambienceFolder.mkdir();
        }

        File file = new File(ambienceFolder, "compendium.json");

        try{
            if(file.exists()) {
                String jsonInput = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                CompendiumEntry[] compendiumArray = JsonUtil.GSON.fromJson(jsonInput, CompendiumEntry[].class);
                addAllEntries(Arrays.asList(compendiumArray));
            }
        }
        catch (IOException e){
            logger.error("Failed IO.", e);
        } catch (JsonSyntaxException e) {
            logger.error("JSON file failed to parse.", e);
        }
    }

    //called when the world ends, write the entries saved to disk
    public void end() {
        logger.info("End was called");
        Path path = getWorldDir();
        File ambienceFolder = new File(path.toFile(), "ambience");
        if (!ambienceFolder.exists()){
            //Only create the folder if we have data to save, otherwise quit this method because there is nothing to save
            if(size() != 0)
                ambienceFolder.mkdir();
            else
                return;
        }

        File file = new File(ambienceFolder, "compendium.json");

        try{
            FileUtils.writeStringToFile(file, JsonUtil.toJson(getAllEntries()), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            logger.error("Failed IO.", e);
        }
    }

    public void updateAllCompendiums() {
        //Update all connected clients
        //PacketHandler.NET.send(PacketDistributor.ALL.noArg(), new PacketCompendium(getAllEntries()));
        PacketHandler.NETWORK.sendToAll(new PacketCompendium(getAllEntries()));
    }

    public Path getWorldDir() {
        //File worldDir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "field_"+"75770_b");

        //File worldDir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "field_"+"75770_b");field_71308_o

        if(FMLCommonHandler.instance().getMinecraftServerInstance().worlds.length == 0)
            return null;

        return FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getChunkSaveLocation().toPath();

        /*try {
            if(FMLCommonHandler.instance().getMinecraftServerInstance().worlds.length == 0)
                throw new FileNotFoundException();

            return FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getChunkSaveLocation().toPath();
        } catch (FileNotFoundException e) {
            logger.error("There is no world to get the save file from");
        }
        return null;*/
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("ServerCompendium");
        string.append("{");
        int i = 1;
        for(CompendiumEntry entry : getAllEntries()) {
            string.append(entry.getData().getSoundName());
            i++;
            if(i == getAllEntries().size())
                string.append(", ");
        }
        string.append("}");
        return string.toString();
    }
}
