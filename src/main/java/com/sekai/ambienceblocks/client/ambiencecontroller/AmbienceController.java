package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

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
    private ArrayList<CustomSoundSlot> sounds = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        handler = Minecraft.getInstance().getSoundHandler();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        tick++;
        if(tick >= 10)
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
            for(CustomSoundSlot slot : sounds) {
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

                if(!slot.getOwner().isGlobal()) {
                    //slot.getMusicInstance().setVolume((float) (slot.getOwner().data.getVolume() * slot.getOwner().data.getBounds().getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos())));
                    slot.getMusicInstance().setVolume((float) (slot.getOwner().data.getVolume() * slot.getOwner().data.getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos())));
                }
                else {
                    slot.getMusicInstance().setVolume(slot.getOwner().data.getVolume());
                }
            }

            //after
            ArrayList<AmbienceTileEntity> usefulTiles = new ArrayList<>();

            for (int i = 0; i < mc.world.loadedTileEntityList.size(); i++) {
                if (mc.world.loadedTileEntityList.get(i) instanceof AmbienceTileEntity) {
                    if(((AmbienceTileEntity) mc.world.loadedTileEntityList.get(i)).isWithinBounds(mc.player))
                        usefulTiles.add((AmbienceTileEntity) mc.world.loadedTileEntityList.get(i));
                }
            }

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

            sounds.removeIf(CustomSoundSlot::isMarkedForDeletion);

            /*System.out.println("List of audio blocks going on");
            for (CustomSoundSlot slot : sounds) {
                System.out.println(slot.getMusicString() + ", " + slot.getOwner().getPos() + " volume " + slot.getMusicInstance().getVolume() + slot.isMarkedForDeletion());
            }
            System.out.println("end");*/

            tick = 0;
        }
    }

    public void playMusic(AmbienceTileEntity tile) {
        //System.out.println("playing " + tile.getMusicName() + " at " + tile.getPos());
        ResourceLocation playingResource = new ResourceLocation(tile.getMusicName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?tile.data.getVolume():0.01f,tile.data.getPitch(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        sounds.add(new CustomSoundSlot(tile.getMusicName(), playingMusic, tile));
    }

    public void stopMusic(CustomSoundSlot soundSlot) {
        //System.out.println("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
        handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    public void swapOwner(CustomSoundSlot soundSlot, AmbienceTileEntity tile) {
        //System.out.println("swapping " + tile.getMusicName() + " from " + soundSlot.getOwner().getPos() + " to " + tile.getPos());
        soundSlot.setOwner(tile);
        soundSlot.getMusicInstance().setBlockPos(tile.getPos());
    }

    public void stopFromTile(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : sounds) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot);
        }
    }

    public int getHighestPriority() {
        int highest = 0;
        for(CustomSoundSlot slot : sounds) {
            if(slot.getOwner().getPriority() > highest && slot.getOwner().isUsingPriority() && !slot.isMarkedForDeletion())
                highest = slot.getOwner().getPriority();
        }
        return highest;
    }

    public CustomSoundSlot isMusicAlreadyPlaying(String music) {
        for(CustomSoundSlot slot : sounds) {
            if(music.equals(slot.getMusicString()) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public CustomSoundSlot isTileEntityAlreadyPlaying(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : sounds) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
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

        public CustomSoundSlot clone() {
            return new CustomSoundSlot(this.musicName, this.musicRef, this.owner);
        }
    }

    public class AmbienceDelayRestriction {
        private AmbienceTileEntity origin;
        private int tickLeft;

        public AmbienceDelayRestriction(AmbienceTileEntity origin, int tickLeft) {
            this.origin = origin;
            this.tickLeft = tickLeft;
        }

        public void tick() {
            tickLeft--;
        }

        public boolean isDone() {
            return tickLeft <= 0;
        }
    }
}
