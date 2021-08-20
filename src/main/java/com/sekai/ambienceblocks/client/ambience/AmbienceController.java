package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.client.rendering.RenderingEventHandler;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.compendium.CompendiumEntry;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.ambience.compendium.ClientCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
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
    private IProfiler prf;
    public final SoundHandler handler;
    public final ClientCompendium compendium;
    public static boolean debugMode = false;

    //System variables
    public AmbienceData clipboard;

    //System lists
    public ArrayList<AmbienceSlot> soundsList = new ArrayList<>();
    public ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        prf = mc.getProfiler();
        handler = Minecraft.getInstance().getSoundHandler();
        compendium = new ClientCompendium();
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

        //I can clear the client compendium, it gets refreshed by the server upon joining anyway.
        compendium.clear();

        //AmbienceCompendiumRegistry.instance.entries.clear();
        //compendium.clear();

        //Clear the events for debug logging from memory.
        RenderingEventHandler.clearEvent();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        //prf = mc.getProfiler();
        //prf = mc.world.getProfiler();
        //prf.startTick();
        prf.startSection("ambTick");
        //there's no player, probably on the main menu or something
        if(mc.world == null || mc.player == null || e.phase.equals(TickEvent.Phase.END))
            return;

        //waits if the game is paused
        if(Minecraft.getInstance().isGamePaused())
            return;

        //abandon ship, no tile entities to work with
        if(mc.world.loadedTileEntityList == null)
            return;

        prf.startSection("renderParticles");
        //render the invisible ambience block particles client side
        renderInvisibleTileEntityParticles();
        prf.endSection();

        prf.startSection("tick");
        systemTick();
        prf.endSection();

        prf.endSection();
        //prf.endTick();
    }

    private void systemTick() {
        //get all of the loaded ambience sources
        prf.startSection("getAllSources");
        List<IAmbienceSource> ambienceSources = getListOfAmbienceSources();
        prf.endSection();

        prf.startSection("ambSlotIter");
        for (AmbienceSlot slot : soundsList) {
            //update volume, pitch, intro/outro, fade in/out first
            slot.tick();

            if (isOwnerLoaded(ambienceSources, slot)) continue;

            if (!canTilePlay(slot.getOwner())) {
                if (aboutToStopAndNeedToFuse(ambienceSources, slot))
                    continue;

                //only doing this disgusting thing to avoid the costly canTilePlayFailureContext, it's like checking canTilePlay twice, like please don't
                stopMusic(slot, debugMode ? canTilePlayFailureContext(slot.getOwner()) : EventContext.UNKNOWN);
                continue;
            }

            //fusing volume and pitch shenanigans
            //todo priority is broken with this system, maybe i could cache a priority value based on the collective one of all fuse blocks? (maybe i fixed it right now)
            if (slot.getOwner().getData().shouldFuse()) {
                float volume = 0;
                float pitch = 0;
                double totalBias = 0;
                //List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                //tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                //only keep tiles that can fuse, possess the same music and is able to play right now
                ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(slot.getOwner().getData(), tile.data) || !canTilePlay(tile));
                for (AmbienceTileEntity tile : ambienceTileEntityList) {
                    volume += getVolumeFromTileEntity(tile);
                    totalBias += tile.getPercentageHowCloseIsPlayer(mc.player);
                }
                for (AmbienceTileEntity tile : ambienceTileEntityList) {
                    pitch += tile.data.getPitch() * (tile.getPercentageHowCloseIsPlayer(mc.player) / totalBias);
                }

                //bruteforce a custom volume/pitch in there
                slot.setHasForcedVolume(true);
                slot.setHasForcedPitch(true);
                slot.setForcedVolume(volume);
                slot.setForcedPitch(pitch);
            }

            //forces to update the volume and pitch
            slot.forceVolumeAndPitchUpdate();

            //reset after applying it or it'll stay forced afterwards, not ideal but can't think of a better way lol
            slot.setHasForcedVolume(false);
            slot.setHasForcedPitch(false);
        }
        prf.endSection();

        ArrayList<IAmbienceSource> ambienceSourcesToPlay = new ArrayList<>();

        prf.startSection("getCandidates");
        for (IAmbienceSource source : ambienceSources) {
            if(source.getData().isUsingDelay()) {
                if (tileHasDelayRightNow(source)) {
                    AmbienceDelayRestriction delay = getDelayEntry(source);
                    if (!delay.isDone())
                        delay.tick();
                    else
                        delay.restart();
                } else {
                    delayList.add(new AmbienceDelayRestriction(source, source.getData().getDelay()));
                }
            }

            //this tile can play
            if (canTilePlay(source)) {
                ambienceSourcesToPlay.add(source);
            }
        }
        prf.endSection();

        prf.startSection("candidateIter");
        for (IAmbienceSource source : ambienceSourcesToPlay) {
            //this tile uses delay which is a special case
            if (tileHasDelayRightNow(source) && source.getData().isUsingDelay()) {
                if(getDelayEntry(source).isDone()) {
                    if(isTileEntityAlreadyPlaying(source) == null){
                        playMusicNoRepeat(source, EventContext.VALID_DELAY);
                        delayList.remove(getDelayEntry(source));
                        continue;
                    }
                    else
                    {
                        if(source.getData().canPlayOverSelf()){
                            if(source.getData().shouldStopPrevious()) {
                                stopMusic(isTileEntityAlreadyPlaying(source), EventContext.DELAY_STOP_PREVIOUS);
                            }
                            playMusicNoRepeat(source, EventContext.DELAY_OVER_ITSELF);
                            delayList.remove(getDelayEntry(source));
                            continue;
                        }
                    }
                }
                continue;
            }

            //this tile is already in the ambience list, please don't play it again it hurts my ears
            if (isTileEntityAlreadyPlaying(source) != null)
                continue;

            //if the music is already playing and can be fused, check if you can't replace it with the new tile
            //TODO getValidFuseTarget could potentially use a list for maximal compability, but technically only one
            // can play at a time by design, so I don't really know, come back here if a change break this
            // like changing hasSameMusic to check for intro and outro separately.
            if(source.getData().shouldFuse() && getValidFuseTarget(source.getData()) != null) {
                AmbienceSlot slot = getValidFuseTarget(source.getData());

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
            playMusic(source, EventContext.VALID_AMBIENCE);
        }
        prf.endSection();

        prf.startSection("removeUnused");
        //no use counting down the delay of an unloaded tile
        delayList.removeIf(delay -> (delay.getOrigin() instanceof AmbienceTileEntity) && !mc.world.loadedTileEntityList.contains((AmbienceTileEntity) delay.getOrigin()));

        soundsList.removeIf(AmbienceSlot::isMarkedForDeletion);
        prf.endSection();
    }

    private boolean isOwnerLoaded(List<IAmbienceSource> ambienceSources, AmbienceSlot slot) {
        //is the tile unloaded? (broken or from unloaded chunk)
        if (slot.getOwner() instanceof AmbienceTileEntity && !mc.world.loadedTileEntityList.contains((AmbienceTileEntity) slot.getOwner())) {
            if (aboutToStopAndNeedToFuse(ambienceSources, slot))
                return true;

            stopMusic(slot, EventContext.OWNER_NOT_LOADED);
            return true;
        }

        //is the compendium entry removed? (updated by compendium)
        if (slot.getOwner() instanceof CompendiumEntry && !compendium.contains((CompendiumEntry) slot.getOwner())) {
            if (aboutToStopAndNeedToFuse(ambienceSources, slot))
                return true;

            stopMusic(slot, EventContext.ENTRY_NOT_LOADED);
            return true;
        }
        return false;
    }

    private boolean aboutToStopAndNeedToFuse(List<IAmbienceSource> ambienceSources, AmbienceSlot slot) {
        if (slot.getData().shouldFuse()) {
            List<IAmbienceSource> ambienceTileEntityList = new ArrayList<>(ambienceSources);
            ambienceTileEntityList.removeIf(tile -> !tile.getData().shouldFuse() || !hasSameMusics(tile.getData(), slot.getData()) || !canTilePlay(tile) || slot.getOwner().equals(tile));

            if(ambienceTileEntityList.size() != 0) {
                swapOwner(slot, ambienceTileEntityList.get(0));
                return true;
            }
        }
        return false;
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
    }

    public void playMusic(IAmbienceSource source, EventContext ctx) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), ctx);
            //RenderingEventHandler.addEvent("playing " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), reason, RenderingEventHandler.cBlue);

        AmbienceSlot slot = new AmbienceSlot(handler, source);
        slot.play();
        soundsList.add(slot);
    }

    public void playMusicNoRepeat(IAmbienceSource source, EventContext ctx) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing non-looping " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), ctx);

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

    public void stopMusic(AmbienceSlot soundSlot, EventContext ctx) {
        //no use stopping an already stopped sound
        if(soundSlot.isStopping())
            return;

        if(debugMode)
            RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getOwner()), ctx);
            //RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getOwner()), source, RenderingEventHandler.cRed);

        soundSlot.stop();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(AmbienceSlot soundSlot, IAmbienceSource source) {
        if(debugMode)
            RenderingEventHandler.addEvent("swapping " + source.getData().getSoundName() + " from " + getAmbienceSourceName(soundSlot.getOwner()) + " to " + getAmbienceSourceName(source), EventContext.SWAPPING);

        soundSlot.setOwner(source);
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, EventContext.PACKET_RECEIVED);
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);
    }

    public String getAmbienceSourceName(IAmbienceSource source) {
        if(source instanceof TileEntity)
            return ParsingUtil.customBlockPosToString(((TileEntity) source).getPos());
        else
            return "RegistryEntry";
    }

    public boolean canTilePlay(IAmbienceSource tile) {
        if(!tile.isWithinBounds(mc.player))
            return false;

        if(tile.getData().isUsingCondition()) {
            List<AbstractCond> conditions = tile.getData().getConditions();
            for (AbstractCond condition : conditions) {
                if (!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), mc.world, tile))
                    return false;
            }
        }

        int priority = getHighestPriorityByChannel(tile.getData().getChannel());
        if (tile.getData().isUsingPriority() && tile.getData().getPriority() < priority)
            return false;

        return true;
    }

    private EventContext canTilePlayFailureContext(IAmbienceSource tile) {
        if(!tile.isWithinBounds(mc.player))
            return EventContext.OUT_OF_BOUNDS;

        if(tile.getData().isUsingCondition()) {
            List<AbstractCond> conditions = tile.getData().getConditions();
            for (AbstractCond condition : conditions) {
                if (!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), mc.world, tile))
                    return EventContext.CONDITION_IS_FALSE;
            }
        }

        int priority = getHighestPriorityByChannel(tile.getData().getChannel());
        if (tile.getData().isUsingPriority() && tile.getData().getPriority() < priority)
            return EventContext.OUT_PRIORITIZED;
            //return "lower priority than the maximal one : slot priority " + tile.getData().getPriority() + " and max priority " + priority;

        return EventContext.UNKNOWN;
    }

    public List<IAmbienceSource> getListOfAmbienceSources() {
        List<IAmbienceSource> ambienceSourceList = new ArrayList<>();

        //add all of the tiles
        ambienceSourceList.addAll(getListOfLoadedAmbienceTiles());
        //add all of the compendium entries
        ambienceSourceList.addAll(compendium.getAllEntries());

        return ambienceSourceList;
    }

    public List<AmbienceTileEntity> getListOfLoadedAmbienceTiles() {
        return mc.world.loadedTileEntityList.stream().filter(tile -> tile instanceof AmbienceTileEntity).map(tile -> (AmbienceTileEntity) tile).collect(Collectors.toList());
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

    public AmbienceSlot getValidFuseTarget(AmbienceData data) {
        //no point looking
        if(!data.shouldFuse())
            return null;

        for(AmbienceSlot slot : soundsList) {
            //has both compatible music and can fuse
            //copied from isMusicAlreadyPlaying because there should by logic be only one instance of the fusing music being played
            if(hasSameMusics(data, slot.getOwner().getData()) && !slot.isMarkedForDeletion() && slot.getData().shouldFuse())
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

    public AmbienceDelayRestriction getDelayEntry(IAmbienceSource origin) {
        for(AmbienceDelayRestriction restriction : delayList) {
            if(restriction.getOrigin() == origin) return restriction;
        }
        return null;
    }

    public boolean tileHasDelayRightNow(IAmbienceSource origin) {
        return getDelayEntry(origin) != null;
    }

    public enum EventContext {
        //TODO I could easily replace comment with translation keys and translate them on the client
        VALID_AMBIENCE("this tile passed every test and can play", RenderingEventHandler.cBlue),
        DELAY_OVER_ITSELF("delayed sound can play over itself", RenderingEventHandler.cBlue),
        VALID_DELAY("valid delayed sound", RenderingEventHandler.cBlue),
        SWAPPING("", RenderingEventHandler.cGreen),
        DELAY_STOP_PREVIOUS("delay stopped it since it's playing again", RenderingEventHandler.cRed),
        OWNER_NOT_LOADED("the owner doesn't exist", RenderingEventHandler.cRed),
        ENTRY_NOT_LOADED("the entry doesn't exist", RenderingEventHandler.cRed),
        PACKET_RECEIVED("forcefully stopped, updated by server?", RenderingEventHandler.cRed),
        OUT_OF_BOUNDS("not within bounds", RenderingEventHandler.cRed),
        CONDITION_IS_FALSE("at least one condition returned false", RenderingEventHandler.cRed),
        OUT_PRIORITIZED("lower priority than the highest one on this channel", RenderingEventHandler.cRed),
        UNKNOWN("no idea why that happened", RenderingEventHandler.cWhite);

        private final String comment;
        private final int color;

        EventContext(String comment, int color) {
            this.comment = comment;
            this.color = color;
        }

        public String getComment() {
            return comment;
        }

        public int getColor() {
            return color;
        }
    }
}
