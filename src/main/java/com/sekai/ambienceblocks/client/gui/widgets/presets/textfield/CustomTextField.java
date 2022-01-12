package com.sekai.ambienceblocks.client.gui.widgets.presets.textfield;

import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class CustomTextField extends EditBox {
    public CustomTextField(int xIn, int yIn, int widthIn, int heightIn, String msg) {
        super(Minecraft.getInstance().font, xIn, yIn, widthIn, heightIn, new TextComponent(msg));
    }

    public void filterDecimalNumberFilter() {
        this.setFilter(ParsingUtil.decimalNumberFilter);
    }

    /*cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);*/
}
