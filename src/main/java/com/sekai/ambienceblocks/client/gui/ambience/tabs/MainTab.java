package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.util.AmbienceType;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainTab extends AbstractTab {
    public TextInstance textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.music_id") + " :", font);
    public TextField soundName = new TextField(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation, 20, new TextComponentString(""));
    public Button soundButton = new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, new TextComponentString("..."), button -> {
        Minecraft.getMinecraft().displayGuiScreen(new ChooseSoundGUI(this.screen, soundName));
    });

    public TextInstance textCategory = new TextInstance(0, 0, 0xFFFFFF, I18n.format("ui.ambienceblocks.category") + " :", font);
    /*public ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, 4, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });*/
    public ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, 4, StaticUtil.getListOfSoundCategories(), this.screen, (list, index, name) -> {

    });

    public TextInstance textType = new TextInstance(0, 0, 0xFFFFFF, I18n.format("ui.ambienceblocks.type") + " :", font);
    public ScrollListWidget listType = new ScrollListWidget(0, 0, 60, 20, 4, 16, 2, StaticUtil.getListOfAmbienceType(), this.screen, (list, index, name) -> {

    });

    public TextInstance textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.volume") + " :", font);
    public TextField soundVolume = new TextField(font, getNeighbourX(textVolume), getRowY(1), 40, 20, new TextComponentString(""));

    public TextInstance textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.pitch") + " :", font);
    public TextField soundPitch = new TextField(font, getNeighbourX(textPitch), getRowY(1), 40, 20, new TextComponentString(""));

    public TextInstance textFadeIn = new TextInstance(0, 0, 0xFFFFFF, "Fade In :", font);
    public TextField soundFadeIn = new TextField(font, 0, 0, 40, 20, new TextComponentString(""));

    public TextInstance textFadeOut = new TextInstance(0, 0, 0xFFFFFF, "Fade Out :", font);
    public TextField soundFadeOut = new TextField(font, 0, 0, 40, 20, new TextComponentString(""));

    //public Checkbox needRedstone = new Checkbox(getBaseX(), getRowY(3), 20 + font.getStringWidth("Needs redstone") + checkboxOffset, 20, "Needs redstone", false);
    public Checkbox usePriority = new Checkbox(0, getRowY(2), 20 + font.getStringWidth("Using priority") + checkboxOffset, 20, new TextComponentString("Using priority"), false);
    public Checkbox useCondition = new Checkbox(getNeighbourX(usePriority), getRowY(2), 20 + font.getStringWidth("Using condition") + checkboxOffset, 20, new TextComponentString("Using condition"), false);
    //public Checkbox alwaysPlay = new Checkbox(getNeighbourX(useCondition), getRowY(2), 20 + font.getStringWidth("Always play") + checkboxOffset, 20, "Always play", false);
    public Checkbox shouldFuse = new Checkbox(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should fuse") + checkboxOffset, 20, new TextComponentString("Should fuse"), false);
    public Checkbox useDelay = new Checkbox(getBaseX(), getRowY(3), 20 + font.getStringWidth("Using delay") + checkboxOffset, 20, new TextComponentString("Using delay"), false);

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
        addWidget(textSoundName);
        addWidget(soundName);
        soundName.setMaxStringLength(50);
        addWidget(soundButton);

        addWidget(textVolume);
        addWidget(soundVolume);
        soundVolume.setValidator(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxStringLength(6);

        addWidget(textPitch);
        addWidget(soundPitch);
        soundPitch.setValidator(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxStringLength(6);

        addWidget(textType);
        addWidget(listType);
        listType.setLayer(AmbienceScreen.Layer.HIGHEST);

        addWidget(textCategory);
        addWidget(listCategory);
        listCategory.setLayer(AmbienceScreen.Layer.HIGHEST);

        addWidget(textFadeIn);
        addWidget(soundFadeIn);
        soundFadeIn.setValidator(ParsingUtil.numberFilter);
        soundFadeIn.setMaxStringLength(6);

        addWidget(textFadeOut);
        addWidget(soundFadeOut);
        soundFadeOut.setValidator(ParsingUtil.numberFilter);
        soundFadeOut.setMaxStringLength(6);

        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);

        //add widgets to the list
        //addButton(soundButton);

        //technically not needed anymore, it automatically registers itself
        /*listCategory.addWidget(this);
        listCategory.addWidget(this.screen);
        listType.addWidget(this);
        listType.addWidget(this.screen);*/

        /*addWidget(soundVolume);
        addWidget(soundPitch);
        addWidget(soundFadeIn);
        addWidget(soundFadeOut);
        //addWidget(needRedstone);
        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);
        //addWidget(alwaysPlay);*/
    }

    @Override
    public void updateWidgetPosition() {
        textSoundName.x = getBaseX(); textSoundName.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        soundName.x = getNeighbourX(textSoundName); soundName.y = getRowY(0); soundName.width = getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation;
        soundName.forceUpdatePosition();
        soundButton.x = getNeighbourX(soundName); soundButton.y = getRowY(0) + getOffsetY(20);

        textCategory.x = getBaseX(); textCategory.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        listCategory.x = getNeighbourX(textCategory); listCategory.y = getRowY(1) + getOffsetY(20);
        listCategory.forceUpdatePosition();
        listCategory.updateWidgetPosition();

        textType.x = getNeighbourX(listCategory); textType.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        listType.x = getNeighbourX(textType); listType.y = getRowY(1) + getOffsetY(20);
        listType.forceUpdatePosition();
        listType.updateWidgetPosition();

        textVolume.x = getBaseX(); textVolume.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundVolume.x = getNeighbourX(textVolume); soundVolume.y = getRowY(2);
        soundVolume.forceUpdatePosition();

        textPitch.x = getNeighbourX(soundVolume); textPitch.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        soundPitch.x = getNeighbourX(textPitch); soundPitch.y = getRowY(2);
        soundPitch.forceUpdatePosition();

        textFadeIn.x = getBaseX(); textFadeIn.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeIn.x = getNeighbourX(textFadeIn); soundFadeIn.y = getRowY(3);
        soundFadeIn.forceUpdatePosition();

        textFadeOut.x = getNeighbourX(soundFadeIn); textFadeOut.y = getRowY(3) + getOffsetY(font.FONT_HEIGHT);
        soundFadeOut.x = getNeighbourX(textFadeOut); soundFadeOut.y = getRowY(3);
        soundFadeOut.forceUpdatePosition();

        //needRedstone.x = getBaseX(); needRedstone.y = getRowY(3);

        usePriority.x = getBaseX(); usePriority.y = getRowY(4);
        useCondition.x = getNeighbourX(usePriority); useCondition.y = getRowY(4);
        //alwaysPlay.x = getBaseX(); alwaysPlay.y = getRowY(5);
        shouldFuse.x = getBaseX(); shouldFuse.y = getRowY(5);
        useDelay.x = getBaseX(); useDelay.y = getRowY(5);

    }

    @Override
    public void render(int mouseX, int mouseY) {
        AmbienceScreen.Layer layer = AmbienceScreen.Layer.LOWEST;

        boolean isMusic = AmbienceType.MUSIC.equals(getType());
        shouldFuse.active = isMusic;
        shouldFuse.setVisible(isMusic);
        //shouldFuse.visible = isMusic;
        useDelay.active = !isMusic;
        useDelay.setVisible(!isMusic);
        //useDelay.visible = !isMusic;

        /* TODO do I need to remove render or tick?

        textSoundName.render(mouseX, mouseY, layer);
        soundName.render(mouseX, mouseY, layer);
        soundButton.render(mouseX, mouseY, layer);

        textVolume.render(mouseX, mouseY, layer);
        soundVolume.render(mouseX, mouseY, layer);

        textPitch.render(mouseX, mouseY, layer);
        soundPitch.render(mouseX, mouseY, layer);

        textFadeIn.render(mouseX, mouseY, layer);
        soundFadeIn.render(mouseX, mouseY, layer);
        textFadeOut.render(mouseX, mouseY, layer);
        soundFadeOut.render(mouseX, mouseY, layer);

        //needRedstone.render(mouseX, mouseY);
        shouldFuse.render(mouseX, mouseY, layer);
        useDelay.render(mouseX, mouseY, layer);
        usePriority.render(mouseX, mouseY, layer);
        useCondition.render(mouseX, mouseY, layer);
        //alwaysPlay.render(mouseX, mouseY);

        textCategory.render(mouseX, mouseY, layer);
        listCategory.render(mouseX, mouseY, layer);
        textType.render(mouseX, mouseY, layer);
        listType.render(mouseX, mouseY, layer);*/
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHovered()) {
            list.add(TextFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(soundButton.isHovered()) {
            list.add(TextFormatting.RED + "Tree selection");
            list.add("Select the sound in the list of loaded sounds.");
            list.add(TextFormatting.GRAY + "You first choose the namespace of the sound.");
            list.add(TextFormatting.GRAY + "Sounds are separated into folders by '.' (dot), folders are annotated with <>.");
            list.add(TextFormatting.GRAY + "Anything that isn't a folder is an actual sound, double clicking it will select it.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textCategory.isHovered()) {
            list.add(TextFormatting.RED + "Category");
            list.add(TextFormatting.GRAY + "Category the sound should play as.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textType.isHovered()) {
            list.add(TextFormatting.RED + "Type");
            list.add(TextFormatting.GRAY + "What kind of sound is it?");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textVolume.isHovered()) {
            list.add(TextFormatting.RED + "Sound Volume");
            list.add(TextFormatting.DARK_GRAY + "0 to 1");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textPitch.isHovered()) {
            list.add(TextFormatting.RED + "Sound Pitch");
            list.add(TextFormatting.DARK_GRAY + "0.5 to 2");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeIn.isHovered()) {
            list.add(TextFormatting.RED + "Fade In Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach max volume when it begins?");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(textFadeOut.isHovered()) {
            list.add(TextFormatting.RED + "Fade Out Time");
            list.add(TextFormatting.GRAY + "How long should it take for this sound to reach min volume when it stops?");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
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
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(usePriority.isHovered()) {
            list.add(TextFormatting.RED + "Using Priority");
            list.add("If another block is playing at a higher priority, this one will not play, they won't interact if they're on different channels.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useDelay.isHovered()) {
            list.add(TextFormatting.RED + "Using Delay");
            list.add("This block will wait a random amount of time before playing again, not very compatible with fusing and priority.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(useCondition.isHovered()) {
            list.add(TextFormatting.RED + "Using Condition");
            list.add("This block will only play when a condition is met.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        /*if(alwaysPlay.isHovered()) {
            list.add(TextFormatting.RED + "Always play");
            list.add("This block starts playing muted as soon as it loads in to make it sound more seamless.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }*/
    }

    @Override
    public void tick() {
        /*soundName.tick();

        soundVolume.tick();

        soundPitch.tick();

        soundFadeIn.tick();
        soundFadeOut.tick();*/
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
