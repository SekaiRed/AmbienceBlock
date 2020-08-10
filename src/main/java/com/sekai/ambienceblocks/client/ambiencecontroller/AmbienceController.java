package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
        tick++;
        if(tick >= 4)
        {
            //there's no world, probably on the main menu or something
            if(mc.world == null)
                return;

            //waits if the game is paused
            if(Minecraft.getInstance().isGamePaused())
                return;

            //abandon ship, no tile entities to work with
            if(mc.world.loadedTileEntityList == null)
                return;

            //before
            for(CustomSoundSlot slot : soundsList) {
                if(!handler.isPlaying(slot.getMusicInstance()) || !mc.world.loadedTileEntityList.contains(slot.getOwner()) || !slot.getOwner().isWithinBounds(mc.player)) {
                    stopMusic(slot);
                    continue;
                }

                int priority = getHighestPriority();
                if(slot.getOwner().getPriority() < priority && slot.getOwner().isUsingPriority()) {
                    stopMusic(slot);
                    continue;
                }

                if(slot.getOwner().data.needsRedstone() && !mc.world.isBlockPowered(slot.getOwner().getPos())) {
                    stopMusic(slot);
                    continue;
                }

                float volume = getVolumeFromTileEntity(slot.getOwner()), pitch = getPitchFromTileEntity(slot.getOwner());
                if(slot.getOwner().data.shouldFuse()) {
                    //setting original bias
                    //pitch *= slot.getOwner().data.getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos());
                    List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                    tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                    ArrayList<AmbienceTileEntity> ambienceTileEntityList = new ArrayList<>();
                    for (TileEntity target : tileEntityList) {
                        ambienceTileEntityList.add((AmbienceTileEntity) target);
                    }
                    for (AmbienceTileEntity target : ambienceTileEntityList) {
                        if (target == slot.getOwner())
                            continue;

                        if (target.getMusicName().equals(slot.getMusicString()) && target.isWithinBounds(mc.player)) {
                            //additive
                            volume += getVolumeFromTileEntity(target);
                            //averaging
                            //pitch = (float) ((pitch * getPitchFromTileEntity(target) * target.data.getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos())) / 2);
                            //pitch = pitch + getPitchFromTileEntity(target) * (1 - )
                        }
                    }
                }
                slot.setVolume(volume);
                slot.setPitch(pitch);

                //slot.getMusicInstance().setVolume(getVolumeFromTileEntity(slot.getOwner()));
            }

            delayList.removeIf(delay -> !mc.world.loadedTileEntityList.contains(delay.getOrigin()));

            /*for(AmbienceDelayRestriction slot : delayList) {
                if(!mc.world.loadedTileEntityList.contains(slot.getOrigin())) {
                    stopMusic(slot);
                    continue;
                }
            }*/

            //after
            ArrayList<AmbienceTileEntity> usefulTiles = new ArrayList<>();

            for (int i = 0; i < mc.world.loadedTileEntityList.size(); i++) {
                if (mc.world.loadedTileEntityList.get(i) instanceof AmbienceTileEntity) {
                    usefulTiles.add((AmbienceTileEntity) mc.world.loadedTileEntityList.get(i));

                    /*if(tileHasDelayRightNow(tile))
                    {
                        getDelayEntry(tile).tick();
                    }*/
                }
            }

            for (Iterator<AmbienceTileEntity> iterator = usefulTiles.iterator(); iterator.hasNext();) {
                AmbienceTileEntity tile = iterator.next();

                //I need to tick the delay even for tiles that are out of reach so they don't always play when you enter their range
                if(tileHasDelayRightNow(tile))
                {
                    AmbienceDelayRestriction delay = getDelayEntry(tile);
                    if(!delay.isDone())
                        delay.tick();
                    else
                        delay.restart();
                }
                else
                {
                    if(tile.data.isUsingDelay()) {
                        delayList.add(new AmbienceDelayRestriction(tile, tile.getDelay()));
                    }
                }

                if (!tile.isWithinBounds(mc.player)) {
                    iterator.remove();
                }
            }

            /*for (int i = 0; i < mc.world.loadedTileEntityList.size(); i++) {
                if (mc.world.loadedTileEntityList.get(i) instanceof AmbienceTileEntity) {
                    if(((AmbienceTileEntity) mc.world.loadedTileEntityList.get(i)).isWithinBounds(mc.player))
                        usefulTiles.add((AmbienceTileEntity) mc.world.loadedTileEntityList.get(i));

                    if(tileHasDelayRightNow(tile))
                    {
                        getDelayEntry(tile).tick();
                    }
                }
            }*/

            //System.out.println(usefulTiles.size() + " in usefulTiles");

            int highestPriority = 0;
            for(AmbienceTileEntity tile : usefulTiles)
            {
                if(tile.getPriority() > highestPriority && tile.isUsingPriority()) highestPriority = tile.getPriority();
            }

            //System.out.println(highestPriority + " is the highest priority");

            ArrayList<AmbienceTileEntity> finalTiles = new ArrayList<>();

            for(AmbienceTileEntity tile : usefulTiles)
            {
                if(tile.data.needsRedstone() && !mc.world.isBlockPowered(tile.getPos()))
                    continue;

                if(!tile.isUsingPriority())
                {
                    finalTiles.add(tile);
                    continue;
                }

                if(tile.getPriority() >= highestPriority)
                    finalTiles.add(tile);
            }

            //System.out.println(finalTiles.size() + " in finalTiles");

            for (AmbienceTileEntity tile : finalTiles) {
                //this tile uses delay which is a special case
                if (tileHasDelayRightNow(tile) && tile.data.isUsingDelay()) {
                    if(getDelayEntry(tile).isDone()) {
                        playMusicNoRepeat(tile);
                        delayList.remove(getDelayEntry(tile));
                        continue;
                    }
                    continue;
                }

                //that tile entity already owns a song in the list, check if it has the same song too
                if (isTileEntityAlreadyPlaying(tile) != null) {
                    if (isTileEntityAlreadyPlaying(tile).getMusicString().equals(tile.getMusicName())) {
                        continue; //litterally the same tile, don't bother and skip it
                    } else {
                        stopMusic(isTileEntityAlreadyPlaying(tile)); //stops the previous one since it has an outdated song
                        //playMusic(tile);
                        //continue;
                    }
                }

                //if the music is already playing, check if you can't replace it with the new tile
                if (isMusicAlreadyPlaying(tile.getMusicName()) != null && tile.data.shouldFuse()) {
                    CustomSoundSlot slot = isMusicAlreadyPlaying(tile.getMusicName());

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

                playMusic(tile);
            }

            soundsList.removeIf(CustomSoundSlot::isMarkedForDeletion);

            /*System.out.println("List of audio blocks going on");
            for (CustomSoundSlot slot : soundsList) {
                System.out.println(slot.getMusicString() + ", " + slot.getOwner().getPos() + " volume " + slot.getMusicInstance().getVolume() + slot.isMarkedForDeletion());
            }
            System.out.println("and");
            System.out.println("List of audio delays going on");
            for (AmbienceDelayRestriction slot : delayList) {
                System.out.println(slot.getOrigin().getMusicName() + ", " + slot.getOrigin().getPos() + " with a tick of " + slot.tickLeft);
            }
            System.out.println("end");*/

            tick = 0;
        }
    }

    public void playMusic(AmbienceTileEntity tile) {
        //System.out.println("playing " + tile.getMusicName() + " at " + tile.getPos());
        ResourceLocation playingResource = new ResourceLocation(tile.getMusicName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), getVolumeFromTileEntity(tile),tile.data.getPitch(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        soundsList.add(new CustomSoundSlot(tile.getMusicName(), playingMusic, tile));
    }

    public void playMusicNoRepeat(AmbienceTileEntity tile) {
        //System.out.println("playing non-looping " + tile.getMusicName() + " at " + tile.getPos());
        ResourceLocation playingResource = new ResourceLocation(tile.getMusicName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), getVolumeFromTileEntity(tile),tile.data.getPitch(), false);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        soundsList.add(new CustomSoundSlot(tile.getMusicName(), playingMusic, tile));
    }

    public void stopMusic(CustomSoundSlot soundSlot) {
        //System.out.println("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
        handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(CustomSoundSlot soundSlot, AmbienceTileEntity tile) {
        //System.out.println("swapping " + tile.getMusicName() + " from " + soundSlot.getOwner().getPos() + " to " + tile.getPos());
        soundSlot.setOwner(tile);
        soundSlot.getMusicInstance().setBlockPos(tile.getPos());
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot);
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);

        /*for(AmbienceDelayRestriction delay : delayList) {
            if(delay.getOrigin().equals(tile))
                fhf
        }*/
    }

    public int getHighestPriority() {
        int highest = 0;
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().getPriority() > highest && slot.getOwner().isUsingPriority() && !slot.isMarkedForDeletion())
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
            //getMusicInstance().setBlockPos(owner.getPos());
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

        public CustomSoundSlot clone() {
            return new CustomSoundSlot(this.musicName, this.musicRef, this.owner);
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