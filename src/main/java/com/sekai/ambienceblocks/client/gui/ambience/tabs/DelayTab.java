package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;

public class DelayTab extends AbstractTab {
    public TextInstance textMinDelay;
    public TextFieldWidget minDelay;

    public TextInstance textMaxDelay;
    public TextFieldWidget maxDelay;

    public DelayTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Delay";
    }

    @Override
    public void init() {
        textMinDelay = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.min") + " :", font);
        addWidget(minDelay = new TextFieldWidget(font, getNeighbourX(textMinDelay), getRowY(0), 50, 20, "name"));
        minDelay.setValidator(ParsingUtil.numberFilter);
        minDelay.setMaxStringLength(8);

        textMaxDelay = new TextInstance(getNeighbourX(minDelay), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.max") + " :", font);
        addWidget(maxDelay = new TextFieldWidget(font, getNeighbourX(textMaxDelay), getRowY(0), 50, 20, "name"));
        maxDelay.setValidator(ParsingUtil.numberFilter);
        maxDelay.setMaxStringLength(8);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textMinDelay.render();
        minDelay.render(mouseX, mouseY, partialTicks);

        textMaxDelay.render();
        maxDelay.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        minDelay.tick();

        maxDelay.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        minDelay.setText(String.valueOf(data.getMinDelay()));
        maxDelay.setText(String.valueOf(data.getMaxDelay()));
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setMinDelay(ParsingUtil.tryParseInt(minDelay.getText()));
        data.setMaxDelay(ParsingUtil.tryParseInt(maxDelay.getText()));
    }
}
