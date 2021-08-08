package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sekai.ambienceblocks.client.rendering.RenderTypeHelper;
import com.sekai.ambienceblocks.client.rendering.RenderingEventHandler;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public static Minecraft mc;
    public static SoundHandler handler;
    public static boolean debugMode = false;

    //System variables
    public int tick = 0;
    public AmbienceTileEntityData clipboard;

    //System lists
    public ArrayList<AmbienceSlot> soundsList = new ArrayList<>();
    public ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        handler = Minecraft.getInstance().getSoundHandler();
    }

    @SubscribeEvent
    public void renderOverlay(RenderWorldLastEvent event) {
        if(mc.world == null)
            return;

        boolean holdingFinder = false;
        if (mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            for(ItemStack itemstack : mc.player.getHeldEquipment()) {
                if (itemstack.getItem() == RegistryHandler.AMBIENCE_BLOCK_FINDER.get()) {
                    holdingFinder = true;
                    break;
                }
            }
        }

        if(!holdingFinder)
            return;

        MatrixStack ms = event.getMatrixStack();
        ms.push();

        for(TileEntity tile : mc.world.loadedTileEntityList) {
            if(!(tile instanceof AmbienceTileEntity))
                continue;

            AmbienceTileEntity aTile = (AmbienceTileEntity) tile;

            float[] c = aTile.data.getColor();
            //TODO change render type with thicker lines when the block is playing! Do that by adding a new thicker RenderType of lines and put it in LINE_BUFFERS
            float brightness = isTileEntityAlreadyPlaying(aTile) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;

            renderBlockOutlineAt(ms, RenderTypeHelper.LINE_BUFFERS, tile.getPos(), c);
        }

        ms.pop();
        RenderSystem.disableDepthTest();
        RenderTypeHelper.LINE_BUFFERS.finish();
    }

    private void renderBlockOutlineAt(MatrixStack ms, IRenderTypeBuffer.Impl lineBuffers, BlockPos pos, float[] c) {
        double renderPosX = Minecraft.getInstance().getRenderManager().info.getProjectedView().getX();
        double renderPosY = Minecraft.getInstance().getRenderManager().info.getProjectedView().getY();
        double renderPosZ = Minecraft.getInstance().getRenderManager().info.getProjectedView().getZ();

        ms.push();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);
        //ms.translate(pos.getX() - renderPosX, pos.getY() - renderPosY, pos.getZ() - renderPosZ + 1);

        ms.scale(1F, 1F, 1F);

        renderBlockOutline(ms.getLast().getMatrix(), lineBuffers.getBuffer(RenderTypeHelper.LINE_NO_DEPTH_TEST), pos, c);

        ms.pop();
    }

    private static void renderBlockOutline(Matrix4f mat, IVertexBuilder buffer, BlockPos pos, float[] c) {
        float ix = (float) pos.getX();
        float iy = (float) pos.getY();
        float iz = (float) pos.getZ();
        float ax = (float) pos.getX() + 1;
        float ay = (float) pos.getY() + 1;
        float az = (float) pos.getZ() + 1;
        float a = c[3];
        float r = c[0];
        float g = c[1];
        float b = c[2];

        buffer.pos(mat, ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ix, ay, iz).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, ay, iz).color(r, g, b, a).endVertex();

        buffer.pos(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.pos(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ix, iy, iz).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.pos(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ax, iy, az).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, ay, az).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ix, iy, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.pos(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.pos(mat, ax, ay, az).color(r, g, b, a).endVertex();
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
        for(AmbienceSlot slot : soundsList) {
            slot.forceStop();
        }
        soundsList.clear();

        delayList.clear();

        RenderingEventHandler.clearEvent();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        //there's no world, probably on the main menu or something
        if(mc.world == null || mc.player == null)
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
        List<AmbienceTileEntity> ambienceTiles = getListOfLoadedAmbienceTiles();

        //iterate through all ambienceslots to stop them from playing or
        ambienceIteration: for(AmbienceSlot slot : soundsList) {
            //update volume, pitch, intro/outro, fade in/out first
            slot.tick();

            if(!mc.world.loadedTileEntityList.contains(slot.getOwner())) {
                stopMusic(slot, "the owner doesn't exist");
                continue;
            }

            //the tile is out of bounds
            if(!slot.getOwner().isWithinBounds(mc.player)) {
                //it could be fused with another better candidate
                //it does this by checking if there's a valid fusing target loaded right now and
                //it just grabs the first available candidate and swaps with it instead of stopping the sound
                if (slot.getData().shouldFuse()) {
                    List<AmbienceTileEntity> ambienceTileEntityList = new ArrayList<>(ambienceTiles);
                    //ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !tile.data.getSoundName().equals(slot.getMusicString()) || !canTilePlay(tile));
                    //ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(tile.data, slot.getOwner().data) || !canTilePlay(tile));
                    ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(tile.data, slot.getData()) || !canTilePlay(tile));

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
                //condBool turns true if atleast one condition returns false
                //was i drunk when i wrote this what is this
                /*boolean condBool = false;
                List<AbstractCond> conditions = slot.getData().getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), slot.getOwner().getPos(), mc.world, slot.getOwner())) {
                        condBool = true;//continue;
                    }
                }
                if(condBool) {
                    stopMusic(slot, "conditions returned false");
                    continue;
                }*/

                List<AbstractCond> conditions = slot.getData().getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), slot.getOwner().getPos(), mc.world, slot.getOwner())) {
                        //if a single condition is false let's end this early
                        stopMusic(slot, "conditions returned false");
                        continue ambienceIteration;
                    }
                }
            }

            //fusing volume and pitch shenanigans
            //todo priority is broken with this system, maybe i could cache a priority value based on the collective one of all fuse blocks?
            if(slot.getOwner().data.shouldFuse()) {
                float volume = 0;
                float pitch = 0;
                double totalBias = 0;
                //List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                //tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                //only keep tiles that can fuse, possess the same music and is able to play right now
                ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !hasSameMusics(slot.getOwner().data, tile.data) || !canTilePlay(tile));
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    volume += getVolumeFromTileEntity(tile);
                    totalBias += tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getOrigin());
                }
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    pitch += tile.data.getPitch() * (tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getOrigin()) / totalBias);
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
        delayList.removeIf(delay -> !mc.world.loadedTileEntityList.contains(delay.getOrigin()));

        for (AmbienceTileEntity tile : ambienceTiles) {
            if (tileHasDelayRightNow(tile)) {
                AmbienceDelayRestriction delay = getDelayEntry(tile);
                if (!delay.isDone())
                    delay.tick();
                else
                    delay.restart();
            } else {
                if (tile.data.isUsingDelay()) {
                    delayList.add(new AmbienceDelayRestriction(tile, tile.data.getDelay()));
                }
            }
        }

        //getting all of the max priorities by channel
        int[] maxPriorityByChannel = new int[AmbienceTileEntityData.maxChannels];
        for(int i = 0; i < AmbienceTileEntityData.maxChannels; i++) {
            maxPriorityByChannel[i] = 0;
            for (AmbienceTileEntity tile : ambienceTiles) {
                if (tile.data.getChannel() == i && tile.data.getPriority() > maxPriorityByChannel[i] && tile.data.isUsingPriority() && canTilePlay(tile))
                    maxPriorityByChannel[i] = tile.data.getPriority();
            }
        }

        ArrayList<AmbienceTileEntity> ambienceTilesToPlay = new ArrayList<>();

        for(AmbienceTileEntity tile : ambienceTiles)
        {
            //this tile cannot play lol
            if(!canTilePlay(tile))
                continue;

            //this tile is using priority with a valid channel
            if(tile.data.isUsingPriority() && tile.data.getChannel() <= AmbienceTileEntityData.maxChannels)
                //this tile has too low of a priority, skip it '^'
                if(tile.data.getPriority() < maxPriorityByChannel[tile.data.getChannel()])
                    continue;

            ambienceTilesToPlay.add(tile);
        }

        //final boss : go through every tile and check if they're allowed to play
        for (AmbienceTileEntity tile : ambienceTilesToPlay) {
            //this tile uses delay which is a special case
            if (tileHasDelayRightNow(tile) && tile.data.isUsingDelay()) {
                if(getDelayEntry(tile).isDone()) {
                    if(isTileEntityAlreadyPlaying(tile) == null){
                        playMusicNoRepeat(tile, "valid delayed sound");
                        delayList.remove(getDelayEntry(tile));
                        continue;
                    }
                    else
                    {
                        if(tile.data.canPlayOverSelf()){
                            if(tile.data.shouldStopPrevious()) {
                                stopMusic(isTileEntityAlreadyPlaying(tile), "delay stopped it since it's playing again");
                            }
                            playMusicNoRepeat(tile, "delayed sound can play over itself");
                            delayList.remove(getDelayEntry(tile));
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
            if (isTileEntityAlreadyPlaying(tile) != null) {
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
            if (isMusicAlreadyPlaying(tile.data) != null && canTilePlay(tile) && tile.data.shouldFuse()) {
                //AmbienceSlot slot = isMusicAlreadyPlaying(tile.data.getSoundName());
                AmbienceSlot slot = isMusicAlreadyPlaying(tile.data);

                //todo holy shit you can't just grab the first tile and call it a day, please change this to iterate all of them
                //todo and check if they can fuse individually (even if it's unlikely that someone use both fuse and not fuse on the same music)
                if(!slot.getData().shouldFuse())
                    continue;

                double distOld = slot.getOwner().distanceTo(mc.player); //distance to already playing tile
                double distNew = tile.distanceTo(mc.player); //distance to new tile we're iterating through

                //the already playing one is closer, don't swap and skip the proposed tile from the queue
                //(to avoid overlapping songs)
                if (distNew >= distOld) {
                    continue;
                }

                //the new one is closer, we should swap the tile with the new one while still playing the music
                if (distNew < distOld) {
                    swapOwner(slot, tile);
                    continue;
                }
            }

            //survived the gauntlet of conditions, this tile can safely play
            playMusic(tile, "reached the end of tile to play");
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

    private boolean hasSameMusics(AmbienceTileEntityData oData, AmbienceTileEntityData nData) {
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

    public void playMusic(AmbienceTileEntity tile, String source) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing " + tile.data.getSoundName() + " at " + ParsingUtil.customBlockPosToString(tile.getPos()), source, RenderingEventHandler.cBlue);

        AmbienceSlot slot = new AmbienceSlot(handler, tile);
        slot.play();
        soundsList.add(slot);
    }

    public void playMusicNoRepeat(AmbienceTileEntity tile, String source) {
        if(debugMode)
            RenderingEventHandler.addEvent("playing non-looping " + tile.data.getSoundName() + " at " + ParsingUtil.customBlockPosToString(tile.getPos()), source, RenderingEventHandler.cBlue);

        float volume = getVolumeFromTileEntity(tile), pitch = tile.data.getPitch();

        if(tile.data.isUsingRandomVolume()) volume = (float) (tile.data.getMinRandomVolume() + Math.random() * (tile.data.getMaxRandomVolume() - tile.data.getMinRandomVolume()));
        if(tile.data.isUsingRandomPitch()) pitch = (float) (tile.data.getMinRandomPitch() + Math.random() * (tile.data.getMaxRandomPitch() - tile.data.getMinRandomPitch()));

        AmbienceSlot slot = new AmbienceSlot(handler, tile);
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
            RenderingEventHandler.addEvent("stopping " + soundSlot.getMusicString() + " at " + ParsingUtil.customBlockPosToString(soundSlot.getOwner().getPos()), source, RenderingEventHandler.cRed);

        soundSlot.stop();
    }

    public void stopMusicNoFadeOut(AmbienceSlot soundSlot, String source) {
        if(debugMode)
            RenderingEventHandler.addEvent("forced stopped " + soundSlot.getMusicString() + " at " + ParsingUtil.customBlockPosToString(soundSlot.getOwner().getPos()), source, RenderingEventHandler.cRed);

        soundSlot.forceStop();
        //handler.stop(soundSlot.getMusicInstance());
        //soundSlot.markForDeletion();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(AmbienceSlot soundSlot, AmbienceTileEntity tile) {
        if(debugMode)
            RenderingEventHandler.addEvent("swapping " + tile.data.getSoundName() + " from " + ParsingUtil.customBlockPosToString(soundSlot.getOwner().getPos()) + " to " + ParsingUtil.customBlockPosToString(tile.getPos()), null, RenderingEventHandler.cGreen);

        soundSlot.setOwner(tile);
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

    public boolean canTilePlay(AmbienceTileEntity tile) {
        /*System.out.println(tile.data.getSoundName() + " within bounds " + tile.isWithinBounds(mc.player));
        System.out.println(tile.data.getSoundName() + " needs redstone and is powered or none " + (tile.data.needsRedstone()?mc.world.isBlockPowered(tile.getPos()):true));
        return tile.isWithinBounds(mc.player) && (tile.data.needsRedstone()?mc.world.isBlockPowered(tile.getPos()):true);*/
        if(!tile.isWithinBounds(mc.player))
            return false;

        /*if(tile.data.needsRedstone()) {
            if (!mc.world.isBlockPowered(tile.getPos())) {
                return false;
            }
        }*/

        List<AbstractCond> conditions = tile.data.getConditions();
        for (AbstractCond condition : conditions) {
            if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), tile.getPos(), mc.world, tile))
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
        for (AmbienceSlot slot : soundsList) {
            if (slot.getOwner().data.isUsingPriority() && slot.getOwner().data.getChannel() == index && slot.getOwner().data.getPriority() > highest)
                highest = slot.getOwner().data.getPriority();
        }
        return highest;
    }

    /*public AmbienceSlot isMusicAlreadyPlaying(String music) {
        for(AmbienceSlot slot : soundsList) {
            if(music.equals(slot.getMusicString()) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }*/

    public AmbienceSlot isMusicAlreadyPlaying(AmbienceTileEntityData data) {
        for(AmbienceSlot slot : soundsList) {
            if(hasSameMusics(data, slot.getOwner().data) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public AmbienceSlot isTileEntityAlreadyPlaying(AmbienceTileEntity tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                return slot;
        }
        return null;
    }

    public float getVolumeFromTileEntity(AmbienceTileEntity tile) {
        if(tile.data.isGlobal())
            return tile.data.getVolume();
        else
            return (float) (tile.data.getVolume() * tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getOrigin()));
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

    /*public class CustomSoundSlot {
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
    }*/

    /*public class CustomSoundSlot {
        private String musicName;
        private AmbienceInstance musicRef;
        private AmbienceTileEntity owner;

        private boolean hasCachedVolume = false;
        private float cachedVolume = 0f;
        private boolean hasCachedPitch = false;
        private float cachedPitch = 0f;

        private boolean markForDeletion = false;

        public CustomSoundSlot(String musicName, AmbienceTileEntity owner) {
            this.musicName = musicName;
            //this.musicRef = musicRef;
            this.owner = owner;
        }

        public void play() {
            AmbienceTileEntityData d = owner.data;
            ResourceLocation playingResource;
            AmbienceInstance playingMusic;
            if(d.getType().toUpperCase().equals(AmbienceType.MUSIC)) {

            } else {
                playingResource = new ResourceLocation(owner.data.getSoundName());
                playingMusic = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), owner.getPos().add(ParsingUtil.Vec3dToVec3i(d.getOffset())), getVolumeFromTileEntity(tile),tile.data.getPitch(), tile.data.getFadeIn(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
                handler.play(playingMusic);
            }
            //if(d.getIntr)
            //ResourceLocation playingResource = new ResourceLocation(owner.data.getSoundName());
            //AmbienceInstance playingMusic = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(tile.data.getCategory().toUpperCase(), SoundCategory.MASTER), tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())), getVolumeFromTileEntity(tile),tile.data.getPitch(), tile.data.getFadeIn(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
            //handler.play(playingMusic);
        }

        public void tick() {
            //handler.
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
            //todo fuck
            //return new CustomSoundSlot(this.musicName, this.musicRef, this.owner);
            return new CustomSoundSlot(this.musicName, this.owner);
        }

        @Override
        public String toString() {
            return getMusicString() + ", " +
                    getOwner().getPos() + ", volume " + getVolume() + ", pitch " + getPitch() + ", priority " + getOwner().data.getPriority()
                    + ", channel " + getOwner().data.getChannel();
        }

        private enum AmbienceSoundState {
            INTRO,
            LOOP,
            OUTRO
        }

        private enum AmbienceFadeState {
            FADE_IN,
            MAIN,
            FADE_OUT
        }
    }*/

    /*//lists off every counted up Ambiences
    public class AmbienceEntry {

    }*/

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

        @Override
        public String toString() {
            return getOrigin().data.getSoundName() +
                    ", " + ParsingUtil.customBlockPosToString(origin.getPos()) +
                    ", originalTick=" + originalTick +
                    ", tickLeft=" + tickLeft;
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
