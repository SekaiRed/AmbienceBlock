package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckboxWidget extends Checkbox {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    public CheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, new TextComponent(msg), isChecked);
        this.setAlpha(1f);
    }

    @Override
    public void renderButton(PoseStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
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
        //minecraft.getTextureManager().bindForSetup(TEXTURE);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        //FontRenderer fontrenderer = minecraft.fontRenderer;
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        //RenderSystem.color(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrix, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.selected() ? 20.0F : 0.0F, 20, this.height, 64, 64);
        this.renderBg(matrix, minecraft, p_renderButton_1_, p_renderButton_2_);
        drawString(matrix, minecraft.font, this.getMessage(), this.x + 24, this.y + (this.height - 8)/2, 0xFFFFFF);
        //drawString(matrix, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xFFFFFF);
        /*if (this.field_238499_c_) {
            drawString(matrix, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }*/
    }

    public void setChecked(boolean b) {
        if(b && !super.selected())
            super.onPress();
        if(!b && super.selected())
            super.onPress();
    }

    public boolean isChecked() {
        return super.selected();
    }
}