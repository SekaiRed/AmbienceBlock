package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.compendium.BaseCompendium;
import com.sekai.ambienceblocks.ambience.compendium.CompendiumEntry;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.sync.structure.StructureSyncClient;
import com.sekai.ambienceblocks.ambience.sync.target.TargetSyncClient;
import com.sekai.ambienceblocks.client.rendering.RenderingEventHandler;
import com.sekai.ambienceblocks.client.util.SoundReflection;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//This class mostly handles the ambience system, but it also has some client only data
public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public final Minecraft mc;
    private ProfilerFiller prf;
    public final SoundManager handler;
    //public final ClientCompendium compendium;
    public static boolean debugMode = false;

    //Sub-systems
    //Client only systems that are easier to package with this general client only class
    public final SoundReflection reflection;
    public final BaseCompendium compendium;
    public final TargetSyncClient target;
    public final StructureSyncClient structure;

    //System variables
    public AmbienceData clipboard;

    //System lists
    public ArrayList<AmbienceTileEntity> ambienceTileEntities = new ArrayList<>();
    public ArrayList<AmbienceSlot> soundsList = new ArrayList<>();
    public ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    //Reflection
    public static final Field BLOCK_ENTITY_TICKERS_FIELD = ObfuscationReflectionHelper.findField(Level.class, "f_151512_"); //blockEntityTickers
    public List<TickingBlockEntity> blockEntityTickers;

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        prf = mc.getProfiler();
        handler = Minecraft.getInstance().getSoundManager();
        reflection = new SoundReflection(handler);
        compendium = new BaseCompendium();
        target = new TargetSyncClient();
        structure = new StructureSyncClient();
    }

    @SubscribeEvent
    public void soundReload(SoundLoadEvent e) {
        clear();
    }

    @SubscribeEvent
    public void joinWorld(ClientPlayerNetworkEvent.LoggedInEvent e) {
        clear();

        /*if(mc.level != null)
            fetchBlockEntityList(mc.level);*/
    }

    @SuppressWarnings("unchecked")
    /*private void fetchBlockEntityList(Level level) {
        try {
            blockEntityTickers = (List<TickingBlockEntity>) BLOCK_ENTITY_TICKERS_FIELD.get(level);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/

    @SubscribeEvent
    public void leaveWorld(ClientPlayerNetworkEvent.LoggedOutEvent e) {
        //Only clear the compendium when leaving the world
        compendium.clear();
        clear();

        //blockEntityTickers = null;
    }

    //clears everything ambience related
    //just to be safe
    private void clear() {
        ambienceTileEntities.clear();
        soundsList.forEach(AmbienceSlot::forceStop);
        soundsList.clear();
        delayList.clear();

        //I can clear the client compendium, it gets refreshed by the server upon joining anyway.
        //Removed! This made it so that reloading resource packs cleared all compendium entries and I would need to query them again from the server
        //compendium.clear();

        //Clear the events for debug logging from memory.
        RenderingEventHandler.clearEvent();
    }

    //TODO after a bit of research, it seems Client and Server variants of TickEvent happen after IProfiler is done, oof
    // either I port this code over WorldTick or I keep it as is and I give up on the /debug to find out which conditions
    // cost the most performance, a small price to pay for salvation
    // After trying for a while it's just too bothersome to try and do, especially if it's only to see how much time
    // my system wastes
    // 8/24/2021 : I could instead use the good old System.getTime trick, I could even wrap it in a class to have
    // the times be averaged and separated into sections without having to dump it all in a file, maybe
    // that info could be shown on the debug screen in the bottom left corner?
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        prf.push("ambTick");
        //there's no player, probably on the main menu or something
        if(mc.level == null || mc.player == null || e.phase.equals(TickEvent.Phase.END))
            return;

        //waits if the game is paused
        if(Minecraft.getInstance().isPaused())
            return;

        //abandon ship, no tile entities to work with
        if(ambienceTileEntities == null)
            return;

        //tile entities unloaded
        ambienceTileEntities.removeIf(ambienceTile -> mc.level.getBlockEntity(ambienceTile.getBlockPos()) == null);

        prf.push("renderParticles");
        //render the invisible ambience block particles client side
        renderInvisibleTileEntityParticles();
        prf.pop();

        prf.push("tick");
        target.tick();
        structure.tick();
        systemTick();
        prf.pop();

        prf.pop();
    }

    private void systemTick() {
        //get all of the loaded ambience sources
        prf.push("getAllSources");
        List<IAmbienceSource> ambienceSources = getListOfAmbienceSources();
        prf.pop();

        prf.push("ambSlotIter");
        for (AmbienceSlot slot : soundsList) {
            //update volume, pitch, intro/outro, fade in/out first
            slot.tick();

            if (isOwnerLoaded(ambienceSources, slot)) continue;

            CanPlayResult result = canTileKeepPlaying(slot.getSource());
            if (!result.succeeded()) {
                if (aboutToStopAndNeedToFuse(ambienceSources, slot))
                    continue;

                stopMusic(slot, result.getContext());
                continue;
            }

            //fusing volume and pitch shenanigans
            //todo priority is broken with this system, maybe i could cache a priority value based on the collective one of all fuse blocks? (maybe i fixed it right now)
            if (slot.getSource().getData().shouldFuse()) {
                float volume = 0;
                float pitch = 0;
                double totalBias = 0;
                //List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                //tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<IAmbienceSource> sourceList = getListOfAmbienceSources();
                //only keep tiles that can fuse, possess the same music and is able to play right now
                sourceList.removeIf(tile -> !tile.getData().shouldFuse() || !hasSameMusics(slot.getSource().getData(), tile.getData()) || !canTilePlay(tile).succeeded());
                for (IAmbienceSource source : sourceList) {
                    volume += getVolumeFromTileEntity(source);
                    totalBias += source.getPercentageHowCloseIsPlayer(mc.player);
                }
                for (IAmbienceSource source : sourceList) {
                    pitch += source.getData().getPitch() * (source.getPercentageHowCloseIsPlayer(mc.player) / totalBias);
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
        prf.pop();

        ArrayList<IAmbienceSource> ambienceSourcesToPlay = new ArrayList<>();

        prf.push("getCandidates");
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
            if (canTilePlay(source).succeeded()) {
                ambienceSourcesToPlay.add(source);
            }
        }
        prf.pop();

        prf.push("candidateIter");
        for (IAmbienceSource source : ambienceSourcesToPlay) {
            //this tile uses delay which is a special case
            if (tileHasDelayRightNow(source) && source.getData().isUsingDelay()) {
                if(getDelayEntry(source).isDone()) {
                    if(isSourceAlreadyPlaying(source) == null){
                        playMusicNoRepeat(source, EventContext.VALID_DELAY);
                        delayList.remove(getDelayEntry(source));
                        continue;
                    }
                    else
                    {
                        if(source.getData().canPlayOverSelf()){
                            if(source.getData().shouldStopPrevious()) {
                                stopMusic(isSourceAlreadyPlaying(source), EventContext.DELAY_STOP_PREVIOUS);
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
            if (isSourceAlreadyPlaying(source) != null && !isSourceAlreadyPlaying(source).isStopping())
                continue;

            //if the music is already playing and can be fused, check if you can't replace it with the new tile
            //TODO getValidFuseTarget could potentially use a list for maximal compability, but technically only one
            // can play at a time by design, so I don't really know, come back here if a change break this
            // like changing hasSameMusic to check for intro and outro separately.
            if(source.getData().shouldFuse() && getValidFuseTarget(source.getData()) != null) {
                AmbienceSlot slot = getValidFuseTarget(source.getData());

                double distOld = slot.getSource().distanceTo(mc.player); //distance to already playing tile
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
        prf.pop();

        prf.push("removeUnused");
        //no use counting down the delay of an unloaded tile
        delayList.removeIf(delay -> (delay.getOrigin() instanceof AmbienceTileEntity) && !ambienceTileEntities.contains((AmbienceTileEntity) delay.getOrigin()));

        soundsList.removeIf(AmbienceSlot::isMarkedForDeletion);
        prf.pop();
    }

    private boolean isOwnerLoaded(List<IAmbienceSource> ambienceSources, AmbienceSlot slot) {
        //is the tile unloaded? (broken or from unloaded chunk)
        if (slot.getSource() instanceof AmbienceTileEntity && !ambienceTileEntities.contains((AmbienceTileEntity) slot.getSource())) {
            if (aboutToStopAndNeedToFuse(ambienceSources, slot))
                return true;

            stopMusic(slot, EventContext.OWNER_NOT_LOADED);
            return true;
        }

        //is the compendium entry removed? (updated by compendium)
        if (slot.getSource() instanceof CompendiumEntry && !compendium.contains((CompendiumEntry) slot.getSource())) {
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
            ambienceTileEntityList.removeIf(source -> !source.getData().shouldFuse() || !hasSameMusics(source.getData(), slot.getData()) || !canTilePlay(source).succeeded() || slot.getSource().equals(source));

            if(ambienceTileEntityList.size() != 0) {
                swapOwner(slot, ambienceTileEntityList.get(0));
                return true;
            }
        }
        return false;
    }

    public boolean isSoundPlaying(String sound) {
        for(AmbienceSlot slot : soundsList) {
            //slot.getMusicString().contains(sound)
            if(ParsingUtil.validateString(slot.getMusicString(), sound))
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
        if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.CREATIVE) {
            for(ItemStack itemstack : this.mc.player.getHandSlots()) {
                if (itemstack.getItem() == RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get()) {
                    holdingBlock = true;
                    break;
                }
            }
        }
        for(AmbienceTileEntity tile : tiles) {
            if(holdingBlock && tile.getBlockState().is(RegistryHandler.INVISIBLE_AMBIENCE_BLOCK.get())) {
                //mc.level.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getBlockPos().getX() + 0.5D, tile.getBlockPos().getY() + 0.5D, tile.getBlockPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                mc.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, RegistryHandler.INVISIBLE_AMBIENCE_BLOCK.get().defaultBlockState()), tile.getBlockPos().getX() + 0.5D, tile.getBlockPos().getY() + 0.5D, tile.getBlockPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                //new StationaryItemParticle(p_172376_, p_172377_, p_172378_, p_172379_, Blocks.BARRIER.asItem());
            }
        }
    }

    public void playMusic(IAmbienceSource source, EventContext ctx) {
        /*if(debugMode)
            RenderingEventHandler.addEvent("playing " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), ctx);*/

        AmbienceSlot slot = null;

        for (AmbienceSlot sound : soundsList) {
            if (sound.getSource().equals(source) && sound.isStopping())
                slot = sound;
        }

        if(slot == null) {
            slot = new AmbienceSlot(handler, source);
            slot.play();
            soundsList.add(slot);

            if(debugMode)
                RenderingEventHandler.addEvent("playing " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), ctx);
        } else {
            slot.resume();

            if(debugMode)
                RenderingEventHandler.addEvent("resuming " + source.getData().getSoundName() + " at " + getAmbienceSourceName(source), ctx);
        }

        /*AmbienceSlot slot = new AmbienceSlot(handler, source);
        slot.play();
        soundsList.add(slot);*/
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
            RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getSource()), ctx);
            //RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + getAmbienceSourceName(soundSlot.getOwner()), source, RenderingEventHandler.cRed);

        soundSlot.stop();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(AmbienceSlot soundSlot, IAmbienceSource source) {
        if(debugMode)
            RenderingEventHandler.addEvent("swapping " + source.getData().getSoundName() + " from " + getAmbienceSourceName(soundSlot.getSource()) + " to " + getAmbienceSourceName(source), EventContext.SWAPPING);

        soundSlot.setSource(source);
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getSource().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, EventContext.PACKET_RECEIVED);
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);
    }

    //Called when the compendium is updated
    public void stopAllCompendium() {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getSource() instanceof CompendiumEntry)
                stopMusic(slot, EventContext.PACKET_RECEIVED);
        }
    }

    public String getAmbienceSourceName(IAmbienceSource source) {
        if(source instanceof BlockEntity)
            return ParsingUtil.customBlockPosToString(((BlockEntity) source).getBlockPos());
        else
            return "RegistryEntry";
    }

    public CanPlayResult canTilePlay(IAmbienceSource source) {
        return canTilePlayInternal(source, false);
    }

    public CanPlayResult canTileKeepPlaying(IAmbienceSource source) {
        return canTilePlayInternal(source, true);
    }

    public CanPlayResult canTilePlayInternal(IAmbienceSource source, boolean alreadyStarted) {
        if(!source.isWithinBounds(mc.player))
            return CanPlayResult.fail(EventContext.OUT_OF_BOUNDS);

        if(source.getData().isUsingCondition()) {
            List<AbstractCond> conditions = source.getData().getConditions();
            for (AbstractCond condition : conditions) {
                if (!condition.isTrue(mc.player, mc.level, source))
                    return CanPlayResult.fail(EventContext.CONDITION_IS_FALSE);
            }
        }

        int priority = getHighestPriorityByChannel(source.getData().getChannel());
        //if (source.getData().isUsingPriority() && source.getData().getPriority() < priority)
        if(source.getData().isUsingPriority() && ((source.getData().canPlayAtSamePriority() || alreadyStarted) ? (source.getData().getPriority() < priority) : (source.getData().getPriority() <= priority)))
            return CanPlayResult.fail(EventContext.OUT_PRIORITIZED);
        //return "lower priority than the maximal one : slot priority " + source.getData().getPriority() + " and max priority " + priority;

        return CanPlayResult.succeed();
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
        return ambienceTileEntities;
        //return mc.world.loadedTileEntityList.stream().filter(tile -> tile instanceof AmbienceTileEntity).map(tile -> (AmbienceTileEntity) tile).collect(Collectors.toList());
    }

    public int getHighestPriorityByChannel(int index) {
        int highest = -1;
        for (AmbienceSlot slot : soundsList) {
            if (slot.getSource().getData().isUsingPriority() && slot.getSource().getData().getChannel() == index && slot.getSource().getData().getPriority() > highest)
                highest = slot.getSource().getData().getPriority();
        }
        return highest;
    }

    public AmbienceSlot isMusicAlreadyPlaying(AmbienceData data) {
        for(AmbienceSlot slot : soundsList) {
            if(hasSameMusics(data, slot.getSource().getData()) && !slot.isMarkedForDeletion())
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
            if(hasSameMusics(data, slot.getSource().getData()) && !slot.isMarkedForDeletion() && slot.getData().shouldFuse())
                return slot;
        }
        return null;
    }

    public AmbienceSlot isSourceAlreadyPlaying(IAmbienceSource source) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getSource().equals(source) && !slot.isMarkedForDeletion())
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

    public void addAmbienceTileEntity(AmbienceTileEntity tileEntity) {
        ambienceTileEntities.add(tileEntity);
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
        PACKET_RECEIVED("forcefully stopped, updated by server", RenderingEventHandler.cRed),
        OUT_OF_BOUNDS("not within bounds", RenderingEventHandler.cRed),
        CONDITION_IS_FALSE("at least one condition returned false", RenderingEventHandler.cRed),
        OUT_PRIORITIZED("lower priority than the highest one on this channel", RenderingEventHandler.cRed),
        UNKNOWN("no idea how that happened", RenderingEventHandler.cWhite);

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

    public static class CanPlayResult {
        boolean success;
        EventContext context;

        private CanPlayResult(boolean success, EventContext context) {
            this.success = success;
            this.context = context;
        }

        protected static CanPlayResult succeed() {
            return new CanPlayResult(true, EventContext.UNKNOWN);
        }

        protected static CanPlayResult fail(EventContext context) {
            return new CanPlayResult(false, context);
        }

        public boolean succeeded() {
            return success;
        }

        public EventContext getContext() {
            return context;
        }
    }
}
