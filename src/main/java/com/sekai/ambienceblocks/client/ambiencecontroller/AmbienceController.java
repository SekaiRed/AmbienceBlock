package com.sekai.ambienceblocks.client.ambiencecontroller;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sekai.ambienceblocks.client.particles.AmbienceParticle;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AmbienceController {
    //Static references
    public static AmbienceController instance;
    public static Minecraft mc;
    public static SoundHandler handler;
    private static final boolean debugMode = false;

    //System variables
    public int tick = 0;
    public AmbienceTileEntityData clipboard;

    //System lists
    private ArrayList<CustomSoundSlot> soundsList = new ArrayList<>();
    private ArrayList<AmbienceDelayRestriction> delayList = new ArrayList<>();

    //What


    public AmbienceController() {
        instance = this;
        mc = Minecraft.getInstance();
        handler = Minecraft.getInstance().getSoundHandler();
    }

    @SubscribeEvent
    public void renderOverlay(RenderWorldLastEvent event) {
        //RegistryHandler.AMBIENCE_BLOCK_FINDER.get().
        //if(Minecraft.getInstance().player.getHeldItemMainhand().getItem())
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

        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        //BlockPos pos = new BlockPos(0, 64, 0); // Just somewhere in the world
        //BlockPos pos = mc.player.getPosition().add(0, 0, 2);
        MatrixStack matrixStack = event.getMatrixStack();

        //Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        //RenderSystem.multMatrix(matrix4f);

        RenderSystem.pushMatrix();
        matrixStack.translate(-renderInfo.getProjectedView().getX(), -renderInfo.getProjectedView().getY(), -renderInfo.getProjectedView().getZ()); // translate back to camera
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderSystem.depthFunc(519);
        Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for(TileEntity tile : mc.world.loadedTileEntityList) {
            if(!(tile instanceof AmbienceTileEntity))
                continue;

            BlockPos pos = tile.getPos();
            drawBlock(Tessellator.getInstance().getBuffer(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((AmbienceTileEntity) tile).data.getColor());
        }
        Tessellator.getInstance().draw();
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();

        /*IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());
        MatrixStack matrixStack = event.getMatrixStack();

        PlayerEntity player = Minecraft.getInstance().player;
        double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
        double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
        double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

        matrixStack.push();
        matrixStack.translate(-x, -y, -z);
        Matrix4f matrix = matrixStack.getLast().getMatrix();

        builder.pos(matrix, 0, 0, 0).color(1f, 0, 0, 1f).endVertex();
        builder.pos(matrix, 0, 256, 0).color(1f, 0, 0, 1f).endVertex();

        //buffer.finish();

        matrixStack.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(RenderType.LINES);*/

        /*RenderSystem.enableAlphaTest();
        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.push();
        matrixstack.translate((double)((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F))), (double)((float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F))), -50.0D);
        float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
        matrixstack.scale(f7, -f7, f7);
        matrixstack.rotate(Vector3f.YP.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(f4))));
        matrixstack.rotate(Vector3f.XP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
        matrixstack.rotate(Vector3f.ZP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
        IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderTypeBuffers.getBufferSource();
        this.mc.getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixstack, irendertypebuffer$impl);
        matrixstack.pop();
        irendertypebuffer$impl.finish();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();*/


        /*private void drawSquare(BlockPos pos) {
            Tessellator tess = Tessellator.INSTANCE;
            tess.startDrawing(7);
            tess.setBrightness(15728880);
            tess.setColorOpaque_F(1F, 0F, 0F);
            //GL11.glColor3f(1F, 0F, 0F);//red
            float f = 1f/16f;
            //Y-Group
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.001, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord - 0.001, thisBlock.zCoord + 0.5 - f);

            //X-Group
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord - 0.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);

            //Z-Group
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord - 0.001);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.001);

            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.001);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord - 0.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord - 0.001);

            //Y-Group+1
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 1.001, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.999, thisBlock.zCoord + 0.5 - f);

            //X-Group+1
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 1.001, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);

            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 - f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 + f);
            tess.addVertex(thisBlock.xCoord + 0.999, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.5 - f);

            //Z-Group+1
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.999);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 1.001);

            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 1.001);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 1.001);

            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 - f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 + f, thisBlock.zCoord + 0.999);
            tess.addVertex(thisBlock.xCoord + 0.5 + f, thisBlock.yCoord + 0.5 - f, thisBlock.zCoord + 0.999);

            tess.draw();
        }*/

        //mc.worldRenderer.draw
        //WorldRenderer.drawBoundingBox(event.getMatrixStack(), event.getContext().getEntityOutlineFramebuffer(),bufferIn.getBuffer(RenderType.getLines())/*event.getBuffers().getBuffer*/);
        //WorldRenderer.drawBoundingBox(event.getMatrixStack(), event.getContext().getEntityOutlineFramebuffer().,bufferIn.getBuffer(RenderType.getLines())/*event.getBuffers().getBuffer*/);

        /*// your positions. You might want to shift them a bit too
        int sX = yourX;
        int sY = yourY;
        int sZ = yourZ;
// Usually the player
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
//Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)evt.getPartialTicks();
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)evt.getPartialTicks();
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)evt.getPartialTicks();
//Apply 0-our transforms to set everything back to 0,0,0
        Tessellator.getInstance().getBuffer().setTranslation(-d0, -d1, -d2);
//Your render function which renders boxes at a desired position. In this example I just copy-pasted the one on TileEntityStructureRenderer
        renderBox(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), sX, sY, sZ, sX + 1, sY + 1, sZ + 1);
//When you are done rendering all your boxes reset the offsets. We do not want everything that renders next to still be at 0,0,0 :)
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);*/
    }

    private static void drawBlock(final BufferBuilder bufferbuilder, final double x, final double y, final double z, final float[] c) {
        double size = 0.5;
        if(c.length != 4)
            return;

        float r = c[0];
        float g = c[1];
        float b = c[2];
        float a = c[3];

        /*float r = 1f;
        float g = 0f;
        float b = 0f;
        float a = 1f;*/

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
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        //there's no world, probably on the main menu or something
        if(mc.world == null || mc.player == null)
            return;

        //waits if the game is paused
        if(Minecraft.getInstance().isGamePaused())
            return;

        //abandon ship, no tile entities to work with
        if(mc.world.loadedTileEntityList == null)
            return;

        renderInvisibleTileEntityParticles();

        //check if a sound needs to be stopped and change volume and pitch
        for(CustomSoundSlot slot : soundsList) {
            if(!handler.isPlaying(slot.getMusicInstance()) || !mc.world.loadedTileEntityList.contains(slot.getOwner())) {
                stopMusic(slot, "music wasn't playing, the owner didn't exist anymore, the player isn't within it's bounds");
                continue;
            }

            //the tile is out of bounds
            if(!slot.getOwner().isWithinBounds(mc.player)) {
                //it could be fused
                if (slot.getOwner().data.shouldFuse()) {
                    List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                    ambienceTileEntityList.removeIf(tile -> !tile.data.shouldFuse() || !tile.data.getSoundName().equals(slot.getMusicString()) || !canTilePlay(tile));

                    if(ambienceTileEntityList.size() != 0) {
                        swapOwner(slot, ambienceTileEntityList.get(0));
                        continue;
                    }
                }

                stopMusic(slot, "not within bounds and couldn't find another block to fuse with");
                continue;
            }

            if(slot.getOwner().data.isUsingPriority())
            {
                int priority = getHighestPriorityByChannel(slot.getOwner().data.getChannel());
                if (slot.getOwner().data.getPriority() < priority) {
                    stopMusic(slot, "lower priority than the maximal one : slot priority " + slot.getOwner().data.getPriority() + " and max priority " + priority);
                    continue;
                }
            }

            /*if(slot.getOwner().data.needsRedstone() && !mc.world.isBlockPowered(slot.getOwner().getPos())) {
                stopMusic(slot, "needed redstone but wasn't powered");
                continue;
            }*/

            if(slot.getOwner().data.isUsingCondition()) {
                boolean condBool = false;
                List<AbstractCond> conditions = slot.getOwner().data.getConditions();
                for (AbstractCond condition : conditions) {
                    if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), slot.getOwner().getPos(), mc.world)) {
                        condBool = true;//continue;
                    }
                }
                if(condBool) {
                    stopMusic(slot, "conditions returned false");
                    continue;
                }
            }

            //volume stuff
            float volume = getVolumeFromTileEntity(slot.getOwner()), pitch = getPitchFromTileEntity(slot.getOwner());

            AmbienceTileEntityData data = slot.getOwner().data;

            if(slot.hasCachedVolume()) volume = data.isGlobal() ? slot.getCachedVolume() : (float) (slot.getCachedVolume() * data.getPercentageHowCloseIsPlayer(mc.player, slot.getOwner().getPos()));
            if(slot.hasCachedPitch()) pitch = slot.getCachedPitch();

            if(slot.getOwner().data.shouldFuse()) {
                volume = 0; pitch = 0; double totalBias = 0;
                //List<TileEntity> tileEntityList = mc.world.loadedTileEntityList;
                //tileEntityList.removeIf(lambda -> !(lambda instanceof AmbienceTileEntity));
                List<AmbienceTileEntity> ambienceTileEntityList = getListOfLoadedAmbienceTiles();
                ambienceTileEntityList.removeIf(lambda -> !lambda.data.getSoundName().equals(slot.getMusicString()) || !canTilePlay(lambda));
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    volume += getVolumeFromTileEntity(tile);
                    totalBias += tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getPos());
                }
                for(AmbienceTileEntity tile : ambienceTileEntityList) {
                    pitch += tile.data.getPitch() * (tile.data.getPercentageHowCloseIsPlayer(mc.player, tile.getPos()) / totalBias);
                }
            }
            slot.setVolume(volume);
            slot.setPitch(pitch);
        }

        delayList.removeIf(delay -> !mc.world.loadedTileEntityList.contains(delay.getOrigin()));

        List<AmbienceTileEntity> usefulTiles = getListOfLoadedAmbienceTiles();

        //delay stuff, should execute regardless of if we're within bounds or not
        for (AmbienceTileEntity tile : usefulTiles) {
            //mc.world.addParticle(AmbienceParticle.getParticle(mc.world, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get()), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D + 1D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            //mc.particles.addEffect(AmbienceParticle.getParticle(mc.world, tile.getPos().getX(), tile.getPos().getY() + 2D, tile.getPos().getZ(), RegistryHandler.INVISIBLE_AMBIENCE_BLOCK_ITEM.get()));
            //mc.world.addParticle(RegistryHandler.PARTICLE_SPEAKER.get(), tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D + 1D, tile.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
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

        //getting all of the priorities by channel
        int[] maxPriorityByChannel = new int[AmbienceTileEntityData.maxChannels];
        for(int i = 0; i < AmbienceTileEntityData.maxChannels; i++) {
            maxPriorityByChannel[i] = 0;
            for (AmbienceTileEntity tile : usefulTiles) {
                if (tile.data.getChannel() == i && tile.data.getPriority() > maxPriorityByChannel[i] && tile.data.isUsingPriority() && canTilePlay(tile))
                    maxPriorityByChannel[i] = tile.data.getPriority();
            }
        }

        ArrayList<AmbienceTileEntity> ambienceTilesToPlay = new ArrayList<>();

        for(AmbienceTileEntity tile : usefulTiles)
        {
            //this tile cannot play
            if(!canTilePlay(tile))
                continue;

            /*if(tile.data.needsRedstone() && !mc.world.isBlockPowered(tile.getPos()))
                continue;*/

            //this tile is using priority and is of a high enough priority
            if(tile.data.isUsingPriority() && tile.data.getChannel() < AmbienceTileEntityData.maxChannels)
                if(tile.data.getPriority() < maxPriorityByChannel[tile.data.getChannel()])
                    continue;
                //ambienceTilesToPlay.add(tile);

            ambienceTilesToPlay.add(tile);
        }

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
            if (isTileEntityAlreadyPlaying(tile) != null) {
                if (isTileEntityAlreadyPlaying(tile).getMusicString().equals(tile.data.getSoundName())) {
                    continue; //litterally the same tile, don't bother and skip it
                } else {
                    stopMusic(isTileEntityAlreadyPlaying(tile), "stopped because the song playing was different"); //stops the previous one since it has an outdated song
                    //playMusic(tile);
                    //continue;
                }
            }

            //if the music is already playing, check if you can't replace it with the new tile
            if (isMusicAlreadyPlaying(tile.data.getSoundName()) != null && canTilePlay(tile) && tile.data.shouldFuse()) {
                CustomSoundSlot slot = isMusicAlreadyPlaying(tile.data.getSoundName());
                if(slot != null) {
                    if(!(slot.getOwner().data.shouldFuse() && canTilePlay(slot.getOwner())))
                        continue;

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
            }

            playMusic(tile, "reached the end of tile to play");
        }

        soundsList.removeIf(CustomSoundSlot::isMarkedForDeletion);

        if(debugMode) {
            System.out.println("List of audio blocks going on");
            for (CustomSoundSlot slot : soundsList) {
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
        if(debugMode) {
            System.out.print("playing " + tile.data.getSoundName() + " at " + tile.getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        //System.out.println("playing " + tile.getMusicName() + " at " + tile.getPos());
        ResourceLocation playingResource = new ResourceLocation(tile.data.getSoundName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(tile.data.getCategory().toUpperCase(), SoundCategory.MASTER), tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())), getVolumeFromTileEntity(tile),tile.data.getPitch(), tile.data.getFadeIn(), true);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        soundsList.add(new CustomSoundSlot(tile.data.getSoundName(), playingMusic, tile));
    }

    public void playMusicNoRepeat(AmbienceTileEntity tile) {
        //System.out.println("playing non-looping " + tile.getMusicName() + " at " + tile.getPos());
        float volume = getVolumeFromTileEntity(tile), pitch = tile.data.getPitch();

        boolean usingRandomVolume = false;
        if(tile.data.isUsingRandomVolume()) usingRandomVolume = true;
        boolean usingRandomPitch = false;
        if(tile.data.isUsingRandomPitch()) usingRandomPitch = true;

        if(usingRandomVolume) volume = (float) (tile.data.getMinRandomVolume() + Math.random() * (tile.data.getMaxRandomVolume() - tile.data.getMinRandomVolume()));
        if(usingRandomPitch) pitch = (float) (tile.data.getMinRandomPitch() + Math.random() * (tile.data.getMaxRandomPitch() - tile.data.getMinRandomPitch()));

        ResourceLocation playingResource = new ResourceLocation(tile.data.getSoundName());
        AmbienceInstance playingMusic = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(tile.data.getCategory().toUpperCase(), SoundCategory.MASTER), tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())), volume, pitch, tile.data.getFadeIn(), false);//AmbienceInstance(playingResource, SoundCategory.AMBIENT, tile.getPos(), tile.isGlobal()?1.0f:0.01f);
        handler.play(playingMusic);
        CustomSoundSlot custom = new CustomSoundSlot(tile.data.getSoundName(), playingMusic, tile);
        soundsList.add(custom);
        if(usingRandomVolume) custom.setCachedVolume(volume);
        if(usingRandomPitch) custom.setCachedPitch(pitch);
    }

    public void stopMusic(CustomSoundSlot soundSlot, String source) {
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        if(soundSlot.getOwner().data.getFadeOut() != 0f && !soundSlot.getOwner().data.isUsingDelay())
            soundSlot.getMusicInstance().stop(soundSlot.getOwner().data.getFadeOut());
        else
            handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    public void stopMusicNoFadeOut(CustomSoundSlot soundSlot, String source) {
        if(debugMode) {
            System.out.print("stopping " + soundSlot.getMusicString() + " at " + soundSlot.getOwner().getPos());
            System.out.print(" from " + source);
            System.out.println(" ");
        }
        handler.stop(soundSlot.getMusicInstance());
        soundSlot.markForDeletion();
    }

    //Swaps which tiles is currently owning a playing sound, used for relays and fusing
    public void swapOwner(CustomSoundSlot soundSlot, AmbienceTileEntity tile) {
        if(debugMode) {
            System.out.println("swapping " + tile.data.getSoundName() + " from " + soundSlot.getOwner().getPos() + " to " + tile.getPos());
        }
        soundSlot.setOwner(tile);
        soundSlot.getMusicInstance().setBlockPos(tile.getPos().add(ParsingUtil.Vec3dToVec3i(tile.data.getOffset())));
    }

    //Mostly used when the tile gets update from a request by the server
    public void stopFromTile(AmbienceTileEntity tile) {
        for(CustomSoundSlot slot : soundsList) {
            if(slot.getOwner().equals(tile) && !slot.isMarkedForDeletion())
                stopMusic(slot, "stoppped from stopFromTile, updated by server?");
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
            if(!condition.isTrue(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), tile.getPos(), mc.world))
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
        for (CustomSoundSlot slot : soundsList) {
            if (slot.getOwner().data.isUsingPriority() && slot.getOwner().data.getChannel() == index && slot.getOwner().data.getPriority() > highest)
                highest = slot.getOwner().data.getPriority();
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
        if(tile.data.isGlobal())
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
