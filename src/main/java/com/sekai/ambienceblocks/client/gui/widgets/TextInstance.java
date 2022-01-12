package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

public class TextInstance extends AbstractWidget {
    //public int x;
    //public int y;
    public int color;
    public String text;

    public boolean active;

    private Font font;

    public TextInstance(int x, int y, int color, String text, Font font) {
        super(x, y, 0, 0, new TextComponent(text));

        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;

        this.width = font.width(text);
        this.height = font.lineHeight;

        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render(PoseStack matrix, int mouseX, int mouseY) {
        if(this.visible && text != null)
            font.drawShadow(matrix, text, (float)x, (float)y, color);

        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        render(matrixStack, mouseX, mouseY);
        //super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public int getWidth() {
        return font.width(text);
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
