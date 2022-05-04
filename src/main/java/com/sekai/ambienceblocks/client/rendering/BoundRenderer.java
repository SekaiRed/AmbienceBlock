package com.sekai.ambienceblocks.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.bounds.*;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class BoundRenderer {
    public static void renderBounds(AmbienceTileEntity tile) {
        renderBounds(tile.getData(), tile.getOrigin());
    }

    public static void renderBounds(AmbienceData data, Vec3 origin) {
        RenderSystem.assertOnRenderThread();
        //Tesselator tessellator = Tesselator.getInstance();
        //BufferBuilder buffer = tessellator.getBuilder();
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

        renderBoundsInternal(data.getBounds(), origin, data.getColor());

        stack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static void renderBoundsInternal(AbstractBounds bounds, Vec3 origin, float[] color) {
        if(bounds instanceof CubicBounds) {
            renderCubic((CubicBounds) bounds, origin, color);
        } else if(bounds instanceof SphereBounds) {

        } else if(bounds instanceof CylinderBounds) {

        } else if(bounds instanceof CapsuleBounds) {

        }
    }

    private static void renderCubic(CubicBounds cubic, Vec3 origin, float[] color) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        renderCubicFilling(buffer, cubic, origin, color);
        tessellator.end();
        RenderSystem.disableDepthTest();

        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        /*buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.lineWidth(4);*/
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(4.0F);
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        renderCubicOutline(buffer, cubic, origin, color);
        tessellator.end();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        RenderSystem.enableCull();
    }

    private static void renderCubicFilling(BufferBuilder buffer, CubicBounds cubic, Vec3 origin, float[] color) {
        float ix = (float) (origin.x() - cubic.getxSize() / 2f);
        float iy = (float) (origin.y() - cubic.getySize() / 2f);
        float iz = (float) (origin.z() - cubic.getzSize() / 2f);
        float ax = (float) (origin.x() + cubic.getxSize() / 2f);
        float ay = (float) (origin.y() + cubic.getySize() / 2f);
        float az = (float) (origin.z() + cubic.getzSize() / 2f);
        float a = color[3];
        float r = color[0];
        float g = color[1];
        float b = color[2];

        //-y
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        //+y
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();

        //-x
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();

        //+x
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();

        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        //-z
        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, az).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).endVertex();

        //+z
        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).endVertex();

        buffer.vertex(ix, iy, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).endVertex();
    }

    private static void renderCubicOutline(BufferBuilder buffer, CubicBounds cubic, Vec3 origin, float[] color) {
        //Matrix4f matrix4f = p_109622_.last().pose();
        //Matrix3f matrix3f = p_109622_.last().normal();
        /*float f = (float)p_109624_;
        float f1 = (float)p_109625_;
        float f2 = (float)p_109626_;
        float f3 = (float)p_109627_;
        float f4 = (float)p_109628_;
        float f5 = (float)p_109629_;*/
        float ix = (float) (origin.x() - cubic.getxSize() / 2f);
        float iy = (float) (origin.y() - cubic.getySize() / 2f);
        float iz = (float) (origin.z() - cubic.getzSize() / 2f);
        float ax = (float) (origin.x() + cubic.getxSize() / 2f);
        float ay = (float) (origin.y() + cubic.getySize() / 2f);
        float az = (float) (origin.z() + cubic.getzSize() / 2f);
        float a = color[3];
        float r = color[0];
        float g = color[1];
        float b = color[2];

        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ix, ay, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(ix, iy, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(ax, iy, iz).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(ix, ay, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(ax, iy, az).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(ax, ay, iz).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(ax, ay, az).color(r, g, b, a).normal(0.0F, 0.0F, 1.0F).endVertex();
    }
}
