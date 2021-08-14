package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.sekai.ambienceblocks.client.rendering.RenderingEventHandler;
import com.sekai.ambienceblocks.tileentity.AmbienceData;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.world.CompendiumRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public final Minecraft mc;
    public final SoundHandler handler;
    public final CompendiumRegistry compendium;
    public static boolean debugMode = false;

    //System variables
    public int tick = 0;
    public AmbienceData clipboard;

    //System lists
    public ArrayList<AmbienceSlot> soundsList = new ArrayList<>();
    public ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        handler = Minecraft.getInstance().getSoundHandler();
        compendium = new CompendiumRegistry();
    }

    @SubscribeEvent
    public void soundReload(SoundLoadEvent e) {
        clear();
    }

    @SubscribeEvent
    public void joinWorld(ClientPlayerNetworkEvent.LoggedInEvent e) {
        clear();
    }

    @SubscribeEvent
    public void leaveWorld(ClientPlayerNetworkEvent.LoggedOutEvent e) {
        clear();
    }

    //clears everything ambience related
    //just to be safe
    private void clear() {
        soundsList.forEach(AmbienceSlot::forceStop);
        soundsList.clear();
        delayList.clear();

        //AmbienceCompendiumRegistry.instance.entries.clear();
        compendium.clear();

        RenderingEventHandler.clearEvent();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        //there's no world, probably on the main menu or something
        if(mc.world == null || mc.player == null || e.phase.equals(TickEvent.Phase.END))
            return;

        //waits if the game is paused
        if(Minecraft.getInstance().isGamePaused())
            return;

        //abandon ship, no tile entities to work with
        if(mc.world.loadedTileEntityList == null)
            return;

        //render the invisible ambience block particles client side
        renderInvisibleTileEntityParticles();

        //get all of the loaded ambienceTileEntities
        //List<AmbienceTileEntity> ambienceTiles = getListOfLoadedAmbienceTiles();
        List<IAmbienceSource> ambienceSources = getListOfAmbienceSources();

        //iterate through all ambienceslots
        ambienceIteration: for(AmbienceSlot slot : soundsList) {
            //update volume, pitch, intro/outro, fade in/out first
            slot.tick();

            if(slot.getOwner() instanceof AmbienceTileEntity && !mc.world.loadedTileEntityList.contains(slot.getOwner())) {
                stopMusic(slot, "the owner doesn't exist");
                continue;
            }

            //the tile is out of bounds
            if(!slot.getOwner().isWithinBounds(mc.player)) {
                //it could be fused with another better candidate
                //it does this by checking if there's a valid fusing target loaded right now and
                //it just grabs the first available candidate and swaps with it instead of stopping the sound
                if (slot.getData().shouldFuse()) {
                    List<IAmbienceSource> ambienceTileEntityList = new ArrayList<>(ambienceSources);
                    //ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !tile.data.getSoundName().equals(slot.getMusicString()) || !canTilePlay(tile));
                    //ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(tile.data, slot.getOwner().data) || !canTilePlay(tile));
                    ambienceTileEntityList.removeIf(tile -> !tile.getData().shouldFuse() || !hasSameMusics(tile.getData(), slot.getData()) || !canTilePlay(tile));

                    if(ambienceTileEntityList.size() != 0) {
                        swapOwner(slot, ambienceTileEntityList.get(0));
                        continue;
                    }
                }

                stopMusic(slot, "not within bounds and couldn't find another block to fuse with");
                continue;
            }

            //does it have a lower priority to an already playing ambience?
            if(slot.getData().isUsingPriority())
            {
                int priority = getHighestPriorityByChannel(slot.getData().getChannel());
                if (slot.getData().getPriority() < priority) {
                    stopMusic(slot, "lower priority than the maximal one : slot priority " + slot.getData().getPriority() + " and max priority " + priority);
                    continue;
                }
            }

            //are its conditions still fulfilled?
            if(slot.getData().isUsingCondition()) {
                List<AbstractCond> conditions = slot.getData().getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), mc.world, slot.getOwner())) {
                        //if a single condition is false let's end this early
                        stopMusic(slot, "conditions returned false");
                        continue ambienceIteration;
                    }
                }
            }

            //fusing volume and pitch shenanigans
            //todo priority is broken with this system, maybe i could cache a priority value based on the collective one of all fuse blocks?
            if(slot.getOwner().getData().shouldFuse()) {
                float volume = 0;
                float pitch = 0;
                double totalBias = 0;
                //List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                //tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                //only keep tiles that can fuse, possess the same music and is able to play right now
                ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(slot.getOwner().getData(), tile.data) || !canTilePlay(tile));
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    volume += getVolumeFromTileEntity(tile);
                    totalBias += tile.getPercentageHowCloseIsPlayer(mc.player);
                }
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    pitch += tile.data.getPitch() * (tile.getPercentageHowCloseIsPlayer(mc.player) / totalBias);
                }

                //bruteforce a custom volume/pitch in there
                slot.setHasForcedVolume(true);
                slot.setHasForcedPitch(true);
                slot.setForcedVolume(volume);
                slot.setForcedPitch(pitch);
            }

            //update fade/intro/outro tick
            //slot.tick();
            //forces to update the volume and pitch
            slot.forceVolumeAndPitchUpdate();

            //reset after applying it or it'll stay forced afterwards, not ideal but can't think of a better way lol
            slot.setHasForcedVolume(false);
            slot.setHasForcedPitch(false);
        }

        //no use counting down the delay of an unloaded tile
        //TODO SUS call to list.contain
        delayList.removeIf(delay -> (delay.getOrigin() instanceof AmbienceTileEntity) && !mc.world.loadedTileEntityList.contains(delay.getOrigin()));

        for (IAmbienceSource source : ambienceSources) {
            if (tileHasDelayRightNow(source)) {
                AmbienceDelayRestriction delay = getDelayEntry(source);
                if (!delay.isDone())
                    delay.tick();
                else
                    delay.restart();
            } else {
                if (source.getData().isUsingDelay()) {
                    delayList.add(new AmbienceDelayRestriction(source, source.getData().getDelay()));
                }
            }
        }

        //getting all of the max priorities by channel
        int[] maxPriorityByChannel = new int[AmbienceData.maxChannels];
        for(int i = 0; i < AmbienceData.maxChannels; i++) {
            maxPriorityByChannel[i] = 0;
            for (IAmbienceSource source : ambienceSources) {
                if (source.getData().getChannel() == i && source.getData().getPriority() > maxPriorityByChannel[i] && source.getData().isUsingPriority() && canTilePlay(source))
                    maxPriorityByChannel[i] = source.getData().getPriority();
            }
        }

        ArrayList<IAmbienceSource> ambienceSourcesToPlay = new ArrayList<>();

        for(IAmbienceSource source : ambienceSources)
        {
            //this tile cannot play lol
            if(!canTilePlay(source))
                continue;

            //this tile is using priority with a valid channel
            if(source.getData().isUsingPriority() && source.getData().getChannel() <= AmbienceData.maxChannels)
                //this tile has too low of a priority, skip it '^'
                if(source.getData().getPriority() < maxPriorityByChannel[source.getData().getChannel()])
                    continue;

            ambienceSourcesToPlay.add(source);
        }

        //final boss : go through every tile and check if they're allowed to play
        for (IAmbienceSource source : ambienceSourcesToPlay) {
            //this tile uses delay which is a special case
            if (tileHasDelayRightNow(source) && source.getData().isUsingDelay()) {
                if(getDelayEntry(source).isDone()) {
                    if(isTileEntityAlreadyPlaying(source) == null){
                        playMusicNoRepeat(source, "valid delayed sound");
                        delayList.remove(getDelayEntry(source));
                        continue;
                    }
                    else
                    {
                        if(source.getData().canPlayOverSelf()){
                            if(source.getData().shouldStopPrevious()) {
                                stopMusic(isTileEntityAlreadyPlaying(source), "delay stopped it since it's playing again");
                            }
                            playMusicNoRepeat(source, "delayed sound can play over itself");
                            delayList.remove(getDelayEntry(source));
                            continue;
                        }
                    }
                }
                continue;
            }

            //that tile entity already owns a song in the list, check if it has the same song too
            //todo retired, it's tougher to implement now and seems kinda useless, the song can't change without receiving an update that resets all tile sounds
            /*if (isTileEntityAlreadyPlaying(tile) != null) {
                continue;
                //stopMusic(isTileEntityAlreadyPlaying(tile), "already playing i guess");
                //canBeFused
                //if (isTileEntityAlreadyPlaying(tile).getMusicString().equals(tile.data.getSoundName()))
                //if (hasSameMusics(isTileEntityAlreadyPlaying(tile).getOwner().data, tile.data)) {
                //if(isTileEntityAlreadyPlaying(tile)) {
                //    continue; //litterally the same tile, don't bother and skip it
                //} else {
                //    stopMusic(isTileEntityAlreadyPlaying(tile), "stopped because the song playing was different"); //stops the previous one since it has an outdated song
                //    //playMusic(tile);
                //    //continue;
                //}
            }*/

            //this tile is already in the ambience list, please don't play it again it hurts my ears
            if (isTileEntityAlreadyPlaying(source) != null) {
                continue;
                //stopMusic(isTileEntityAlreadyPlaying(tile), "already playing i guess");
                //canBeFused
                //if (isTileEntityAlreadyPlaying(tile).getMusicString().equals(tile.data.getSoundName()))
                //if (hasSameMusics(isTileEntityAlreadyPlaying(tile).getOwner().data, tile.data)) {
                //if(isTileEntityAlreadyPlaying(tile)) {
                //    continue; //litterally the same tile, don't bother and skip it
                //} else {
                //    stopMusic(isTileEntityAlreadyPlaying(tile), "stopped because the song playing was different"); //stops the previous one since it has an outdated song
                //    //playMusic(tile);
                //    //continue;
                //}
            }

            //if the music is already playing, check if you can't replace it with the new tile
            if (isMusicAlreadyPlaying(source.getData()) != null && canTilePlay(source) && source.getData().shouldFuse()) {
                //AmbienceSlot slot = isMusicAlreadyPlaying(tile.data.getSoundName());
                AmbienceSlot slot = isMusicAlreadyPlaying(source.getData());

                //todo holy shit you can't just grab the first tile and call it a day, please change this to iterate all of them
                //todo and check if they can fuse individually (even if it's unlikely that someone use both fuse and not fuse on the same music)
                if(!slot.getData().shouldFuse())
                    continue;

                double distOld = slot.getOwner().distanceTo(mc.player); //distance to already playing tile
                double distNew = source.distanceTo(mc.player); //distance to new tile we're iterating through

                //the already playing one is closer, don't swap and skip the proposed tile from the queue
                //(to avoid overlapping songs)
                if (distNew >= distOld) {
                    continue;
                }

                //the new one is closer, we should swap the tile with the new one while still playing the music
                if (distNew < distOld) {
                    swapOwner(slot, source);
                    continue;
                }
            }

            //survived the gauntlet of conditions, this tile can safely play
            playMusic(source, "reached the end of tile to play");
        }

        soundsList.removeIf(AmbienceSlot::isMarkedForDeletion);
    }

    public boolean isSoundPlaying(String sound) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getMusicString().contains(sound))
                return true;
        }
        return false;
    }

    private boolean hasSameMusics(AmbienceData oData, AmbienceData nData) {
        //if(oData.equals(nData))
        //    return false;

        return oData.getIntroName().equals(nData.getIntroName()) && oData.getSoundName().equals(nData.getSoundName()) && oData.getOutroName().equals(nData.getOutroName());
        //tile.data.getSoundName().equals(slot.getMusicString())
    }

    private void renderInvisibleTileEntityParticles() {
        List<AmbienceTileEntity> tiles = getListOfLoadedAmbienceTiles();
        boolean holdingBlock = false;
        if (this.mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            for(ItemStack itemstack : this.mc.player.getHeldEquipment()) {
                if (itemstack.getItem() == RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get()) {
                    holdingBlock = true;
                    break;
                }
            }
        }
        for(AmbienceTileEntity tile : tiles) {
            if(holdingBlock && tile.getBlockState().isIn(RegistryHandler.INVISIBLE_AMBIENCE_BLOCK.get())) {
                //System.out.println(holdingBlock);
                mc.world.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            }
        }
        //mc.world.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D + 1D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
    }

    public void playMusic(IAmbienceSource source, String reason) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), reason, RenderingEventHandler.cBlue);

        AmbienceSlot slot = new AmbienceSlot(handler, source);
        slot.play();
        soundsList.add(slot);
    }

    public void playMusicNoRepeat(IAmbienceSource source, String reason) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing non-looping " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), reason, RenderingEventHandler.cBlue);

        float volume = getVolumeFromTileEntity(source), pitch = source.getData().getPitch();

        if(source.getData().isUsingRandomVolume()) volume = (float) (source.getData().getMinRandomVolume() + Math.random() * (source.getData().getMaxRandomVolume() - source.getData().getMinRandomVolume()));
        if(source.getData().isUsingRandomPitch()) pitch = (float) (source.getData().getMinRandomPitch() + Math.random() * (source.getData().getMaxRandomPitch() - source.getData().getMinRandomPitch()));

        AmbienceSlot slot = new AmbienceSlot(handler, source);
        //supply the randomly chosen volume and pitch
        slot.setCachedVolume(volume);
        slot.setCachedPitch(pitch);
        slot.play();
        soundsList.add(slot);
    }

    public void stopMusic(AmbienceSlot soundSlot, String source) {
        //no use stopping an already stopped sound
        if(soundSlot.isStopping())
            return;

        if(debugMode)
            RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getOwner()), source, RenderingEventHandler.cRed);

        soundSlot.stop();
    }

    public void stopMusicNoFadeOut(AmbienceSlot soundSlot, String source) {
        if(debugMode)
            RenderingEventHandler.addEvent("force stopped " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getOwner()), source, RenderingEventHandler.cRed);

        soundSlot.forceStop();
        //handler.stop(soundSlot.getMusicInstance());
        //soundSlot.markForDeletion();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(AmbienceSlot soundSlot, IAmbienceSource source) {
        RenderingEventHandler.addEvent("swapping " + source.getData().getSoundName() + " from " + getAmbienceSourceName(soundSlot.getOwner()) + " to " + getAmbienceSourceName(source), null, RenderingEventHandler.cGreen);
        soundSlot.setOwner(source);
        //soundSlot.getMusicInstance().setBlockPos(tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())));
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, "stopped by stopFromTile, updated by server?");
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);

        /*for(AmbienceDelayRestriction delay : delayList) {
            if(delay.getOrigin().equals(tile))
                fhf
        }*/
    }

    private String getAmbienceSourceName(IAmbienceSource source) {
        if(source instanceof TileEntity)
            return ParsingUtil.customBlockPosToString(((TileEntity) source).getPos());
        else
            return "RegistryEntry";
    }

    public boolean canTilePlay(IAmbienceSource tile) {
        if(!tile.isWithinBounds(mc.player))
            return false;

        List<AbstractCond> conditions = tile.getData().getConditions();
        for (AbstractCond condition : conditions) {
            if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), mc.world, tile))
                return false;
        }

        return true;
    }

    public List<IAmbienceSource> getListOfAmbienceSources() {
        List<IAmbienceSource> ambienceSourceList = new ArrayList<>();

        //add all of the tiles
        ambienceSourceList.addAll(getListOfLoadedAmbienceTiles());
        //add all of the compendium entries
        //System.out.println(AmbienceCompendiumRegistry.instance.entries.size());
        ambienceSourceList.addAll(compendium.entries);

        return ambienceSourceList;
    }

    public List<AmbienceTileEntity> getListOfLoadedAmbienceTiles() {
        List<AmbienceTileEntity> ambienceTileEntityList = mc.world.loadedTileEntityList.stream().filter(tile -> tile instanceof AmbienceTileEntity).map(tile -> (AmbienceTileEntity) tile).collect(Collectors.toList());
        return ambienceTileEntityList;
    }

    public int getHighestPriorityByChannel(int index) {
        int highest = 0;
        for (AmbienceSlot slot : soundsList) {
            if (slot.getOwner().getData().isUsingPriority() && slot.getOwner().getData().getChannel() == index && slot.getOwner().getData().getPriority() > highest)
                highest = slot.getOwner().getData().getPriority();
        }
        return highest;
    }

    public AmbienceSlot isMusicAlreadyPlaying(AmbienceData data) {
        for(AmbienceSlot slot : soundsList) {
            if(hasSameMusics(data, slot.getOwner().getData()) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public AmbienceSlot isTileEntityAlreadyPlaying(IAmbienceSource tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public float getVolumeFromTileEntity(IAmbienceSource tile) {
        if(tile.getData().isGlobal())
            return tile.getData().getVolume();
        else
            return (float) (tile.getData().getVolume() * tile.getPercentageHowCloseIsPlayer(mc.player));
    }

    public float getPitchFromTileEntity(IAmbienceSource tile) {
        return tile.getData().getPitch();
    }

    public AmbienceData getClipboard() {
        return clipboard;
    }

    public void setClipboard(AmbienceData clipboard) {
        this.clipboard = clipboard;
    }

    /*//lists off every counted up Ambiences
    public class AmbienceEntry {

    }*/

    public class AmbienceDelayRestriction {
        public IAmbienceSource getOrigin() {
            return origin;
        }

        private final IAmbienceSource origin;
        private int tickLeft;

        private final int originalTick;

        public AmbienceDelayRestriction(IAmbienceSource origin, int tickLeft) {
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

        @Override
        public String toString() {
            return getOrigin().getData().getSoundName() +
                    ", " + getAmbienceSourceName(origin) + //ParsingUtil.customBlockPosToString(origin.getPos()) +
                    ", originalTick=" + originalTick +
                    ", tickLeft=" + tickLeft;
        }
    }

    public AmbienceDelayRestriction getDelayEntry(IAmbienceSource origin) {
        for(AmbienceDelayRestriction restriction : delayList) {
            if(restriction.getOrigin() == origin) return restriction;
        }
        return null;
    }

    public boolean tileHasDelayRightNow(IAmbienceSource origin) {
        return getDelayEntry(origin) != null;
    }
}
