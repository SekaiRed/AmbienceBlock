package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.music_id") + " :", font);
    public TextFieldWidget soundName = new TextFieldWidget(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName) - 20 - separation, 20, "name");
    public Button soundButton = guiRef.addButton(new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, "...", button -> {
        Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef, soundName));
    }));

    public TextInstance textCategory = new TextInstance(0, 0, 0xFFFFFF, I18n.format("ui.ambienceblocks.category") + " :", font);
    ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.volume") + " :", font);
    public TextFieldWidget soundVolume = new TextFieldWidget(font, getNeighbourX(textVolume), getRowY(1), 40, 20, "name");

    public TextInstance textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.pitch") + " :", font);
    public TextFieldWidget soundPitch = new TextFieldWidget(font, getNeighbourX(textPitch), getRowY(1), 40, 20, "name");

    public TextInstance textFadeIn = new TextInstance(0, 0, 0xFFFFFF, "Fade In :", font);
    public TextFieldWidget soundFadeIn = new TextFieldWidget(font, 0, 0, 40, 20, "name");

    public TextInstance textFadeOut = new TextInstance(0, 0, 0xFFFFFF, "Fade Out :", font);
    public TextFieldWidget soundFadeOut = new TextFieldWidget(font, 0, 0, 40, 20, "name");

    //public CheckboxWidget needRedstone = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Needs redstone") + checkboxOffset, 20, "Needs redstone", false);
    public CheckboxWidget shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should fuse") + checkboxOffset, 20, "Should fuse", false);
    public CheckboxWidget useDelay = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Using delay") + checkboxOffset, 20, "Using delay", false);
    public CheckboxWidget usePriority = new CheckboxWidget(getNeighbourX(shouldFuse), getRowY(2), 20 + font.getStringWidth("Using priority") + checkboxOffset, 20, "Using priority", false);
    public CheckboxWidget useCondition = new CheckboxWidget(getNeighbourX(shouldFuse), getRowY(2), 20 + font.getStringWidth("Using condition") + checkboxOffset, 20, "Using condition", false);

    public MainTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public String getShortName() {
        return "Main";
    }

    @Override
    public void initialInit() {
        soundName.setMaxStringLength(50);

        soundVolume.setValidator(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxStringLength(6);

        soundPitch.setValidator(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxStringLength(6);

        soundFadeIn.setValidator(ParsingUtil.numberFilter);
        soundFadeIn.setMaxStringLength(6);

        soundFadeOut.setValidator(ParsingUtil.numberFilter);
        soundFadeOut.setMaxStringLength(6);

        //add widgets to the list
        addWidget(soundName);
        addButton(soundButton);
        listCategory.addWidget(this);
        addWidget(soundVolume);
        addWidget(soundPitch);
        addWidget(soundFadeIn);
        addWidget(soundFadeOut);
        //addWidget(needRedstone);
        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);
    }

    @Override
    public void updateWidgetPosition() {
        textSoundName.x = getBaseX(); textSoundName.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        soundName.x = getNeighbourX(textSoundName); soundName.y = getRowY(0); soundName.setWidth(getEndX() - getNeighbourX(textSoundName) - 20 - separation);
        soundButton.x = getNeighbourX(soundName); soundButton.y = getRowY(0) + getOffsetY(20);

        textCategory.x = getBaseX(); textCategory.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        listCategory.x = getNeighbourX(textCategory); listCategory.y = getRowY(1) + getOffsetY(20);
        listCategory.updateWidgetPosition();

        textVolume.x = getBaseX(); textVolume.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundVolume.x = getNeighbourX(textVolume); soundVolume.y = getRowY(2);

        textPitch.x = getNeighbourX(soundVolume); textPitch.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundPitch.x = getNeighbourX(textPitch); soundPitch.y = getRowY(2);

        textFadeIn.x = getBaseX(); textFadeIn.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeIn.x = getNeighbourX(textFadeIn); soundFadeIn.y = getRowY(3);

        textFadeOut.x = getNeighbourX(soundFadeIn); textFadeOut.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeOut.x = getNeighbourX(textFadeOut); soundFadeOut.y = getRowY(3);

        //needRedstone.x = getBaseX(); needRedstone.y = getRowY(3);
        shouldFuse.x = getBaseX(); shouldFuse.y = getRowY(4);
        usePriority.x = getNeighbourX(shouldFuse); usePriority.y = getRowY(4);
        useDelay.x = getBaseX(); useDelay.y = getRowY(5);
        useCondition.x = getNeighbourX(useDelay); useCondition.y = getRowY(5);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textSoundName.render(mouseX, mouseY);
        soundName.render(mouseX, mouseY, partialTicks);
        soundButton.render(mouseX, mouseY, partialTicks);

        textVolume.render(mouseX, mouseY);
        soundVolume.render(mouseX, mouseY, partialTicks);

        textPitch.render(mouseX, mouseY);
        soundPitch.render(mouseX, mouseY, partialTicks);

        textFadeIn.render(mouseX, mouseY);
        soundFadeIn.render(mouseX, mouseY, partialTicks);
        textFadeOut.render(mouseX, mouseY);
        soundFadeOut.render(mouseX, mouseY, partialTicks);

        //needRedstone.render(mouseX, mouseY, partialTicks);
        shouldFuse.render(mouseX, mouseY, partialTicks);
        useDelay.render(mouseX, mouseY, partialTicks);
        usePriority.render(mouseX, mouseY, partialTicks);
        useCondition.render(mouseX, mouseY, partialTicks);

        textCategory.render(mouseX, mouseY);
        listCategory.render(mouseX, mouseY);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHovered()) {
            list.add(TextFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(soundButton.isHovered()) {
            list.add(TextFormatting.RED + "Tree selection");
            list.add("Select the sound in the list of loaded sounds.");
            list.add(TextFormatting.GRAY + "You first choose the namespace of the sound.");
            list.add(TextFormatting.GRAY + "Sounds are separated into folders by '.' (dot), folders are annotated with <>.");
            list.add(TextFormatting.GRAY + "Anything that isn't a folder is an actual sound, double clicking it will select it.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textCategory.isHovered()) {
            list.add(TextFormatting.RED + "Category");
            list.add(TextFormatting.GRAY + "Category the sound should play as.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textVolume.isHovered()) {
            list.add(TextFormatting.RED + "Sound Volume");
            list.add(TextFormatting.DARK_GRAY + "0 to 1");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textPitch.isHovered()) {
            list.add(TextFormatting.RED + "Sound Pitch");
            list.add(TextFormatting.DARK_GRAY + "0.5 to 2");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeIn.isHovered()) {
            list.add(TextFormatting.RED + "Fade In Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach max volume when it begins?");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeOut.isHovered()) {
            list.add(TextFormatting.RED + "Fade Out Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach min volume when it stops?");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        /*if(needRedstone.isHovered()) {
            list.add(TextFormatting.RED + "Needs Redstone");
            list.add("This block will not play without a redstone signal.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }*/
        if(shouldFuse.isHovered()) {
            list.add(TextFormatting.RED + "Should Fuse");
            list.add("If another block is playing the same sound, the ownership will be transferred to whichever is closer.");
            list.add(TextFormatting.DARK_GRAY + "Volume is summed and pitch is averaged.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(usePriority.isHovered()) {
            list.add(TextFormatting.RED + "Using Priority");
            list.add("If another block is playing at a higher priority, this one will not play, they won't interact if they're on different channels.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useDelay.isHovered()) {
            list.add(TextFormatting.RED + "Using Delay");
            list.add("This block will wait a random amount of time before playing again, not very compatible with fusing and priority.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useCondition.isHovered()) {
            list.add(TextFormatting.RED + "Using Condition");
            list.add("This block will only play when a condition is met.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        soundName.tick();

        soundVolume.tick();

        soundPitch.tick();

        soundFadeIn.tick();
        soundFadeOut.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        soundName.setText(data.getSoundName());
        listCategory.setSelectionByString(data.getCategory());
        soundVolume.setText(String.valueOf(data.getVolume()));
        soundPitch.setText(String.valueOf(data.getPitch()));
        soundFadeIn.setText(String.valueOf(data.getFadeIn()));
        soundFadeOut.setText(String.valueOf(data.getFadeOut()));

        //needRedstone.setChecked(data.needsRedstone());
        shouldFuse.setChecked(data.shouldFuse());
        useDelay.setChecked(data.isUsingDelay());
        usePriority.setChecked(data.isUsingPriority());
        useCondition.setChecked(data.isUsingCondition());
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setSoundName(soundName.getText());
        //data.setCategory(SoundCategory.valueOf(listCategory.getSelectedString()).getName());
        data.setCategory(ParsingUtil.tryParseEnum(listCategory.getSelectedString().toUpperCase(), SoundCategory.MASTER).getName());
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getText()));
        data.setFadeIn(ParsingUtil.tryParseInt(soundFadeIn.getText()));
        data.setFadeOut(ParsingUtil.tryParseInt(soundFadeOut.getText()));

        //data.setNeedRedstone(needRedstone.isChecked());
        data.setShouldFuse(shouldFuse.isChecked());
        data.setUseDelay(useDelay.isChecked());
        data.setUsePriority(usePriority.isChecked());
        data.setUseCondition(useCondition.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
