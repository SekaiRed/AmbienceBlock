package com.sekai.ambienceblocks.client.gui.widgets;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextComponentString;

public class TextInstance extends Widget {
    //public int x;
    //public int y;
    public int color;
    public String text;

    public boolean active;

    private final FontRenderer font;

    public TextInstance(int x, int y, int color, String text, FontRenderer font) {
        super(x, y, 0, 0, new TextComponentString(text));

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

    @Override
    public void render(int pX, int pY, AmbienceScreen.Layer layer) {
        if(this.isVisible() && text != null)
            font.drawStringWithShadow(text, (float)x, (float)y, color);

        this.isHovered = pX >= this.x && pY >= this.y && pX < this.x + this.width && pY < this.y + this.height;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    public int getWidth() {
        return font.getStringWidth(text);
    }
}
