package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckboxWidget extends CheckboxButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    public CheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(msg), isChecked);
        this.setAlpha(1f);
    }

    @Override
    public void renderButton(MatrixStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        //this.setAlpha(1f);
        //super.renderButton(matrix, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);

        /*Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrix, this.x, this.y, 0.0F, super.isChecked() ? 20.0F : 0.0F, 20, this.height, 32, 64);
        this.renderBg(matrix, minecraft, p_renderButton_1_, p_renderButton_2_);
        this.drawString(matrix, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xFFFFFF);*/

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrix, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.isChecked() ? 20.0F : 0.0F, 20, this.height, 64, 64);
        this.renderBg(matrix, minecraft, p_renderButton_1_, p_renderButton_2_);
        drawString(matrix, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xFFFFFF);
        /*if (this.field_238499_c_) {
            drawString(matrix, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }*/
    }

    public void setChecked(boolean b) {
        if(b && !super.isChecked())
            super.onPress();
        if(!b && super.isChecked())
            super.onPress();
    }
}