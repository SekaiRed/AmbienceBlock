package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.util.AmbienceType;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.music_id") + " :", font);
    public TextFieldWidget soundName = new TextFieldWidget(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName) - 20 - separation, 20, new StringTextComponent(""));
    public Button soundButton = guiRef.addButton(new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, new StringTextComponent("..."), button -> {
        Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef, soundName));
    }));

    public TextInstance textCategory = new TextInstance(0, 0, 0xFFFFFF, I18n.format("ui.ambienceblocks.category") + " :", font);
    public ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, 4, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textType = new TextInstance(0, 0, 0xFFFFFF, I18n.format("ui.ambienceblocks.type") + " :", font);
    public ScrollListWidget listType = new ScrollListWidget(0, 0, 60, 20, 4, 16, 2, StaticUtil.getListOfAmbienceType(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.volume") + " :", font);
    public TextFieldWidget soundVolume = new TextFieldWidget(font, getNeighbourX(textVolume), getRowY(1), 40, 20, new StringTextComponent(""));

    public TextInstance textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.pitch") + " :", font);
    public TextFieldWidget soundPitch = new TextFieldWidget(font, getNeighbourX(textPitch), getRowY(1), 40, 20, new StringTextComponent(""));

    public TextInstance textFadeIn = new TextInstance(0, 0, 0xFFFFFF, "Fade In :", font);
    public TextFieldWidget soundFadeIn = new TextFieldWidget(font, 0, 0, 40, 20, new StringTextComponent(""));

    public TextInstance textFadeOut = new TextInstance(0, 0, 0xFFFFFF, "Fade Out :", font);
    public TextFieldWidget soundFadeOut = new TextFieldWidget(font, 0, 0, 40, 20, new StringTextComponent(""));

    //public CheckboxWidget needRedstone = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Needs redstone") + checkboxOffset, 20, "Needs redstone", false);
    public CheckboxWidget usePriority = new CheckboxWidget(0, getRowY(2), 20 + font.getStringWidth("Using priority") + checkboxOffset, 20, "Using priority", false);
    public CheckboxWidget useCondition = new CheckboxWidget(getNeighbourX(usePriority), getRowY(2), 20 + font.getStringWidth("Using condition") + checkboxOffset, 20, "Using condition", false);
    //public CheckboxWidget alwaysPlay = new CheckboxWidget(getNeighbourX(useCondition), getRowY(2), 20 + font.getStringWidth("Always play") + checkboxOffset, 20, "Always play", false);
    public CheckboxWidget shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should fuse") + checkboxOffset, 20, "Should fuse", false);
    public CheckboxWidget useDelay = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Using delay") + checkboxOffset, 20, "Using delay", false);

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
        listCategory.addWidget(this.guiRef);
        listType.addWidget(this);
        listType.addWidget(this.guiRef);
        addWidget(soundVolume);
        addWidget(soundPitch);
        addWidget(soundFadeIn);
        addWidget(soundFadeOut);
        //addWidget(needRedstone);
        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);
        //addWidget(alwaysPlay);
    }

    @Override
    public void updateWidgetPosition() {
        textSoundName.x = getBaseX(); textSoundName.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        soundName.x = getNeighbourX(textSoundName); soundName.y = getRowY(0); soundName.setWidth(getEndX() - getNeighbourX(textSoundName) - 20 - separation);
        soundButton.x = getNeighbourX(soundName); soundButton.y = getRowY(0) + getOffsetY(20);

        textCategory.x = getBaseX(); textCategory.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        listCategory.x = getNeighbourX(textCategory); listCategory.y = getRowY(1) + getOffsetY(20);
        listCategory.updateWidgetPosition();

        textType.x = getNeighbourX(listCategory); textType.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        listType.x = getNeighbourX(textType); listType.y = getRowY(1) + getOffsetY(20);
        listType.updateWidgetPosition();

        textVolume.x = getBaseX(); textVolume.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundVolume.x = getNeighbourX(textVolume); soundVolume.y = getRowY(2);

        textPitch.x = getNeighbourX(soundVolume); textPitch.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundPitch.x = getNeighbourX(textPitch); soundPitch.y = getRowY(2);

        textFadeIn.x = getBaseX(); textFadeIn.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeIn.x = getNeighbourX(textFadeIn); soundFadeIn.y = getRowY(3);

        textFadeOut.x = getNeighbourX(soundFadeIn); textFadeOut.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeOut.x = getNeighbourX(textFadeOut); soundFadeOut.y = getRowY(3);

        //needRedstone.x = getBaseX(); needRedstone.y = getRowY(3);

        usePriority.x = getBaseX(); usePriority.y = getRowY(4);
        useCondition.x = getNeighbourX(usePriority); useCondition.y = getRowY(4);
        //alwaysPlay.x = getBaseX(); alwaysPlay.y = getRowY(5);
        shouldFuse.x = getBaseX(); shouldFuse.y = getRowY(5);
        useDelay.x = getBaseX(); useDelay.y = getRowY(5);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        boolean isMusic = AmbienceType.MUSIC.equals(getType());
        shouldFuse.active = isMusic;
        shouldFuse.visible = isMusic;
        useDelay.active = !isMusic;
        useDelay.visible = !isMusic;

        textSoundName.render(matrix, mouseX, mouseY);
        soundName.render(matrix, mouseX, mouseY, partialTicks);
        soundButton.render(matrix, mouseX, mouseY, partialTicks);

        textVolume.render(matrix, mouseX, mouseY);
        soundVolume.render(matrix, mouseX, mouseY, partialTicks);

        textPitch.render(matrix, mouseX, mouseY);
        soundPitch.render(matrix, mouseX, mouseY, partialTicks);

        textFadeIn.render(matrix, mouseX, mouseY);
        soundFadeIn.render(matrix, mouseX, mouseY, partialTicks);
        textFadeOut.render(matrix, mouseX, mouseY);
        soundFadeOut.render(matrix, mouseX, mouseY, partialTicks);

        //needRedstone.render(mouseX, mouseY, partialTicks);
        shouldFuse.render(matrix, mouseX, mouseY, partialTicks);
        useDelay.render(matrix, mouseX, mouseY, partialTicks);
        usePriority.render(matrix, mouseX, mouseY, partialTicks);
        useCondition.render(matrix, mouseX, mouseY, partialTicks);
        //alwaysPlay.render(matrix, mouseX, mouseY, partialTicks);

        textCategory.render(matrix, mouseX, mouseY);
        listCategory.render(matrix, mouseX, mouseY);
        textType.render(matrix, mouseX, mouseY);
        listType.render(matrix, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHovered()) {
            list.add(TextFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(soundButton.isHovered()) {
            list.add(TextFormatting.RED + "Tree selection");
            list.add("Select the sound in the list of loaded sounds.");
            list.add(TextFormatting.GRAY + "You first choose the namespace of the sound.");
            list.add(TextFormatting.GRAY + "Sounds are separated into folders by '.' (dot), folders are annotated with <>.");
            list.add(TextFormatting.GRAY + "Anything that isn't a folder is an actual sound, double clicking it will select it.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textCategory.isHovered()) {
            list.add(TextFormatting.RED + "Category");
            list.add(TextFormatting.GRAY + "Category the sound should play as.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textType.isHovered()) {
            list.add(TextFormatting.RED + "Type");
            list.add(TextFormatting.GRAY + "What kind of sound is it?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textVolume.isHovered()) {
            list.add(TextFormatting.RED + "Sound Volume");
            list.add(TextFormatting.DARK_GRAY + "0 to 1");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textPitch.isHovered()) {
            list.add(TextFormatting.RED + "Sound Pitch");
            list.add(TextFormatting.DARK_GRAY + "0.5 to 2");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeIn.isHovered()) {
            list.add(TextFormatting.RED + "Fade In Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach max volume when it begins?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeOut.isHovered()) {
            list.add(TextFormatting.RED + "Fade Out Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach min volume when it stops?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        /*if(needRedstone.isHovered()) {
            list.add(TextFormatting.RED + "Needs Redstone");
            list.add("This block will not play without a redstone signal.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }*/
        if(shouldFuse.isHovered()) {
            list.add(TextFormatting.RED + "Should Fuse");
            list.add("If another block is playing the same sound, the ownership will be transferred to whichever is closer.");
            list.add(TextFormatting.DARK_GRAY + "Volume is summed and pitch is averaged.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(usePriority.isHovered()) {
            list.add(TextFormatting.RED + "Using Priority");
            list.add("If another block is playing at a higher priority, this one will not play, they won't interact if they're on different channels.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useDelay.isHovered()) {
            list.add(TextFormatting.RED + "Using Delay");
            list.add("This block will wait a random amount of time before playing again, not very compatible with fusing and priority.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useCondition.isHovered()) {
            list.add(TextFormatting.RED + "Using Condition");
            list.add("This block will only play when a condition is met.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        /*if(alwaysPlay.isHovered()) {
            list.add(TextFormatting.RED + "Always play");
            list.add("This block starts playing muted as soon as it loads in to make it sound more seamless.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }*/
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
        listType.setSelectionByString(data.getType());
        //System.out.println(data.getVolume());
        //todo what the fuck is going on
        //soundVolume.setText(String.format("%.7f", data.getVolume()));
        //soundPitch.setText(String.format("%.7f", data.getPitch()));
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(4);
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        soundVolume.setText(df.format(data.getVolume()));
        soundPitch.setText(df.format(data.getPitch()));

        soundFadeIn.setText(String.valueOf(data.getFadeIn()));
        soundFadeOut.setText(String.valueOf(data.getFadeOut()));

        //needRedstone.setChecked(data.needsRedstone());
        if(AmbienceType.MUSIC.equals(getType()))
            shouldFuse.setChecked(data.shouldFuse());
        if(AmbienceType.AMBIENT.equals(getType()))
            useDelay.setChecked(data.isUsingDelay());
        usePriority.setChecked(data.isUsingPriority());
        useCondition.setChecked(data.isUsingCondition());
        //alwaysPlay.setChecked(data.shouldAlwaysPlay());
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setSoundName(soundName.getText());
        //data.setCategory(SoundCategory.valueOf(listCategory.getSelectedString()).getName());
        data.setCategory(ParsingUtil.tryParseEnum(listCategory.getSelectedString().toUpperCase(), SoundCategory.MASTER).getName());
        data.setType(ParsingUtil.tryParseEnum(listType.getSelectedString().toUpperCase(), AmbienceType.AMBIENT).getName());
        //System.out.println(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getText()));
        data.setFadeIn(ParsingUtil.tryParseInt(soundFadeIn.getText()));
        data.setFadeOut(ParsingUtil.tryParseInt(soundFadeOut.getText()));

        //data.setNeedRedstone(needRedstone.isChecked());
        if(AmbienceType.MUSIC.equals(getType()))
            data.setShouldFuse(shouldFuse.isChecked());
        if(AmbienceType.AMBIENT.equals(getType()))
            data.setUseDelay(useDelay.isChecked());
        data.setUsePriority(usePriority.isChecked());
        data.setUseCondition(useCondition.isChecked());
        //data.setAlwaysPlay(alwaysPlay.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    public AmbienceType getType() {
        return AmbienceType.valueOf(listType.getSelectedString().toUpperCase());
    }
}
