package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public static Minecraft mc;
    public static SoundHandler handler;
    private static final boolean debugMode = false;

    //System variables
    public int tick = 0;
    public AmbienceTileEntityData clipboard;

    //System lists
    private ArrayList<CustomSoundSlot> soundsList = new ArrayList<>();
    private ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        handler = Minecraft.getInstance().getSoundHandler();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        //there's no world, probably on the main menu or something
        if(mc.world == null || mc.player == null)
            return;

        //waits if the game is paused
        if(Minecraft.getInstance().isGamePaused())
            return;

        //abandon ship, no tile entities to work with
        if(mc.world.loadedTileEntityList == null)
            return;

        //check if a sound needs to be stopped and change volume and pitch
        for(CustomSoundSlot slot : soundsList) {
            if(!handler.isPlaying(slot.getMusicInstance()) || !mc.world.loadedTileEntityList.contains(slot.getOwner())) {
                stopMusic(slot, "music wasn't playing, the owner didn't exist anymore, the player isn't within it's bounds");
                continue;
            }

            //the tile is out of bounds
            if(!slot.getOwner().isWithinBounds(mc.player)) {
                //it could be fused
                if (slot.getOwner().data.shouldFuse()) {
                    List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                    ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !tile.getMusicName().equals(slot.getMusicString()) || !canTilePlay(tile));

                    if(ambienceTileEntityList.size() != 0) {
                        swapOwner(slot, ambienceTileEntityList.get(0));
                        continue;
                    }
                }

                stopMusic(slot, "not within bounds and couldn't find another block to fuse with");
                continue;
            }

            if(slot.getOwner().isUsingPriority())
            {
                int priority = getHighestPriorityByChannel(slot.getOwner().data.getChannel());
                if (slot.getOwner().getPriority() < priority) {
                    stopMusic(slot, "lower priority than the maximal one : slot priority " + slot.getOwner().getPriority() + " and max priority " + priority);
                    continue;
                }
            }

            if(slot.getOwner().data.needsRedstone() && !mc.world.isBlockPowered(slot.getOwner().getPos())) {
                stopMusic(slot, "needed redstone but wasn't powered");
                continue;
            }

            if(slot.getOwner().data.isUsingCondition()) {
                boolean condBool = false;
                List<AbstractCond> conditions = slot.getOwner().data.getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vec3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), slot.getOwner().getPos(), mc.world)) {
                        condBool = true;//continue;
                    }
                }
                if(condBool) {
                    stopMusic(slot, "conditions returned false");
                    continue;
                }
            }

            //volume stuff
            float volume = getVolumeFromTileEntity(slot.getOwner()), pitch = getPitchFromTileEntity(slot.getOwner());

            AmbienceTileEntityData data = slot.getOwner().data;

            if(slot.hasCachedVolume()) volume = data.isGlobal() ? slot.getCachedVolume() : (float) (slot.getCachedVolume() * data.getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos()));
            if(slot.hasCachedPitch()) pitch = slot.getCachedPitch();

            if(slot.getOwner().data.shouldFuse()) {
                volume = 0; pitch = 0; double totalBias = 0;
                List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                ambienceTileEntityList.removeIf(lambda -> !lambda.getMusicName().equals(slot.getMusicString()) || !canTilePlay(lambda));
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    volume += getVolumeFromTileEntity(tile);
                    totalBias += tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getPos());
                }
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    pitch += tile.data.getPitch() * (tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getPos()) / totalBias);
                }
            }
            slot.setVolume(volume);
            slot.setPitch(pitch);
        }

        delayList.removeIf(delay -> !mc.world.loadedTileEntityList.contains(delay.getOrigin()));

        List<AmbienceTileEntity> usefulTiles = getListOfLoadedAmbienceTiles();

        //delay stuff, should execute regardless of if we're within bounds or not
        for (AmbienceTileEntity tile : usefulTiles) {
            if (tileHasDelayRightNow(tile)) {
                AmbienceDelayRestriction delay = getDelayEntry(tile);
                if (!delay.isDone())
                    delay.tick();
                else
                    delay.restart();
            } else {
                if (tile.data.isUsingDelay()) {
                    delayList.add(new AmbienceDelayRestriction(tile, tile.getDelay()));
                }
            }
        }

        //getting all of the priorities by channel
        int[] maxPriorityByChannel = new int[AmbienceTileEntityData.maxChannels];
        for(int i = 0; i < AmbienceTileEntityData.maxChannels; i++) {
            maxPriorityByChannel[i] = 0;
            for (AmbienceTileEntity tile : usefulTiles) {
                if (tile.data.getChannel() == i && tile.getPriority() > maxPriorityByChannel[i] && tile.isUsingPriority() && canTilePlay(tile))
                    maxPriorityByChannel[i] = tile.getPriority();
            }
        }

        ArrayList<AmbienceTileEntity> ambienceTilesToPlay = new ArrayList<>();

        for(AmbienceTileEntity tile : usefulTiles)
        {
            //this tile cannot play
            if(!canTilePlay(tile))
                continue;

            /*if(tile.data.needsRedstone() && !mc.world.isBlockPowered(tile.getPos()))
                continue;*/

            //this tile is using priority and is of a high enough priority
            if(tile.isUsingPriority() && tile.data.getChannel() < AmbienceTileEntityData.maxChannels)
                if(tile.getPriority() < maxPriorityByChannel[tile.data.getChannel()])
                    continue;
                //ambienceTilesToPlay.add(tile);

            ambienceTilesToPlay.add(tile);
        }

        for (AmbienceTileEntity tile : ambienceTilesToPlay) {
            //this tile uses delay which is a special case
            if (tileHasDelayRightNow(tile) && tile.data.isUsingDelay()) {
                if(getDelayEntry(tile).isDone()) {
                    if(isTileEntityAlreadyPlaying(tile) == null){
                        playMusicNoRepeat(tile);
                        delayList.remove(getDelayEntry(tile));
                        continue;
                    }
                    else
                    {
                        if(tile.data.canPlayOverSelf()){
                            if(tile.data.shouldStopPrevious()) {
                                stopMusic(isTileEntityAlreadyPlaying(tile), "delay stopped it since it's playing again");
                            }
                            playMusicNoRepeat(tile);
                            delayList.remove(getDelayEntry(tile));
                            continue;
                        }
                    }
                }
                continue;
            }

            //that tile entity already owns a song in the list, check if it has the same song too
            if (isTileEntityAlreadyPlaying(tile) != null) {
                if (isTileEntityAlreadyPlaying(tile).getMusicString().equals(tile.getMusicName())) {
                    continue; //litterally the same tile, don't bother and skip it
                } else {
                    stopMusic(isTileEntityAlreadyPlaying(tile), "stopped because the song playing was different"); //stops the previous one since it has an outdated song
                    //playMusic(tile);
                    //continue;
                }
            }

            //if the music is already playing, check if you can't replace it with the new tile
            if (isMusicAlreadyPlaying(tile.getMusicName()) != null && canTilePlay(tile) && tile.data.shouldFuse()) {
                CustomSoundSlot slot = isMusicAlreadyPlaying(tile.getMusicName());
                if(slot != null) {
                    if(!(slot.getOwner().data.shouldFuse() && canTilePlay(slot.getOwner())))
                        continue;

                    double distOld = slot.getOwner().distanceTo(mc.player); //distance to already playing tile
                    double distNew = tile.distanceTo(mc.player); //distance to new tile we're iterating through

                    //the already playing one is closer, don't swap and remove the proposed tile from the queue
                    //(to avoid overlapping songs)
                    if (distNew >= distOld) {
                        //iter.remove();
                        continue;
                    }

                    //the new one is closer, we should swap the tile with the new one while still playing the music
                    if (distNew < distOld) {
                        //swap code here
                        swapOwner(slot, tile);
                        continue;
                    }
                }
            }

            playMusic(tile, "reached the end of tile to play");
        }

        soundsList.removeIf(CustomSoundSlot::isMarkedForDeletion);

        if(debugMode) {
            System.out.println("List of audio blocks going on");
            for (CustomSoundSlot slot : soundsList) {
                System.out.println(slot.toString());
                //System.out.println(slot.getMusicString() + ", " + slot.getOwner().getPos() + " volume " + slot.getMusicInstance().getVolume() + " pitch " + slot.getMusicInstance().getPitch());
            }
            System.out.println("and");
            System.out.println("List of audio delays going on");
            for (AmbienceDelayRestriction slot : delayList) {
                System.out.println(slot.getOrigin().getMusicName() + ", " + slot.getOrigin().getPos() + " with a tick of " + slot.tickLeft);
            }
            System.out.println("end");
        }
    }

    public void playMusic(AmbienceTileEntity tile, String source) {
        if(debugMode) {
            System.out.print("playing " + tile.getMusicName() + " at " + tile.getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        //System.out.println("playing " + tile.getMusicName() + " at " + tile.getPos());
        ResourceLocation playingResource = new ResourceLocation(tile.getMusicName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())), getVolumeFromTileEntity(tile),tile.data.getPitch(), tile.data.getFadeIn(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        soundsList.add(new CustomSoundSlot(tile.getMusicName(), playingMusic, tile));
    }

    public void playMusicNoRepeat(AmbienceTileEntity tile) {
        //System.out.println("playing non-looping " + tile.getMusicName() + " at " + tile.getPos());
        float volume = getVolumeFromTileEntity(tile), pitch = tile.data.getPitch();

        boolean usingRandomVolume = false;
        if(tile.data.isUsingRandomVolume()) usingRandomVolume = true;
        boolean usingRandomPitch = false;
        if(tile.data.isUsingRandomPitch()) usingRandomPitch = true;

        if(usingRandomVolume) volume = (float) (tile.data.getMinRandomVolume() + Math.random() * (tile.data.getMaxRandomVolume() - tile.data.getMinRandomVolume()));
        if(usingRandomPitch) pitch = (float) (tile.data.getMinRandomPitch() + Math.random() * (tile.data.getMaxRandomPitch() - tile.data.getMinRandomPitch()));

        ResourceLocation playingResource = new ResourceLocation(tile.getMusicName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())), volume, pitch, tile.data.getFadeIn(), false);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        CustomSoundSlot custom = new CustomSoundSlot(tile.getMusicName(), playingMusic, tile);
        soundsList.add(custom);
        if(usingRandomVolume) custom.setCachedVolume(volume);
        if(usingRandomPitch) custom.setCachedPitch(pitch);
    }

    public void stopMusic(CustomSoundSlot soundSlot, String source) {
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        if(soundSlot.getOwner().data.getFadeOut() != 0f && !soundSlot.getOwner().data.isUsingDelay())
            soundSlot.getMusicInstance().stop(soundSlot.getOwner().data.getFadeOut());
        else
            handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    public void stopMusicNoFadeOut(CustomSoundSlot soundSlot, String source) {
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(CustomSoundSlot soundSlot, AmbienceTileEntity tile) {
        if(debugMode) {
            System.out.println("swapping " + tile.getMusicName() + " from " + soundSlot.getOwner().getPos() + " to " + tile.getPos());
        }
        soundSlot.setOwner(tile);
        soundSlot.getMusicInstance().setBlockPos(tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())));
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, "stoppped from stopFromTile, updated by server?");
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);

        /*for(AmbienceDelayRestriction delay : delayList) {
            if(delay.getOrigin().equals(tile))
                fhf
        }*/
    }

    public boolean canTilePlay(AmbienceTileEntity tile) {
        /*System.out.println(tile.data.getSoundName() + " within bounds " + tile.isWithinBounds(mc.player));
        System.out.println(tile.data.getSoundName() + " needs redstone and is powered or none " + (tile.data.needsRedstone()?mc.world.isBlockPowered(tile.getPos()):true));
        return tile.isWithinBounds(mc.player) && (tile.data.needsRedstone()?mc.world.isBlockPowered(tile.getPos()):true);*/
        if(!tile.isWithinBounds(mc.player))
            return false;

        if(tile.data.needsRedstone()) {
            if (!mc.world.isBlockPowered(tile.getPos())) {
                return false;
            }
        }

        List<AbstractCond> conditions = tile.data.getConditions();
        for (AbstractCond condition : conditions) {
            if(!condition.isTrue(new Vec3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), tile.getPos(), mc.world))
                return false;
        }

        return true;
    }

    public List<AmbienceTileEntity> getListOfLoadedAmbienceTiles() {
        List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
        tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
        List<AmbienceTileEntity> ambienceTileEntityList = new ArrayList<>();
        for (TileEntity target : tileEntityList) {
            ambienceTileEntityList.add((AmbienceTileEntity) target);
        }
        return ambienceTileEntityList;
    }

    public int getHighestPriorityByChannel(int index) {
        /*int highest = 0;
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().getPriority() > highest && slot.getOwner().isUsingPriority() && !slot.isMarkedForDeletion())
                highest = slot.getOwner().getPriority();
        }
        return highest;*/

        /*int[] maxPriorityByChannel = new int[AmbienceTileEntityData.maxChannels];
        for(int i = 0; i < AmbienceTileEntityData.maxChannels; i++) {
            maxPriorityByChannel[i] = 0;
            for (AmbienceTileEntity tile : usefulTiles) {
                if (tile.data.getChannel() == i && tile.getPriority() > maxPriorityByChannel[i] && tile.isUsingPriority())
                    maxPriorityByChannel[i] = tile.getPriority();
            }
        }*/

        int highest = 0;
        for (CustomSoundSlot slot : soundsList) {
            if (slot.getOwner().isUsingPriority() && slot.getOwner().data.getChannel() == index && slot.getOwner().getPriority() > highest)
                highest = slot.getOwner().getPriority();
        }
        return highest;
    }

    public CustomSoundSlot isMusicAlreadyPlaying(String music) {
        for(CustomSoundSlot slot : soundsList) {
            if(music.equals(slot.getMusicString()) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public CustomSoundSlot isTileEntityAlreadyPlaying(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public float getVolumeFromTileEntity(AmbienceTileEntity tile) {
        if(tile.isGlobal())
            return tile.data.getVolume();
        else
            return (float) (tile.data.getVolume() * tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getPos()));
    }

    public float getPitchFromTileEntity(AmbienceTileEntity tile) {
        return tile.data.getPitch();
    }

    public AmbienceTileEntityData getClipboard() {
        return clipboard;
    }

    public void setClipboard(AmbienceTileEntityData clipboard) {
        this.clipboard = clipboard;
    }

    public class CustomSoundSlot {
        private String musicName;
        private AmbienceInstance musicRef;
        private AmbienceTileEntity owner;

        private boolean hasCachedVolume = false;
        private float cachedVolume = 0f;
        private boolean hasCachedPitch = false;
        private float cachedPitch = 0f;

        private boolean markForDeletion = false;

        public CustomSoundSlot(String musicName, AmbienceInstance musicRef, AmbienceTileEntity owner) {
            this.musicName = musicName;
            this.musicRef = musicRef;
            this.owner = owner;
        }

        public String getMusicString() { return musicName; }
        public AmbienceInstance getMusicInstance() { return musicRef; }
        public AmbienceTileEntity getOwner() { return owner; }

        public void setOwner(AmbienceTileEntity owner) {
            this.owner = owner;
            getMusicInstance().setBlockPos(owner.getPos());
        }

        public boolean isMarkedForDeletion() {
            return markForDeletion;
        }

        public void markForDeletion() {
            this.markForDeletion = true;
        }

        public float getVolume() {
            return musicRef.getVolume();
        }
        public void setVolume(float volume) {
            musicRef.setVolume(volume);
        }
        public float getPitch() {
            return musicRef.getPitch();
        }
        public void setPitch(float pitch) {
            musicRef.setPitch(pitch);
        }

        public boolean hasCachedVolume() {
            return hasCachedVolume;
        }

        public float getCachedVolume() {
            return cachedVolume;
        }

        public void setCachedVolume(float cachedVolume) {
            this.cachedVolume = cachedVolume;
            hasCachedVolume = true;
        }

        public boolean hasCachedPitch() {
            return hasCachedPitch;
        }

        public float getCachedPitch() {
            return cachedPitch;
        }

        public void setCachedPitch(float cachedPitch) {
            this.cachedPitch = cachedPitch;
            hasCachedPitch = true;
        }

        public CustomSoundSlot clone() {
            return new CustomSoundSlot(this.musicName, this.musicRef, this.owner);
        }

        @Override
        public String toString() {
            return getMusicString() + ", " +
            getOwner().getPos() + ", volume " + getVolume() + ", pitch " + getPitch() + ", priority " + getOwner().data.getPriority()
                    + ", channel " + getOwner().data.getChannel();
        }
    }

    public class AmbienceDelayRestriction {
        public AmbienceTileEntity getOrigin() {
            return origin;
        }

        private AmbienceTileEntity origin;
        private int tickLeft;

        private final int originalTick;

        public AmbienceDelayRestriction(AmbienceTileEntity origin, int tickLeft) {
            this.origin = origin;
            this.tickLeft = tickLeft;
            this.originalTick = tickLeft;
        }

        public void tick() {
            tickLeft--;
        }

        public boolean isDone() {
            return tickLeft <= 0;
        }

        public void restart() {
            tickLeft = originalTick;
        }
    }

    public AmbienceDelayRestriction getDelayEntry(AmbienceTileEntity origin) {
        for(AmbienceDelayRestriction restriction : delayList) {
            if(restriction.getOrigin() == origin) return restriction;
        }
        return null;
    }

    public boolean tileHasDelayRightNow(AmbienceTileEntity origin) {
        return getDelayEntry(origin) != null;
    }
}
