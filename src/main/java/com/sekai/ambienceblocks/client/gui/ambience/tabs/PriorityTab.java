package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class PriorityTab extends AbstractTab {
    public TextInstance textPriority = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.priority") + " :", font);
    public TextFieldWidget priority = new TextFieldWidget(font, getNeighbourX(textPriority), getRowY(0), 40, 20, "name");
    public TextInstance textChannel = new TextInstance(getNeighbourX(priority), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.channel") + " :", font);
    public TextFieldWidget channel = new TextFieldWidget(font, getNeighbourX(textChannel), getRowY(0), 20, 20, "name");

    public PriorityTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Priority";
    }

    @Override
    public void initialInit() {
        addWidget(priority);
        priority.setValidator(ParsingUtil.numberFilter);
        priority.setMaxStringLength(2);
        priority.setText(String.valueOf(0));

        addWidget(channel);
        channel.setValidator(ParsingUtil.numberFilter);
        channel.setMaxStringLength(1);
        channel.setText(String.valueOf(0));
    }

    @Override
    public void updateWidgetPosition() {
        textPriority.x = getBaseX(); textPriority.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        priority.x = getNeighbourX(textPriority); priority.y = getRowY(0);
        textChannel.x = getNeighbourX(priority); textChannel.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        channel.x = getNeighbourX(textChannel); channel.y = getRowY(0);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textPriority.render(mouseX, mouseY);
        priority.render(mouseX, mouseY, partialTicks);

        textChannel.render(mouseX, mouseY);
        channel.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textPriority.isHovered()) {
            list.add(TextFormatting.RED + "Priority");
            list.add("Choose this block's priority, higher is more likely to be played.");
            list.add(TextFormatting.DARK_GRAY + "0 to 99");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textChannel.isHovered()) {
            list.add(TextFormatting.RED + "Channel");
            list.add("Only blocks of the same channel will interact with eachother in regard to priority.");
            list.add(TextFormatting.DARK_GRAY + "0 to 9");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
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
