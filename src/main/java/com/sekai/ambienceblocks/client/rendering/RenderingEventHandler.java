package com.sekai.ambienceblocks.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
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

        if(Minecraft.getInstance().options.renderDebug)
            return;

        if(!event.getType().equals(RenderGameOverlayEvent.ElementType.ALL))
            return;

        RenderSystem.defaultBlendFunc();

        //RenderSystem.enableBlend();

        //ArrayList<String> listR = new ArrayList<String>();

        PoseStack mStack = event.getMatrixStack();
        Font fontrenderer = Minecraft.getInstance().font;
        int width = event.getWindow().getGuiScaledWidth();
        //int width = event.getWindow().getScaledWidth();
        //this.mc.getMainWindow().getScaledWidth();

        mStack.pushPose();

        mStack.scale(1/scale, 1/scale, 1/scale);

        ArrayList<String> listL = new ArrayList<>(getLeft());
        //listR.addAll(getRight());

        int top = 2;
        for (String msg : listL)
        {
            if (msg == null) continue;
            Gui.fill(mStack, 1, top - 1, 2 + fontrenderer.width(msg) + 1, top + fontrenderer.lineHeight - 1, -1873784752);
            fontrenderer.draw(mStack, msg, 2, top, cWhite);
            top += fontrenderer.lineHeight;
        }

        top = 2;
        for(AmbienceEvent e : eventList) {
            if (e.msg == null) continue;
            int w = fontrenderer.width(e.msg);
            int left = (int) (width * scale - 2 - w);
            Gui.fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.lineHeight - 1, -1873784752);
            fontrenderer.draw(mStack, e.msg, left, top, e.color);
            top += fontrenderer.lineHeight;
            if (e.src == null) continue;
            w = fontrenderer.width(e.src);
            left = (int) (width * scale - 2 - w);
            Gui.fill(mStack, left - 1, top - 1, left + w + 1, top + fontrenderer.lineHeight - 1, -1873784752);
            fontrenderer.draw(mStack, e.src, left, top, e.color);
            top += fontrenderer.lineHeight;
        }

        mStack.popPose();
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

    public static void addEvent(String msg, AmbienceController.EventContext ctx) {
        if(eventList.size() >= eventListLimit)
            eventList.remove(0);

        eventList.add(new AmbienceEvent(msg, ctx.getComment(), ctx.getColor()));
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

        if(mc.level == null)
            return;

        boolean holdingFinder = false;
        if (mc.gameMode.getPlayerMode() == GameType.CREATIVE) {
            for(ItemStack itemstack : mc.player.getHandSlots()) {
                if (itemstack.getItem() == RegistryHandler.AMBIENCE_BLOCK_FINDER.get()) {
                    holdingFinder = true;
                    break;
                }
            }
        }

        if(!holdingFinder)
            return;

        PoseStack ms = event.getMatrixStack();
        ms.pushPose();

        for(AmbienceTileEntity tile : AmbienceController.instance.getListOfLoadedAmbienceTiles()) {
            //System.out.println(tile.getData());
            float[] c = tile.data.getColor();
            //TODO change render type with thicker lines when the block is playing! Do that by adding a new thicker RenderType of lines and put it in LINE_BUFFERS
            float brightness = AmbienceController.instance.isSourceAlreadyPlaying(tile) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;

            //RenderSystem.setShader(GameRenderer::getPositionColorShader);
            renderBlockOutlineAt(ms, RenderTypeHelper.LINE_BUFFERS, tile.getBlockPos(), c);
            //renderBlockOutlineAt(ms, mc.renderBuffers().bufferSource(), tile.getBlockPos(), c);
        }

        //RenderTypeHelper.LINE_NO_DEPTH_TEST.end();
        //RenderTypeHelper.LINE_BUFFERS.endBatch(RenderTypeHelper.LINE_NO_DEPTH_TEST);
        ms.popPose();
        //RenderSystem.disableDepthTest();
        RenderTypeHelper.LINE_BUFFERS.endBatch();
    }

    private static void renderBlockOutlineAt(PoseStack ms, MultiBufferSource.BufferSource lineBuffers, BlockPos pos, float[] c) {
        /*double renderPosX = Minecraft.getInstance().getRenderManager().info.getProjectedView().getX();
        double renderPosY = Minecraft.getInstance().getRenderManager().info.getProjectedView().getY();
        double renderPosZ = Minecraft.getInstance().getRenderManager().info.getProjectedView().getZ();*/
        double renderPosX = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x;
        double renderPosY = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y;
        double renderPosZ = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z;

        ms.pushPose();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);
        //RenderSystem.applyModelViewMatrix();
        //ms.translate(pos.getX() - renderPosX, pos.getY() - renderPosY, pos.getZ() - renderPosZ + 1);

        //ms.scale(1F, 1F, 1F);

        //renderBlockOutline(ms.last().pose(), ms.last().normal(), lineBuffers.getBuffer(RenderTypeHelper.LINE_NO_DEPTH_TEST), pos, c);
        LevelRenderer.renderLineBox(ms, lineBuffers.getBuffer(RenderTypeHelper.LINE_NO_DEPTH_TEST), pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1, pos.getZ()+1, c[0], c[1], c[2], c[3]);
        //LevelRenderer.renderLineBox(ms, lineBuffers.getBuffer(RenderType.lines()), pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1, pos.getZ()+1, c[0], c[1], c[2], c[3]);

        ms.popPose();
    }

    private static void renderBlockOutline(Matrix4f mat, Matrix3f normal, VertexConsumer buffer, BlockPos pos, float[] c) {
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

        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).normal(normal, 0f, 1f, 0f).endVertex();
        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();

        /*buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(mat, ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(mat, ax, ay, az).color(r, g, b, a).endVertex();*/
    }

    //public static void
}
