package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.client.particles.AmbienceParticle;
import com.sekai.ambienceblocks.init.ModBlocks;
import com.sekai.ambienceblocks.init.ModItems;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public static Minecraft mc;
    public static SoundHandler handler;
    public static final boolean debugMode = false;
    public static SoundRegistry soundRegistry;

    //System variables
    public int tick = 0;
    public AmbienceData clipboard;

    //System lists
    private final ArrayList<AmbienceSlot> soundsList = new ArrayList<>();
    private final ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    public AmbienceController() {
        instance = this;
        mc = Minecraft.getMinecraft();
        handler = Minecraft.getMinecraft().getSoundHandler();

        Field soundRegistryField = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147697_e");
        soundRegistryField.setAccessible(true);
        try {
            soundRegistry = (SoundRegistry) soundRegistryField.get(mc.getSoundHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderWorldLastEvent event) {
        if(mc.world == null)
            return;

        ///*TODO restore ambience block finder later
        boolean holdingFinder = false;
        if (mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            for(ItemStack itemstack : mc.player.getHeldEquipment()) {
                if (itemstack.getItem() == ModItems.AMBIENCE_BLOCK_FINDER) {
                    holdingFinder = true;
                    break;
                }
            }
        }

        if(!holdingFinder)
            return;

        renderFoundTileAmbienceTiles(event);
    }

    private void renderFoundTileAmbienceTiles(RenderWorldLastEvent event) {
        // Usually the player
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        //Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)event.getPartialTicks();
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)event.getPartialTicks();
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)event.getPartialTicks();
        //Apply 0-our transforms to set everything back to 0,0,0
        Tessellator.getInstance().getBuffer().setTranslation(-d0, -d1, -d2);
        //Apply a bunch of stuff
        //GlStateManager.shadeModel(7425);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        //Your render function which renders boxes at a desired position. In this example I just copy-pasted the one on TileEntityStructureRenderer
        for(TileEntity tile : mc.world.loadedTileEntityList) {
            if(!(tile instanceof AmbienceTileEntity))
                continue;

            AmbienceTileEntity aTile = (AmbienceTileEntity) tile;

            BlockPos pos = tile.getPos();
            int sX = pos.getX();
            int sY = pos.getY();
            int sZ = pos.getZ();
            float[] c = aTile.data.getColor();
            float brightness = isTileEntityAlreadyPlaying(aTile) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;
            renderBox(sX, sY, sZ, sX + 1, sY + 1, sZ + 1, c);
            //renderBox(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), sX, sY, sZ, sX + 1, sY + 1, sZ + 1, ((AmbienceTileEntity) tile).data.getColor());
            //drawBlock(Tessellator.getInstance().getBuffer(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((AmbienceTileEntity) tile).data.getColor());
        }
        //Unapply a bunch of stuff
        //GlStateManager.shadeModel(7424);
        //GlStateManager.enableLighting();
        /*GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();*/
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        //renderBox(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), sX, sY, sZ, sX + 1, sY + 1, sZ + 1);
        //Reset offset or we have trouble
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
    }

    private void renderBox(Tessellator tessellator, BufferBuilder buffer, double sX, double sY, double sZ, double fX, double fY, double fZ, final float[] c)
    {
        float r = c[0];
        float g = c[1];
        float b = c[2];
        float a = c[3];
        GlStateManager.glLineWidth(2.0F);
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        /*buffer.pos(sX, sY, sZ).color((float)c2, (float)c2, (float)c2, 0.0F).endVertex();
        buffer.pos(sX, sY, sZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, sY, sZ).color(c2, c3, c3, c1).endVertex();
        buffer.pos(fX, sY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, sY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, sY, sZ).color(c3, c3, c2, c1).endVertex();
        buffer.pos(sX, fY, sZ).color(c3, c2, c3, c1).endVertex();
        buffer.pos(fX, fY, sZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, fY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, fY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, fY, sZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, fY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(sX, sY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, sY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, fY, fZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, fY, sZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, sY, sZ).color(c2, c2, c2, c1).endVertex();
        buffer.pos(fX, sY, sZ).color((float)c2, (float)c2, (float)c2, 0.0F).endVertex();*/
        RenderGlobal.drawBoundingBox(buffer, sX, sY, sZ, fX, fY, fZ, r, g, b, a);
        tessellator.draw();
        GlStateManager.glLineWidth(1.0F);
    }

    private void renderBox(double sX, double sY, double sZ, double fX, double fY, double fZ, final float[] c)
    {
        float r = c[0];
        float g = c[1];
        float b = c[2];
        float a = c[3];
        GlStateManager.glLineWidth(2.0F);
        RenderGlobal.drawBoundingBox(sX, sY, sZ, fX, fY, fZ, r, g, b, a);
        GlStateManager.glLineWidth(1.0F);
    }

    /*//TODO Reimplement find block draw function
    private static void drawBlock(final BufferBuilder bufferbuilder, final double x, final double y, final double z, final float[] c) {
        double size = 0.5;
        if(c.length != 4)
            return;

        float r = c[0];
        float g = c[1];
        float b = c[2];
        float a = c[3];

        //bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        // UP
        bufferbuilder.pos(-size + x, size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, -size + z).color(r, g, b, a).endVertex();

        // DOWN
        bufferbuilder.pos(-size + x, -size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, -size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, -size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, -size + y, size + z).color(r, g, b, a).endVertex();

        // LEFT
        bufferbuilder.pos(size + x, -size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, -size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, size + z).color(r, g, b, a).endVertex();

        // RIGHT
        bufferbuilder.pos(-size + x, -size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, -size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, size + y, -size + z).color(r, g, b, a).endVertex();

        // BACK
        bufferbuilder.pos(-size + x, -size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, -size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, -size + y, -size + z).color(r, g, b, a).endVertex();

        // FRONT
        bufferbuilder.pos(size + x, -size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size + x, size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, size + y, size + z).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size + x, -size + y, size + z).color(r, g, b, a).endVertex();
    }*/

    @SubscribeEvent
    public void joinWorld(PlayerEvent.PlayerLoggedInEvent e) {
        clear();
    }

    @SubscribeEvent
    public void leaveWorld(PlayerEvent.PlayerLoggedOutEvent e) {
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
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        //there's no world, probably on the main menu or something
        if(mc.world == null || mc.player == null)
            return;

        //waits if the game is paused
        if(mc.isGamePaused())
            return;

        //abandon ship, no tile entities to work with
        if(mc.world.loadedTileEntityList == null)
            return;

        //render the invisible ambience block particles client side
        //todo restore
        renderInvisibleTileEntityParticles();

        //get all of the loaded ambienceTileEntities
        List<AmbienceTileEntity> ambienceTiles = getListOfLoadedAmbienceTiles();

        //iterate through all ambienceslots to stop them from playing or
        for(AmbienceSlot slot : soundsList) {
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
                boolean condBool = false;
                List<AbstractCond> conditions = slot.getData().getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vector3d(mc.player.posX, mc.player.posY, mc.player.posZ), slot.getOwner().getPos(), mc.world, slot.getOwner())) {
                        condBool = true;//continue;
                    }
                }
                if(condBool) {
                    stopMusic(slot, "conditions returned false");
                    continue;
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
        int[] maxPriorityByChannel = new int[AmbienceData.maxChannels];
        for(int i = 0; i < AmbienceData.maxChannels; i++) {
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
            if(tile.data.isUsingPriority() && tile.data.getChannel() <= AmbienceData.maxChannels)
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

        if(debugMode) {
            System.out.println("List of audio blocks going on");
            for (AmbienceSlot slot : soundsList) {
                System.out.println(slot.toString());
                //System.out.println(slot.getMusicString() + ", " + slot.getOwner().getPos() + " volume " + slot.getMusicInstance().getVolume() + " pitch " + slot.getMusicInstance().getPitch());
            }
            System.out.println("and");
            System.out.println("List of audio delays going on");
            for (AmbienceDelayRestriction slot : delayList) {
                System.out.println(slot.getOrigin().data.getSoundName() + ", " + slot.getOrigin().getPos() + " with a tick of " + slot.tickLeft);
            }
            System.out.println("end");
        }
    }

    public boolean isSoundPlaying(String sound) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getMusicString().contains(sound))
                return true;
        }
        return false;
    }

    //didn't work out :(
    /*public boolean isPlaying(ISound sound) {
        //Gather variables
        try {
            //sndEngine = (SoundEngine) sndManager.get(handler);
            engineSoundStopTime = (Map<ISound, Integer>) playingSoundsStopTime.get(sndEngine);
            engineSoundChannel = (Map<ISound, ChannelManager.Entry>) playingSoundsChannel.get(sndEngine);
            engineTick = soundTicks.getInt(sndEngine);
            engineInit = soundInit.getBoolean(sndEngine);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!engineInit) {
            return false;
        } else {
            //return engineSoundStopTime.containsKey(sound) && engineSoundStopTime.get(sound) <= engineTick - 100 ? true : engineSoundChannel.containsKey(sound);
            return engineSoundStopTime.containsKey(sound) && engineSoundStopTime.get(sound) <= engineTick + 10;
            //return this.playingSoundsStopTime.containsKey(soundIn) && this.playingSoundsStopTime.get(soundIn) <= this.ticks ? true : this.playingSoundsChannel.containsKey(soundIn);
        }
    }*/

    private boolean hasSameMusics(AmbienceData oData, AmbienceData nData) {
        //if(oData.equals(nData))
        //    return false;

        return oData.getIntroName().equals(nData.getIntroName()) && oData.getSoundName().equals(nData.getSoundName()) && oData.getOutroName().equals(nData.getOutroName());
        //tile.data.getSoundName().equals(slot.getMusicString())
    }

    //TODO restore this based on how it's done by barrier blocks
    private void renderInvisibleTileEntityParticles() {
        List<AmbienceTileEntity> tiles = getListOfLoadedAmbienceTiles();
        boolean holdingBlock = false;
        if (this.mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            for(ItemStack itemstack : this.mc.player.getHeldEquipment()) {
                if (itemstack.getItem() == Item.getItemFromBlock(ModBlocks.INVISIBLE_AMBIENCE_BLOCK)) {
                    holdingBlock = true;
                    break;
                }
            }
        }
        for(AmbienceTileEntity tile : tiles) {
            if(holdingBlock && tile.getBlockType().equals(ModBlocks.INVISIBLE_AMBIENCE_BLOCK)) {
                Minecraft.getMinecraft().effectRenderer.addEffect(new AmbienceParticle(mc.world, tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D, Item.getItemFromBlock(ModBlocks.INVISIBLE_AMBIENCE_BLOCK)));
                //System.out.println(holdingBlock);
                //mc.world.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            }
        }
        //mc.world.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D + 1D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
    }

    private static void drawParticle(Particle particle) {
        if(particle != null)
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public void playMusic(AmbienceTileEntity tile, String source) {
        if(debugMode) {
            System.out.print("playing " + tile.data.getSoundName() + " at " + tile.getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        //todo fuck
        //soundsList.add(new CustomSoundSlot(tile.data.getSoundName(), playingMusic, tile));
        AmbienceSlot slot = new AmbienceSlot(handler, tile);
        slot.play();
        soundsList.add(slot);
    }

    public void playMusicNoRepeat(AmbienceTileEntity tile) {
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
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        soundSlot.stop();
    }

    public void stopMusicNoFadeOut(AmbienceSlot soundSlot, String source) {
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        soundSlot.forceStop();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(AmbienceSlot soundSlot, AmbienceTileEntity tile) {
        if(debugMode) {
            System.out.println("swapping " + tile.data.getSoundName() + " from " + soundSlot.getOwner().getPos() + " to " + tile.getPos());
        }
        soundSlot.setOwner(tile);
    }

    //Mostly used when the tile gets an update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(AmbienceSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, "stopped by stopFromTile, updated by server?");
        }

        delayList.removeIf(delay -> delay.getOrigin() == tile);
    }

    public boolean canTilePlay(AmbienceTileEntity tile) {
        if(!tile.isWithinBounds(mc.player))
            return false;

        List<AbstractCond> conditions = tile.data.getConditions();
        for (AbstractCond condition : conditions) {
            if(!condition.isTrue(new Vector3d(mc.player.posX, mc.player.posY, mc.player.posZ), tile.getPos(), mc.world, tile))
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
        int highest = 0;
        for (AmbienceSlot slot : soundsList) {
            if (slot.getOwner().data.isUsingPriority() && slot.getOwner().data.getChannel() == index && slot.getOwner().data.getPriority() > highest)
                highest = slot.getOwner().data.getPriority();
        }
        return highest;
    }

    public AmbienceSlot isMusicAlreadyPlaying(AmbienceData data) {
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

    public AmbienceData getClipboard() {
        return clipboard;
    }

    public void setClipboard(AmbienceData clipboard) {
        this.clipboard = clipboard;
    }

    public static boolean isValidSound(String name) {
        for(ResourceLocation resource : soundRegistry.getKeys()) {
            if(resource.toString().equals(name))
                return true;
        }
        return false;
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
