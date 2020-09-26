package com.sekai.ambienceblocks.client.gui.widgets.presets.textfield;

import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class CustomTextField extends TextFieldWidget {
    public CustomTextField(int xIn, int yIn, int widthIn, int heightIn, String msg) {
        super(Minecraft.getInstance().fontRenderer, xIn, yIn, widthIn, heightIn, msg);
    }

    public void filterDecimalNumberFilter() {
        this.setValidator(ParsingUtil.decimalNumberFilter);
    }

    /*cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);*/
}
