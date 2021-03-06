package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class AmbienceScreen extends Screen {
    protected AmbienceScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    public void addWidget(Widget widget) {
        this.children.add(widget);
        if(widget instanceof Button) {
            this.buttons.add(widget);
        }
    }
}
