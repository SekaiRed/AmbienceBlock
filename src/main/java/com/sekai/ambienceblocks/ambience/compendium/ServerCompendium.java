package com.sekai.ambienceblocks.ambience.compendium;

import com.google.gson.JsonSyntaxException;
import com.sekai.ambienceblocks.packets.PacketCompendium;
import com.sekai.ambienceblocks.util.JsonUtil;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class ServerCompendium extends BaseCompendium {
    /*@SubscribeEvent
    public void worldLoad(WorldEvent.Load e) {
        System.out.println("load" + e.getWorld().isRemote());
    }*/
    //FMLServerStartedEvent
    private final Logger logger;

    private final static Field fieldStorage;
    static {
        fieldStorage = ObfuscationReflectionHelper.findField(MinecraftServer.class, "f_129744_");
        fieldStorage.setAccessible(true);
    }

    public static ServerCompendium instance;

    public ServerCompendium(Logger logger) {
        instance = this;
        this.logger = logger;
    }

    @SubscribeEvent
    public void worldLoad(ServerStartedEvent e) {
        //System.out.println("load");
        //simulateCompendiumLoad();

        try {
            LevelStorageSource.LevelStorageAccess storage = (LevelStorageSource.LevelStorageAccess) fieldStorage.get(e.getServer());
            init(storage.getWorldDir());
        } catch (IllegalAccessException illegalAccessException) {
            logger.error("Couldn't access the world's data.");
            illegalAccessException.printStackTrace();
        }

        //System.out.println(e.getServer().getDataDirectory().getAbsolutePath());
        //SaveFormat.LevelSave anvilConverterForAnvilFile

        /*File file2 = new File(file1, "data");
        file2.mkdirs();*/

        //((ServerWorld)e.getServer().getWorld(World.OVERWORLD)).getDa().getWorldDirectory();

        //PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getPos(), finalTile.data));
    }

    @SubscribeEvent
    public void worldSave(ServerStoppingEvent e) {
        //System.out.println("save");
        try {
            LevelStorageSource.LevelStorageAccess storage = (LevelStorageSource.LevelStorageAccess) fieldStorage.get(e.getServer());
            end(storage.getWorldDir());
        } catch (IllegalAccessException illegalAccessException) {
            //illegalAccessException.printStackTrace();
            logger.error("Couldn't access the world's data.", illegalAccessException);
        }
        instance = null;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        //System.out.println("player " + e.getPlayer().getName().getString() + " as joined the game and needs their compendium updated");
        PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getPlayer()), new PacketCompendium(getAllEntries()));
    }

    /*@SubscribeEvent
    public void worldSave(WorldEvent.Save e) {
        System.out.println("save" + e.getWorld().isRemote());
    }*/

    /*String output = JsonUtil.toJson(getData());
    AmbienceData data = JsonUtil.GSON.fromJson(output, AmbienceData.class);*/

    //called when the world begins
    public void init(Path savePath) {
        File ambienceFolder = new File(savePath.toFile(), "ambience");
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
            /*e.printStackTrace();
            System.exit(-1);*/
            logger.error("Failed IO.", e);
        } catch (JsonSyntaxException e) {
            logger.error("JSON file failed to parse.", e);
        }
    }

    //called when the world ends, write the entries saved to disk
    public void end(Path savePath) {
        File ambienceFolder = new File(savePath.toFile(), "ambience");
        if (!ambienceFolder.exists()){
            //Only create the folder if we have data to save, otherwise quit this method because there is nothing to save
            if(size() != 0)
                ambienceFolder.mkdir();
            else
                return;
        }

        File file = new File(ambienceFolder, "compendium.json");
        /*if (!file.exists()){
            file.mkdirs();
        }*/

        try{
            /*BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine()*/
            //FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            /*FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(JsonUtil.toJson(getAllEntries()));
            bw.close();*/

            FileUtils.writeStringToFile(file, JsonUtil.toJson(getAllEntries()), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            /*e.printStackTrace();
            System.exit(-1);*/
            logger.error("Failed IO.", e);
        }
    }

    public void updateAllCompendiums() {
        //Update all connected clients
        PacketHandler.NET.send(PacketDistributor.ALL.noArg(), new PacketCompendium(getAllEntries()));
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
