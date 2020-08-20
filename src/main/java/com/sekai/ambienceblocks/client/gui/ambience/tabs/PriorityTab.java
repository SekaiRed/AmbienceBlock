package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;

public class PriorityTab extends AbstractTab {
    public TextInstance textPriority;
    public TextFieldWidget priority;
    public TextInstance textChannel;
    public TextFieldWidget channel;

    public PriorityTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Priority";
    }

    @Override
    public void init() {
        textPriority = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.priority") + " :", font);
        addWidget(priority = new TextFieldWidget(font, getNeighbourX(textPriority), getRowY(0), 40, 20, "name"));
        priority.setValidator(ParsingUtil.numberFilter);
        priority.setMaxStringLength(2);
        priority.setText(String.valueOf(0));

        textChannel = new TextInstance(getNeighbourX(priority), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.channel") + " :", font);
        addWidget(channel = new TextFieldWidget(font, getNeighbourX(textChannel), getRowY(0), 20, 20, "name"));
        channel.setValidator(ParsingUtil.numberFilter);
        channel.setMaxStringLength(1);
        channel.setText(String.valueOf(0));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textPriority.render();
        priority.render(mouseX, mouseY, partialTicks);

        textChannel.render();
        channel.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        priority.tick();
        channel.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        priority.setText(String.valueOf(data.getPriority()));
        channel.setText(String.valueOf(data.getChannel()));
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setPriority(ParsingUtil.tryParseInt(priority.getText()));
        data.setChannel(ParsingUtil.tryParseInt(channel.getText()));
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
