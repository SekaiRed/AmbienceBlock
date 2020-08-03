package com.sekai.ambienceblocks.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;

public class TextInstance extends Widget {
    public int x;
    public int y;
    public int color;
    public String text;

    public boolean active;

    private FontRenderer font;

    public TextInstance(int x, int y, int color, String text, FontRenderer font) {
        super(x, y, text);

        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;

        this.width = font.getStringWidth(text);
        this.height = font.FONT_HEIGHT;

        this.font = font;
    }

    public void render() {
        if(this.visible && text != null)
            font.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    public int getWidth() {
        return font.getStringWidth(text);
    }
}
