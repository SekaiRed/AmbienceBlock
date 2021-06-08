package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class TextInstance extends Widget {
    //public int x;
    //public int y;
    public int color;
    public String text;

    public boolean active;

    private FontRenderer font;

    public TextInstance(int x, int y, int color, String text, FontRenderer font) {
        super(x, y, 0, 0, new StringTextComponent(text));

        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;

        this.width = font.getStringWidth(text);
        this.height = font.FONT_HEIGHT;

        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        if(this.visible && text != null)
            font.drawStringWithShadow(matrix, text, (float)x, (float)y, color);

        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        render(matrixStack, mouseX, mouseY);
        //super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public int getWidth() {
        return font.getStringWidth(text);
    }
}
