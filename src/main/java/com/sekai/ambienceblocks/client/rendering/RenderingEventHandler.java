package com.sekai.ambienceblocks.client.rendering;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.init.ModItems;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Side.CLIENT)
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

        if(Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return;

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1/scale, 1/scale, 1/scale);

        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        int width = event.getResolution().getScaledWidth();
        //this.mc.getMainWindow().getScaledWidth();

        //mStack.push();

        //mStack.scale(1/scale, 1/scale, 1/scale);

        ArrayList<String> listL = new ArrayList<>(getLeft());
        //listR.addAll(getRight());

        int top = 2;
        for (String msg : listL)
        {
            if (msg == null) continue;
            drawRect(1, top - 1, 2 + fontrenderer.getStringWidth(msg) + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(msg, 2, top, cWhite);
            top += fontrenderer.FONT_HEIGHT;
        }

        top = 2;
        for(AmbienceEvent e : eventList) {
            if (e.msg == null) continue;
            int w = fontrenderer.getStringWidth(e.msg);
            int left = (int) (width * scale - 2 - w);
            drawRect(left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(e.msg, left, top, e.color);
            top += fontrenderer.FONT_HEIGHT;
            if (e.src == null) continue;
            w = fontrenderer.getStringWidth(e.src);
            left = (int) (width * scale - 2 - w);
            drawRect(left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
            fontrenderer.drawString(e.src, left, top, e.color);
            top += fontrenderer.FONT_HEIGHT;
        }

        GL11.glPopMatrix();

        //mStack.pop();
    }

    private static ArrayList<String> getLeft() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Sounds being played");
        AmbienceController.instance.soundsList.forEach(sound -> list.add(sound.toString()));
        list.add("Sounds being delayed");
        AmbienceController.instance.delayList.forEach(delay -> list.add(delay.toString()));
        return list;
    }

    public static void clearEvent() {
        eventList.clear();
    }

    public static void addEvent(String msg, AmbienceController.EventContext ctx) {
        if(eventList.size() >= eventListLimit)
            eventList.remove(0);

        eventList.add(new AmbienceEvent(msg, ctx.getComment(), ctx.getColor()));
    }

    /*public static void addEvent(String msg, String src, int color) {
        if(eventList.size() >= eventListLimit)
            eventList.remove(0);

        eventList.add(new AmbienceEvent(msg, src, color));
    }*/

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
        Minecraft mc = Minecraft.getMinecraft();

        if(mc.world == null)
            return;

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

        //ms.push();
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

            AmbienceTileEntity source = (AmbienceTileEntity) tile;

            BlockPos pos = tile.getPos();
            int sX = pos.getX();
            int sY = pos.getY();
            int sZ = pos.getZ();
            float[] c = source.data.getColor();
            float brightness = AmbienceController.instance.isSourceAlreadyPlaying(source) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;
            renderBox(sX, sY, sZ, sX + 1, sY + 1, sZ + 1, c);
            //renderBox(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), sX, sY, sZ, sX + 1, sY + 1, sZ + 1, ((AmbienceTileEntity) tile).data.getColor());
            //drawBlock(Tessellator.getInstance().getBuffer(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((AmbienceTileEntity) tile).data.getColor());
        }
        //Unapply a bunch of stuff
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        //Reset offset or we have trouble
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);

        /*for(TileEntity tile : mc.world.loadedTileEntityList) {
            if(!(tile instanceof AmbienceTileEntity))
                continue;

            AmbienceTileEntity aTile = (AmbienceTileEntity) tile;

            float[] c = aTile.data.getColor();
            //TODO change render type with thicker lines when the block is playing! Do that by adding a new thicker RenderType of lines and put it in LINE_BUFFERS
            float brightness = AmbienceController.instance.isSourceAlreadyPlaying(aTile) != null ? 1f : 0.75f;
            c[0] *= brightness;
            c[1] *= brightness;
            c[2] *= brightness;

            renderBlockOutlineAt(ms, RenderTypeHelper.LINE_BUFFERS, tile.getPos(), c);
        }*/

        //ms.pop();
    }

    private static void renderBox(double sX, double sY, double sZ, double fX, double fY, double fZ, final float[] c)
    {
        float r = c[0];
        float g = c[1];
        float b = c[2];
        float a = c[3];
        GlStateManager.glLineWidth(2.0F);
        RenderGlobal.drawBoundingBox(sX, sY, sZ, fX, fY, fZ, r, g, b, a);
        GlStateManager.glLineWidth(1.0F);
    }

    /*private static void renderBlockOutlineAt(MatrixStack ms, IRenderTypeBuffer.Impl lineBuffers, BlockPos pos, float[] c) {
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
    }*/
}
