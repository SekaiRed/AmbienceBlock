package com.sekai.ambienceblocks.client.gui.widgets;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiCheckBox;*/

import com.sekai.ambienceblocks.util.Unused;
import net.minecraftforge.fml.client.config.GuiCheckBox;

/**
 * @deprecated Use {@link com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox}
 */
@Deprecated
@Unused(type = Unused.Type.REMOVE)
public class CheckboxWidget extends GuiCheckBox {
    public CheckboxWidget(int id, int xPos, int yPos, String displayString, boolean isChecked) {
        super(id, xPos, yPos, displayString, isChecked);
    }
    /*private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    public CheckboxWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(msg), isChecked);
        this.setAlpha(1f);
    }

    @Override
    public void renderButton(MatrixStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        //this.setAlpha(1f);
        //super.renderButton(matrix, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);

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
    }

    public void setChecked(boolean b) {
        if(b && !super.isChecked())
            super.onPress();
        if(!b && super.isChecked())
            super.onPress();
    }*/
}