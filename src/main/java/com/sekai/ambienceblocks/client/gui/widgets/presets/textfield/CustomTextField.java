package com.sekai.ambienceblocks.client.gui.widgets.presets.textfield;

import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class CustomTextField extends TextFieldWidget {
    public CustomTextField(int xIn, int yIn, int widthIn, int heightIn, String msg) {
        super(Minecraft.getInstance().fontRenderer, xIn, yIn, widthIn, heightIn, new StringTextComponent(msg));
    }

    public void filterDecimalNumberFilter() {
        this.setValidator(ParsingUtil.decimalNumberFilter);
    }

    /*cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);*/
}
