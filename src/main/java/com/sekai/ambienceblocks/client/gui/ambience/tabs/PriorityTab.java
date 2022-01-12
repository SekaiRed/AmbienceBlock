package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class PriorityTab extends AbstractTab {
    public TextInstance textPriority = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.priority") + " :", font);
    public EditBox priority = new EditBox(font, getNeighbourX(textPriority), getRowY(0), 40, 20, TextComponent.EMPTY);
    public TextInstance textChannel = new TextInstance(getNeighbourX(priority), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.channel") + " :", font);
    public EditBox channel = new EditBox(font, getNeighbourX(textChannel), getRowY(0), 20, 20, TextComponent.EMPTY);

    public PriorityTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Priority";
    }

    @Override
    public String getShortName() {
        return "Prio";
    }

    @Override
    public void initialInit() {
        addWidget(priority);
        priority.setFilter(ParsingUtil.numberFilter);
        priority.setMaxLength(2);
        priority.setValue(String.valueOf(0));

        addWidget(channel);
        channel.setFilter(ParsingUtil.numberFilter);
        channel.setMaxLength(1);
        channel.setValue(String.valueOf(0));
    }

    @Override
    public void updateWidgetPosition() {
        textPriority.x = getBaseX(); textPriority.y = getRowY(0) + getOffsetFontY();
        priority.x = getNeighbourX(textPriority); priority.y = getRowY(0);
        textChannel.x = getNeighbourX(priority); textChannel.y = getRowY(0) + getOffsetFontY();
        channel.x = getNeighbourX(textChannel); channel.y = getRowY(0);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        textPriority.render(matrix, mouseX, mouseY);
        priority.render(matrix, mouseX, mouseY, partialTicks);

        textChannel.render(matrix, mouseX, mouseY);
        channel.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(PoseStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textPriority.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Priority");
            list.add("Choose this block's priority, higher is more likely to be played.");
            list.add(ChatFormatting.DARK_GRAY + "0 to 99");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textChannel.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Channel");
            list.add("Only blocks of the same channel will interact with eachother in regard to priority.");
            list.add(ChatFormatting.DARK_GRAY + "0 to 9");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
    }

    @Override
    public void tick() {
        priority.tick();
        channel.tick();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        priority.setValue(String.valueOf(data.getPriority()));
        channel.setValue(String.valueOf(data.getChannel()));
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setPriority(ParsingUtil.tryParseInt(priority.getValue()));
        data.setChannel(ParsingUtil.tryParseInt(channel.getValue()));
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
