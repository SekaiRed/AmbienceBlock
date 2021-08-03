package com.sekai.ambienceblocks.client.gui.widgets.ambience;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

public class Button extends Widget {
    protected final Button.IPressable onPress;

    public Button(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction) {
        super(x, y, width, height, title);
        this.onPress = pressedAction;
    }

    /*public void onPress() {
        this.onPress.onPress(this);
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }

    }*/

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.onPress(this);
        this.playDownSound(Minecraft.getMinecraft().getSoundHandler());
    }

    @Override
    public void render(int pX, int pY, AmbienceScreen.Layer layer) {
        super.render(pX, pY, layer);
        this.isHovered = pX >= this.x && pY >= this.y && pX < this.x + this.width && pY < this.y + this.height;
        if (this.isVisible())
        {
            Minecraft mc = Minecraft.getMinecraft();
            Gui gui = AmbienceScreen.gui;
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //this.isHovered = pX >= this.x && pY >= this.y && pX < this.x + this.width && pY < this.y + this.height;
            int i = this.getHoverState(this.isHovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            gui.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            gui.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            //this.mouseDragged(mc, pX, pY); don't think i need this
            int j = 14737632;

            /* Forced button text color, apparently a fml thing, i don't need it
            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else*/
            if (!this.active)
            {
                j = 10526880;
            }
            else if (this.isHovered)
            {
                j = 16777120;
            }

            gui.drawCenteredString(fontrenderer, message.getFormattedText(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }

    @Override
    public boolean clicked(double pX, double pY) {
        return this.active && pX >= (double)this.x && pY >= (double)this.y && pX < (double)(this.x + this.width) && pY < (double)(this.y + this.height);
    }

    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.active)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

    public interface IPressable {
        void onPress(Button button);
    }
}
