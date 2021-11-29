package com.sekai.ambienceblocks.client.gui.ambience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class AmbienceScreen extends Screen {
    public Screen previousScreen = null;

    protected AmbienceScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    public void addWidget(Widget widget) {
        this.children.add(widget);
        if(widget instanceof Button) {
            this.buttons.add(widget);
        }
    }

    public void setPreviousScreen(Screen screen) {
        previousScreen = screen;
    }

    public void quit(Minecraft mc) {
        mc.displayGuiScreen(previousScreen);
    }
}
