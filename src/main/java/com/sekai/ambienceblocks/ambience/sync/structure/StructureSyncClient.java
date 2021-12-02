package com.sekai.ambienceblocks.ambience.sync.structure;

import com.sekai.ambienceblocks.ambience.sync.Countdown;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.packets.PacketIsItInStructure;
import com.sekai.ambienceblocks.util.PacketHandler;

import java.util.HashMap;

public class StructureSyncClient {
    public static StructureSyncClient instance;

    private final HashMap<String, Countdown> sentRequest = new HashMap<>(); // To not spam the server
    private final HashMap<String, Countdown> isInStructureTimer = new HashMap<>();

    public StructureSyncClient() {
        instance = this;
    }

    public void tick() {
        //Only stops if the countdown reaches 0 or the entity is unloaded from the world (or killed)
        isInStructureTimer.entrySet().removeIf(entry -> entry.getValue().tick());
        sentRequest.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    public boolean isPlayerInStructure(String structure) {
        sendRequestForStructureInfo(structure);
        if(isInStructureTimer.containsKey(structure)) {
            return true; // I have a corresponding key, the player is in the structure
        } else {
            //sendRequestForStructureInfo(structure);
            return false;
        }
        //return false;
    }

    private void sendRequestForStructureInfo(String structure) {
        if(!sentRequest.containsKey(structure)) {
            sentRequest.put(structure, new Countdown(getRequestSentTickTime()));
            PacketHandler.NET.sendToServer(new PacketIsItInStructure(structure));
        }
    }

    public void playerIsInStructure(String structure) {
        if(isInStructureTimer.containsKey(structure))
            isInStructureTimer.get(structure).setTime(getRequestApprovedTickTime());
        else
            isInStructureTimer.put(structure, new Countdown(getRequestApprovedTickTime()));
    }

    public void playerIsntInStructure(String structure) {
        isInStructureTimer.remove(structure);
    }

    private int getRequestSentTickTime() {
        return AmbienceConfig.structureCountdownAmount;
    }

    private int getRequestApprovedTickTime() {
        return AmbienceConfig.structureCountdownAmount * 2;
    }

    /*public boolean isInStructure(String structure) {
        List<Structure<?>> structures = getMatchingStructures(structure);
        for(Structure<?> struct : structures) {
            if(isInStructure(struct))
                return true;
        }
        return false;
        //return isInStructure()
    }

    //TODO Maybe don't use real structure reference, it's useless on the client anyway since you only check on the server
    // instead just look for a string

    private boolean isInStructure(Structure<?> struct) {
        if(isInStructureTimer.containsKey(struct)) {
            return true; // I have a corresponding key, the player is in the structure
        } else {
            if(!sentRequest.containsKey(struct))
                sendRequest(); //!!!!
            return false;
        }
        //return false;
    }

    private List<Structure<?>> getMatchingStructures(String structure) {
        List<Structure<?>> structureList = new ArrayList<>();
        for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
            if(structureFeature.getRegistryName().toString().contains(structure))
                structureList.add(structureFeature);
        }
        return structureList;
    }*/
}
