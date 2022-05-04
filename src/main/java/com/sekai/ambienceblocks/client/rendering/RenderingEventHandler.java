package com.sekai.ambienceblocks.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.ambience.bounds.SphereBounds;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

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
    public static final float BOUND_SEPARATION = 0.998f;

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
    public static void renderOverlay(RenderLevelLastEvent event) {
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

        //TODO Showing block bounds with the finder should be optional with the right click or something

        /*PoseStack ms = event.getPoseStack();
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
        RenderTypeHelper.LINE_BUFFERS.endBatch();*/

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        PoseStack stack = RenderSystem.getModelViewStack();
        stack.pushPose();
        stack.scale(0.998f, 0.998f, 0.998f); // fixes z fighting by pulling all faces slightly closer to the camera

        stack.mulPose(new Quaternion(Vector3f.XP, camera.getXRot(), true));
        stack.mulPose(new Quaternion(Vector3f.YP, camera.getYRot() + 180f, true));
        //PoseStack stack = event.getPoseStack();
        //stack.pushPose();

        double renderPosX = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x;
        double renderPosY = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y;
        double renderPosZ = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z;
        stack.translate(-renderPosX, -renderPosY, -renderPosZ);

        RenderSystem.applyModelViewMatrix();

        for(AmbienceTileEntity tile : AmbienceController.instance.getListOfLoadedAmbienceTiles()) {
            float[] c = tile.data.getColor();

            BoundRenderer.renderBounds(tile);

            /*long timeDiff = context.world().getTime() - positionData.time;
            float a = ((WhereIsIt.CONFIG.getFadeoutTime() - timeDiff) / (float) WhereIsIt.CONFIG.getFadeoutTime()) * 0.6f;

            Vec3d finalPos = cameraPos.subtract(positionData.pos.getX(), positionData.pos.getY(), positionData.pos.getZ()).negate();
            if (finalPos.lengthSquared() > 4096) { // if it's more than 64 blocks away, scale it so distant ones are still visible
                finalPos = finalPos.normalize().multiply(64);
            }*/

            RenderSystem.disableDepthTest();

            // Bright boxes, in front of terrain but blocked by it
            //if (!simpleRendering) {
                RenderSystem.enableDepthTest();

                buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

                //renderBlockBoundShape(buffer, tile, c);
                //renderBlockOutline(buffer, tile.getBlockPos(), c);

                tessellator.end();

                RenderSystem.disableDepthTest();
            //}

            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            // Translucent boxes, behind terrain but always visible
            buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            RenderSystem.lineWidth(4);

            /*drawShape(buffer, positionData.shape,
                    finalPos.x,
                    finalPos.y,
                    finalPos.z,
                    c[0],
                    c[1],
                    c[2],
                    c[3] * 0.6f);*/
            c[3] *= 5f;
            ////renderBlockBoundShape(buffer, tile, c);
            ////renderBlockOutline(buffer, tile.getBlockPos(), c);

            //tessellator.draw();
            tessellator.end();

            RenderSystem.depthFunc(GL11.GL_LEQUAL);

            /*if (timeDiff >= WhereIsIt.CONFIG.getFadeoutTime()) {
                toRemove.add(entry.getKey());
            }*/
        }

        stack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static void renderBlockBoundShape(BufferBuilder buffer, AmbienceTileEntity tile, float[] c) {
        AbstractBounds bounds = tile.getData().getBounds();
        if(!(bounds instanceof SphereBounds))
            return;

        float ox = (float) tile.getOrigin().x;
        float oy = (float) tile.getOrigin().y;
        float oz = (float) tile.getOrigin().z;

        float a = c[3] * 0.1f;
        float r = c[0];
        float g = c[1];
        float b = c[2];

        double radius = ((SphereBounds) bounds).getRadius();

        float ax, ay, az, ix, iy, iz, alpha, beta; // Storage for coordinates and angles
        int gradation = 10;
        float abx, aby, aplusbx, aplusby, abplusx, abplusy, aplusbplusx, aplusbplusy, abz, aplusbz;

        for (alpha = (float) 0.0; alpha + Math.PI/gradation < Math.PI; alpha += Math.PI/gradation)
        {
            for (beta = (float) 0.0; beta < 2*Math.PI; beta += Math.PI/gradation)
            {
                /*x = (float) (radius * Math.cos(beta) * Math.sin(alpha)) + ox;
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha)) + oy;
                z = (float) (radius * Math.cos(alpha)) + oz;
                buffer.vertex(x, y, z).color(r, g, b, a).endVertex();
                //glVertex3f(x, y, z);
                x = (float) (radius * Math.cos(beta)* Math.sin(alpha + Math.PI/gradation)) + ox;
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha + Math.PI/gradation)) + oy;
                z = (float) (radius * Math.cos(alpha + Math.PI/gradation)) + oz;
                buffer.vertex(x, y, z).color(r, g, b, a).endVertex();
                //glVertex3f(x, y, z);*/

                abx = (float) (radius * Math.cos(beta) * Math.sin(alpha));
                aby = (float) (radius * Math.sin(beta) * Math.sin(alpha));
                aplusbx = (float) (radius * Math.cos(beta) * Math.sin(alpha + Math.PI/gradation));
                aplusby = (float) (radius * Math.sin(beta) * Math.sin(alpha + Math.PI/gradation));
                abplusx = (float) (radius * Math.cos(beta + Math.PI/gradation) * Math.sin(alpha));
                abplusy = (float) (radius * Math.sin(beta + Math.PI/gradation) * Math.sin(alpha));
                aplusbplusx = (float) (radius * Math.cos(beta + Math.PI/gradation) * Math.sin(alpha + Math.PI/gradation));
                aplusbplusy = (float) (radius * Math.sin(beta + Math.PI/gradation) * Math.sin(alpha + Math.PI/gradation));

                abz = (float) (radius * Math.cos(alpha));
                aplusbz = (float) (radius * Math.cos(alpha + Math.PI/gradation));

                /*buffer.vertex(aplusbx + ox, aplusby + oy, aplusbz + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbplusx + ox, aplusbplusy + oy, aplusbz + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abplusx + ox, abplusy + oy, abz + oz).color(r, g, b, a).endVertex();

                buffer.vertex(abplusx + ox, abplusy + oy, abz + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abx + ox, aby + oy, abz + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbx + ox, aplusby + oy, aplusbz + oz).color(r, g, b, a).endVertex();*/

                buffer.vertex(abplusx * BOUND_SEPARATION + ox, abplusy * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbplusx * BOUND_SEPARATION + ox, aplusbplusy * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(aplusbx * BOUND_SEPARATION + ox, aplusby * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();

                buffer.vertex(aplusbx * BOUND_SEPARATION + ox, aplusby * BOUND_SEPARATION + oy, aplusbz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abx * BOUND_SEPARATION + ox, aby * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();
                buffer.vertex(abplusx * BOUND_SEPARATION + ox, abplusy * BOUND_SEPARATION + oy, abz * BOUND_SEPARATION + oz).color(r, g, b, a).endVertex();

                /*buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
                buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
                buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
                buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();*/
                //glVertex3f(x, y, z);
            }
        }
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

    private static void renderBlockOutline(BufferBuilder buffer, BlockPos pos, float[] c) {
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

        /*buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();*/
        //bottom / -y
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        //top / +y
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();

        //east / +x
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        //west / -x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
    }

    /*private static void renderBlockOutline(Matrix4f mat, VertexConsumer buffer, BlockPos pos, float[] c) {
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

        buffer.vertex(mat, ix, iy, iz).color(r, g, b, a).endVertex();
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
    }*/

    //public static void
}
