package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class DelayTab extends AbstractTab {
    public TextInstance textMinDelay;
    public TextFieldWidget minDelay;

    public TextInstance textMaxDelay;
    public TextFieldWidget maxDelay;

    public CheckboxWidget canPlayOverSelf;
    public CheckboxWidget shouldStopPrevious;

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

        //canPlayOverSelf = new CheckboxButton(getBaseX(), getRowY(0) + getOffsetY())
        addWidget(canPlayOverSelf = new CheckboxWidget(getBaseX(), getRowY(1), 20 + font.getStringWidth("Can play over itself"), 20, "Can play over itself", false));
        addWidget(shouldStopPrevious = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should stop previous instance"), 20, "Should stop previous instance", false));
        shouldStopPrevious.active = false;
        shouldStopPrevious.visible = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textMinDelay.render(mouseX, mouseY);
        minDelay.render(mouseX, mouseY, partialTicks);

        textMaxDelay.render(mouseX, mouseY);
        maxDelay.render(mouseX, mouseY, partialTicks);

        canPlayOverSelf.render(mouseX, mouseY, partialTicks);
        shouldStopPrevious.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textMinDelay.isHovered()) {
            list.add(TextFormatting.RED + "Minimum Delay");
            list.add("Should be smaller than max.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textMaxDelay.isHovered()) {
            list.add(TextFormatting.RED + "Maximum Delay");
            list.add("Ticking for the delay will keep going as long as the block is loaded, so don't hesitate making this long.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(canPlayOverSelf.isHovered()) {
            list.add("If this block's delay reaches the end before a previous sound finishes playing, it won't care and play anyway when this is checked.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(shouldStopPrevious.isHovered()) {
            list.add("If this block plays over a previous sound it will stop the previous instance and play the new one.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        minDelay.tick();

        maxDelay.tick();

        shouldStopPrevious.active = canPlayOverSelf.isChecked();
        shouldStopPrevious.visible = canPlayOverSelf.isChecked();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        minDelay.setText(String.valueOf(data.getMinDelay()));
        maxDelay.setText(String.valueOf(data.getMaxDelay()));

        //setCheckBoxChecked(canPlayOverSelf, data.canPlayOverSelf());
        //setCheckBoxChecked(shouldStopPrevious, data.shouldStopPrevious());
        canPlayOverSelf.setChecked(data.canPlayOverSelf());
        shouldStopPrevious.setChecked(data.shouldStopPrevious());
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setMinDelay(ParsingUtil.tryParseInt(minDelay.getText()));
        data.setMaxDelay(ParsingUtil.tryParseInt(maxDelay.getText()));

        data.setCanPlayOverSelf(canPlayOverSelf.isChecked());
        if(data.canPlayOverSelf()) data.setShouldStopPrevious(shouldStopPrevious.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
