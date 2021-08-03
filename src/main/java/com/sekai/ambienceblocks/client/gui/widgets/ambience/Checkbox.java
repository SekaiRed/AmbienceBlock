package com.sekai.ambienceblocks.client.gui.widgets.ambience;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

import static com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen.BACKGROUND_TEXTURE;

public class Checkbox extends Widget {
    //TODO Actually I think GuiUtils.drawContinuousTexturedBox can use any size for the checkbox, need to check once the porting is done
    private static final int CHECKBOX_SIZE = 20;

    private boolean isChecked;

    public Checkbox(int x, int y, int width, int height, ITextComponent message, boolean isChecked) {
        super(x, y, width, height, message);
        this.isChecked = isChecked;
    }

    @Override
    public boolean clicked(double pX, double pY) {
        return this.active && this.isVisible() && pX >= (double)this.x && pY >= (double)this.y && pX < (double)(this.x + this.width) && pY < (double)(this.y + this.height);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        if (this.active && this.isVisible() && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + CHECKBOX_SIZE && mouseY < this.y + this.height)
        {
            this.isChecked = !this.isChecked;
            this.playDownSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    @Override
    public void render(int pX, int pY, AmbienceScreen.Layer layer) {
        super.render(pX, pY, layer);
        if (this.isVisible())
        {
            Minecraft mc = Minecraft.getMinecraft();
            Gui gui = AmbienceScreen.gui;
            this.isHovered = pX >= this.x && pY >= this.y && pX < this.x + this.width && pY < this.y + this.height;
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, CHECKBOX_SIZE, this.height, 200, 20, 2, 3, 2, 2, 0 /*layer.getZLevel()*/);
            //this.mouseDragged(mc, mouseX, mouseY); didn't see it being used and i'm too tired to put it in
            //int color = 14737632;
            int color = 0xFFFFFF;

            if (!this.active)
            {
                color = 10526880;
            }

            if (this.isChecked) {
                mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                gui.drawTexturedModalRect(x, y, 64, 184, 20, 20);
            }

            /*if (this.isChecked)
                gui.drawCenteredString(mc.fontRenderer, "x", this.x + CHECKBOX_SIZE / 2 + 1, this.y + CHECKBOX_SIZE / 2 + 1, 14737632);*/

            gui.drawString(mc.fontRenderer, message.getFormattedText(), this.x + CHECKBOX_SIZE + 4, this.y + (CHECKBOX_SIZE - mc.fontRenderer.FONT_HEIGHT) / 2, color);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
