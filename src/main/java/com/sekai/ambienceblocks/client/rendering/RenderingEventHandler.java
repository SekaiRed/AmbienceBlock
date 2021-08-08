package com.sekai.ambienceblocks.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderingEventHandler {
    public final static int cWhite = 0xE0E0E0;//14737632;
    public final static int cRed = 0xE02020;//14680064;
    public final static int cGreen = 0x20E020;//57344;
    public final static int cBlue = 0x2020E0;//224;

    private final static float scale = 2f;
    private final static int eventListLimit = 20;
    private final static List<AmbienceEvent> eventList = new ArrayList<>();

    @SubscribeEvent
    public static void renderDebug(RenderGameOverlayEvent.Post event) {
        if(!AmbienceController.debugMode)
            return;

        if(Minecraft.getInstance().gameSettings.showDebugInfo)
            return;

        RenderSystem.defaultBlendFunc();

        //RenderSystem.enableBlend();

        ArrayList<String> listL = new ArrayList<String>();
        //ArrayList<String> listR = new ArrayList<String>();

        MatrixStack mStack = event.getMatrixStack();
        FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
        int width = event.getWindow().getScaledWidth();
        //this.mc.getMainWindow().getScaledWidth();

        mStack.push();

        mStack.scale(1/scale, 1/scale, 1/scale);

        listL.addAll(getLeft());
        //listR.addAll(getRight());

        int top = 2;
        for (String msg : listL)
        {
            if (msg == null) continue;
            AbstractGui.fill(mStack, 1, top - 1, 2 + fontrenderer.getStringWidth(msg) + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(mStack, msg, 2, top, cWhite);
            top += fontrenderer.FONT_HEIGHT;
        }

        top = 2;
        for(AmbienceEvent e : eventList) {
            if (e.msg == null) continue;
            int w = fontrenderer.getStringWidth(e.msg);
            int left = (int) (width * scale - 2 - w);
            AbstractGui.fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(mStack, e.msg, left, top, e.color);
            top += fontrenderer.FONT_HEIGHT;
            if (e.src == null) continue;
            w = fontrenderer.getStringWidth(e.src);
            left = (int) (width * scale - 2 - w);
            AbstractGui.fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(mStack, e.src, left, top, e.color);
            top += fontrenderer.FONT_HEIGHT;
        }
        /*for (String msg : listR)
        {
            if (msg == null) continue;
            int w = fontrenderer.getStringWidth(msg);
            int left = (int) (width * scale - 2 - w);
            AbstractGui.fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(mStack, msg, left, top, cWhite);
            top += fontrenderer.FONT_HEIGHT;
        }*/

        mStack.pop();
    }

    private static ArrayList<String> getLeft() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Sounds being played");
        AmbienceController.instance.soundsList.forEach(sound -> list.add(sound.toString()));
        list.add("Sounds being delayed");
        AmbienceController.instance.delayList.forEach(delay -> list.add(delay.toString()));
        return list;
    }

    /*private static ArrayList<String> getRight() {
        ArrayList<String> list = new ArrayList<>();
        //list.add("Nothing here yet!");
        //eventList.forEach(event -> list.add(event.msg));
        return list;
    }*/

    public static void clearEvent() {
        eventList.clear();
    }

    public static void addEvent(String msg, String src, int color) {
        if(eventList.size() >= eventListLimit)
            eventList.remove(0);

        eventList.add(new AmbienceEvent(msg, src, color));
    }

    private static class AmbienceEvent {
        private String msg;
        private String src;
        private int color;

        public AmbienceEvent(String msg, String src, int color) {
            this.msg = msg;
            this.src = src;
            this.color = color;
        }
    }

    @SubscribeEvent
    public static void renderOverlay(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();

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
            float brightness = AmbienceController.instance.isTileEntityAlreadyPlaying(aTile) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;

            renderBlockOutlineAt(ms, RenderTypeHelper.LINE_BUFFERS, tile.getPos(), c);
        }

        ms.pop();
        RenderSystem.disableDepthTest();
        RenderTypeHelper.LINE_BUFFERS.finish();
    }

    private static void renderBlockOutlineAt(MatrixStack ms, IRenderTypeBuffer.Impl lineBuffers, BlockPos pos, float[] c) {
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
}
