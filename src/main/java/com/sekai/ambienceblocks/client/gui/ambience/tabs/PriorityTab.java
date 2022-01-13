package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class PriorityTab extends AbstractTab {
    public TextInstance textPriority = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.priority") + " :", font);
    public TextField priority = new TextField(font, getNeighbourX(textPriority), getRowY(0), 40, 20, new TextComponentString(""));
    public TextInstance textChannel = new TextInstance(getNeighbourX(priority), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.channel") + " :", font);
    public TextField channel = new TextField(font, getNeighbourX(textChannel), getRowY(0), 20, 20, new TextComponentString(""));

    public Checkbox allowSamePriority = new Checkbox(getBaseX(), getRowY(1), 20 + font.getStringWidth("Can play at the same priority"), 20, new TextComponentString("Can play at the same priority"), true);

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
        addWidget(textPriority);
        addWidget(priority);
        priority.setValidator(ParsingUtil.numberFilter);
        priority.setMaxStringLength(2);
        priority.setText(String.valueOf(0));

        addWidget(textChannel);
        addWidget(channel);
        channel.setValidator(ParsingUtil.numberFilter);
        channel.setMaxStringLength(1);
        channel.setText(String.valueOf(0));

        addWidget(allowSamePriority);
    }

    @Override
    public void updateWidgetPosition() {
        textPriority.x = getBaseX(); textPriority.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        priority.x = getNeighbourX(textPriority); priority.y = getRowY(0);
        textChannel.x = getNeighbourX(priority); textChannel.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        channel.x = getNeighbourX(textChannel); channel.y = getRowY(0);
        allowSamePriority.x = getBaseX(); allowSamePriority.y = getRowY(1);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        /*textPriority.render(matrix, mouseX, mouseY);
        priority.render(matrix, mouseX, mouseY, partialTicks);

        textChannel.render(matrix, mouseX, mouseY);
        channel.render(matrix, mouseX, mouseY, partialTicks);*/
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textPriority.isHovered()) {
            list.add(TextFormatting.RED + "Priority");
            list.add("Choose this block's priority, higher is more likely to be played.");
            list.add(TextFormatting.DARK_GRAY + "0 to 99");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textChannel.isHovered()) {
            list.add(TextFormatting.RED + "Channel");
            list.add("Only blocks of the same channel will interact with eachother in regard to priority.");
            list.add(TextFormatting.DARK_GRAY + "0 to 9");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(allowSamePriority.isHovered()) {
            list.add(TextFormatting.RED + "Can play at the same priority");
            list.add("Should this ambience source be able to play if another one has the same priority.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        priority.tick();
        channel.tick();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        priority.setText(String.valueOf(data.getPriority()));
        channel.setText(String.valueOf(data.getChannel()));
        allowSamePriority.setChecked(data.canPlayAtSamePriority());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setPriority(ParsingUtil.tryParseInt(priority.getText()));
        data.setChannel(ParsingUtil.tryParseInt(channel.getText()));
        data.setCanPlayAtSamePriority(allowSamePriority.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
