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

public class DelayTab extends AbstractTab {
    private static final String MIN_DELAY = I18n.format("ui.ambienceblocks.min") + " " + I18n.format("ui.ambienceblocks.delay");
    private static final String MAX_DELAY = I18n.format("ui.ambienceblocks.max") + " " + I18n.format("ui.ambienceblocks.delay");

    private static final String MIN_VOLUME = I18n.format("ui.ambienceblocks.min") + " " + I18n.format("ui.ambienceblocks.volume");
    private static final String MAX_VOLUME = I18n.format("ui.ambienceblocks.max") + " " + I18n.format("ui.ambienceblocks.volume");

    private static final String MIN_PITCH = I18n.format("ui.ambienceblocks.min") + " " + I18n.format("ui.ambienceblocks.pitch");
    private static final String MAX_PITCH = I18n.format("ui.ambienceblocks.max") + " " + I18n.format("ui.ambienceblocks.pitch");

    public TextInstance textMinDelay = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, MIN_DELAY + " :", font);
    public TextField minDelay = new TextField(font, getNeighbourX(textMinDelay), getRowY(0), 50, 20, new TextComponentString(""));

    public TextInstance textMaxDelay = new TextInstance(getNeighbourX(minDelay), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, MAX_DELAY + " :", font);
    public TextField maxDelay = new TextField(font, getNeighbourX(textMaxDelay), getRowY(0), 50, 20, new TextComponentString(""));

    public TextInstance textMinVolume = new TextInstance(0, 0, 0xFFFFFF, MIN_VOLUME + " :", font);
    public TextField minVolume = new TextField(font, 0, 0, 50, 20, new TextComponentString(""));

    public TextInstance textMaxVolume = new TextInstance(0, 0, 0xFFFFFF, MAX_VOLUME + " :", font);
    public TextField maxVolume = new TextField(font, 0, 0, 50, 20, new TextComponentString(""));

    public TextInstance textMinPitch = new TextInstance(0, 0, 0xFFFFFF, MIN_PITCH + " :", font);
    public TextField minPitch = new TextField(font, 0, 0, 50, 20, new TextComponentString(""));

    public TextInstance textMaxPitch = new TextInstance(0, 0, 0xFFFFFF, MAX_PITCH + " :", font);
    public TextField maxPitch = new TextField(font, 0, 0, 50, 20, new TextComponentString(""));

    /*public TextInstance textRandPos = new TextInstance(0, 0, 0xFFFFFF, "Random Pos :", font);
    public TextInstance textRandX = new TextInstance(0, 0, 0xFFFFFF, "X :", font);
    public TextField randX = new TextField(font, 0, 0, 30, 20, "name");
    public TextInstance textRandY = new TextInstance(0, 0, 0xFFFFFF, "Y :", font);
    public TextField randY = new TextField(font, 0, 0, 30, 20, "name");
    public TextInstance textRandZ = new TextInstance(0, 0, 0xFFFFFF, "Z :", font);
    public TextField randZ = new TextField(font, 0, 0, 30, 20, "name");*/

    //TODO add translation keys for checkbox desc
    public Checkbox canPlayOverSelf = new Checkbox(getBaseX(), getRowY(3), 20 + font.getStringWidth("Plays over itself"), 20, new TextComponentString("Plays over itself"), false);
    public Checkbox shouldStopPrevious = new Checkbox(getBaseX(), getRowY(4), 20 + font.getStringWidth("Stop previous"), 20, new TextComponentString("Stop previous"), false);

    public DelayTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    //TODO add automated key translation with like tab.delay.title and tab.delay.mintitle
    @Override
    public String getName() {
        return "Delay";
    }

    @Override
    public String getShortName() {
        return "Dly";
    }

    @Override
    public void initialInit() {
        minDelay.setValidator(ParsingUtil.numberFilter);
        minDelay.setMaxStringLength(8);
        minDelay.setText(String.valueOf(0));
        maxDelay.setValidator(ParsingUtil.numberFilter);
        maxDelay.setMaxStringLength(8);
        maxDelay.setText(String.valueOf(0));

        minVolume.setValidator(ParsingUtil.decimalNumberFilter);
        minVolume.setMaxStringLength(8);
        minVolume.setText(String.valueOf(0));
        maxVolume.setValidator(ParsingUtil.decimalNumberFilter);
        maxVolume.setMaxStringLength(8);
        maxVolume.setText(String.valueOf(0));

        minPitch.setValidator(ParsingUtil.decimalNumberFilter);
        minPitch.setMaxStringLength(8);
        minPitch.setText(String.valueOf(0));
        maxPitch.setValidator(ParsingUtil.decimalNumberFilter);
        maxPitch.setMaxStringLength(8);
        maxPitch.setText(String.valueOf(0));

        shouldStopPrevious.active = false;
        shouldStopPrevious.setVisible(false);
        //shouldStopPrevious.visible = false;

        addWidget(textMinDelay);
        addWidget(textMaxDelay);
        addWidget(minDelay);
        addWidget(maxDelay);

        addWidget(textMinVolume);
        addWidget(textMaxVolume);
        addWidget(minVolume);
        addWidget(maxVolume);

        addWidget(textMinPitch);
        addWidget(textMaxPitch);
        addWidget(minPitch);
        addWidget(maxPitch);

        addWidget(canPlayOverSelf);
        addWidget(shouldStopPrevious);
    }

    @Override
    public void updateWidgetPosition() {
        textMinDelay.x = getBaseX(); textMinDelay.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        minDelay.x = getNeighbourX(textMinDelay); minDelay.y = getRowY(0);
        textMaxDelay.x = getNeighbourX(minDelay); textMaxDelay.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        maxDelay.x = getNeighbourX(textMaxDelay); maxDelay.y = getRowY(0);

        textMinVolume.x = getBaseX(); textMinVolume.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        minVolume.x = getNeighbourX(textMinVolume); minVolume.y = getRowY(1);
        textMaxVolume.x = getNeighbourX(minVolume); textMaxVolume.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        maxVolume.x = getNeighbourX(textMaxVolume); maxVolume.y = getRowY(1);

        textMinPitch.x = getBaseX(); textMinPitch.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        minPitch.x = getNeighbourX(textMinPitch); minPitch.y = getRowY(2);
        textMaxPitch.x = getNeighbourX(minPitch); textMaxPitch.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        maxPitch.x = getNeighbourX(textMaxPitch); maxPitch.y = getRowY(2);

        /*textRandPos.x = getBaseX(); textRandPos.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        textRandX.x = getNeighbourX(textRandPos); textRandX.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        randX.x = getNeighbourX(textRandX); randX.y = getRowY(3);
        textRandY.x = getNeighbourX(randX); textRandY.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        randY.x = getNeighbourX(textRandX); randY.y = getRowY(3);
        textRandZ.x = getNeighbourX(randX); textRandZ.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        randZ.x = getNeighbourX(textRandX); randZ.y = getRowY(3);*/

        canPlayOverSelf.x = getBaseX(); canPlayOverSelf.y = getRowY(3);
        shouldStopPrevious.x = getNeighbourX(canPlayOverSelf); shouldStopPrevious.y = getRowY(3);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        /*AmbienceScreen.Layer layer = AmbienceScreen.Layer.LOWEST;
        
        textMinDelay.render(mouseX, mouseY, layer);
        minDelay.render(mouseX, mouseY, layer);

        textMaxDelay.render(mouseX, mouseY, layer);
        maxDelay.render(mouseX, mouseY, layer);

        textMinVolume.render(mouseX, mouseY, layer);
        minVolume.render(mouseX, mouseY, layer);
        textMaxVolume.render(mouseX, mouseY, layer);
        maxVolume.render(mouseX, mouseY, layer);
        textMinPitch.render(mouseX, mouseY, layer);
        minPitch.render(mouseX, mouseY, layer);
        textMaxPitch.render(mouseX, mouseY, layer);
        maxPitch.render(mouseX, mouseY, layer);

        canPlayOverSelf.render(mouseX, mouseY, layer);
        shouldStopPrevious.render(mouseX, mouseY, layer);*/

        /*textRandPos.render(mouseX, mouseY);
        textRandX.render(mouseX, mouseY);
        randX.render(mouseX, mouseY);
        textRandY.render(mouseX, mouseY);
        randY.render(mouseX, mouseY);
        textRandZ.render(mouseX, mouseY);
        randZ.render(mouseX, mouseY);*/
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textMinDelay.isHovered()) {
            list.add(TextFormatting.RED + "Minimum Delay");
            list.add("Should be smaller than max.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textMaxDelay.isHovered()) {
            list.add(TextFormatting.RED + "Maximum Delay");
            list.add("Ticking for the delay will keep going as long as the block is loaded, so don't hesitate making this long.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(textMinVolume.isHovered()) {
            list.add(TextFormatting.RED + "Minimum Volume");
            list.add("If the volume is random between two bounds then the lower bound is <tile's volume> - <value>");
            list.add(TextFormatting.GRAY + "0 on both min and max means that the sound won't have a random volume.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textMaxVolume.isHovered()) {
            list.add(TextFormatting.RED + "Maximum Volume");
            list.add("If the volume is random between two bounds then the upper bound is <tile's volume> + <value>");
            list.add(TextFormatting.GRAY + "0 on both min and max means that the sound won't have a random volume.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(textMinPitch.isHovered()) {
            list.add(TextFormatting.RED + "Minimum Pitch");
            list.add("If the pitch is random between two bounds then the lower bound is <tile's pitch> - <value>");
            list.add(TextFormatting.GRAY + "0 on both min and max means that the sound won't have a random pitch.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textMaxPitch.isHovered()) {
            list.add(TextFormatting.RED + "Maximum Pitch");
            list.add("If the pitch is random between two bounds then the upper bound is <tile's pitch> + <value>");
            list.add(TextFormatting.GRAY + "0 on both min and max means that the sound won't have a random pitch.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(canPlayOverSelf.isHovered()) {
            list.add("If this block's delay reaches the end before a previous sound finishes playing, it won't care and play anyway when this is checked.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(shouldStopPrevious.isHovered()) {
            list.add("If this block plays over a previous sound it will stop the previous instance and play the new one.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        /*minDelay.tick();
        maxDelay.tick();

        minVolume.tick();
        maxVolume.tick();

        minPitch.tick();
        maxPitch.tick();*/

        shouldStopPrevious.active = canPlayOverSelf.isChecked();
        shouldStopPrevious.setVisible(canPlayOverSelf.isChecked());
        //shouldStopPrevious.visible = canPlayOverSelf.isChecked();

        /*randX.tick();
        randY.tick();
        randZ.tick();*/
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        minDelay.setText(String.valueOf(data.getMinDelay()));
        maxDelay.setText(String.valueOf(data.getMaxDelay()));

        minVolume.setText(String.valueOf(data.getMinRandVolume()));
        maxVolume.setText(String.valueOf(data.getMaxRandVolume()));

        minPitch.setText(String.valueOf(data.getMinRandPitch()));
        maxPitch.setText(String.valueOf(data.getMaxRandPitch()));

        canPlayOverSelf.setChecked(data.canPlayOverSelf());
        shouldStopPrevious.setChecked(data.shouldStopPrevious());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setMinDelay(ParsingUtil.tryParseInt(minDelay.getText()));
        data.setMaxDelay(ParsingUtil.tryParseInt(maxDelay.getText()));

        data.setMinRandVolume(ParsingUtil.tryParseFloat(minVolume.getText()));
        data.setMaxRandVolume(ParsingUtil.tryParseFloat(maxVolume.getText()));

        data.setMinRandPitch(ParsingUtil.tryParseFloat(minPitch.getText()));
        data.setMaxRandPitch(ParsingUtil.tryParseFloat(maxPitch.getText()));

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
