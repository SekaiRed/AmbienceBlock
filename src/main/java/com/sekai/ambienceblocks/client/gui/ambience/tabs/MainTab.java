package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.music_id") + " :", font);
    public EditBox soundName = new EditBox(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation, 20, new TextComponent(""));
    public Button soundButton = new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, new TextComponent("..."), button -> {
        Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, soundName));
    });
    /*guiRef.addButton(new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, new TextComponent("..."), button -> {
        Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef, soundName));
    }));*/

    public TextInstance textCategory = new TextInstance(0, 0, 0xFFFFFF, I18n.get("ui.ambienceblocks.category") + " :", font);
    public ScrollListWidget listCategory = new ScrollListWidget(0, 0, 60, 20, 4, 16, 4, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textType = new TextInstance(0, 0, 0xFFFFFF, I18n.get("ui.ambienceblocks.type") + " :", font);
    public ScrollListWidget listType = new ScrollListWidget(0, 0, 60, 20, 4, 16, 2, StaticUtil.getListOfAmbienceType(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });

    public TextInstance textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.volume") + " :", font);
    public EditBox soundVolume = new EditBox(font, getNeighbourX(textVolume), getRowY(1), 40, 20, TextComponent.EMPTY);

    public TextInstance textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.pitch") + " :", font);
    public EditBox soundPitch = new EditBox(font, getNeighbourX(textPitch), getRowY(1), 40, 20, TextComponent.EMPTY);

    public TextInstance textFadeIn = new TextInstance(0, 0, 0xFFFFFF, "Fade In :", font);
    public EditBox soundFadeIn = new EditBox(font, 0, 0, 40, 20, TextComponent.EMPTY);

    public TextInstance textFadeOut = new TextInstance(0, 0, 0xFFFFFF, "Fade Out :", font);
    public EditBox soundFadeOut = new EditBox(font, 0, 0, 40, 20, TextComponent.EMPTY);

    public TextInstance textTag = new TextInstance(0, 0, 0xFFFFFF, "Tag :", font);
    public EditBox tag = new EditBox(font, 0, 0, 40, 20, TextComponent.EMPTY);
    //public CheckboxWidget needRedstone = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Needs redstone") + checkboxOffset, 20, "Needs redstone", false);
    public CheckboxWidget usePriority = new CheckboxWidget(0, getRowY(2), 20 + font.width("Priority") + checkboxOffset, 20, "Priority", false);
    public CheckboxWidget useCondition = new CheckboxWidget(getNeighbourX(usePriority), getRowY(2), 20 + font.width("Condition") + checkboxOffset, 20, "Condition", false);
    //public CheckboxWidget alwaysPlay = new CheckboxWidget(getNeighbourX(useCondition), getRowY(2), 20 + font.getStringWidth("Always play") + checkboxOffset, 20, "Always play", false);
    public CheckboxWidget shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.width("Fuse") + checkboxOffset, 20, "Fuse", false);
    public CheckboxWidget useDelay = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.width("Delay") + checkboxOffset, 20, "Delay", false);

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
        soundName.setMaxLength(StaticUtil.LENGTH_SOUND);

        soundVolume.setFilter(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxLength(6);

        soundPitch.setFilter(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxLength(6);

        soundFadeIn.setFilter(ParsingUtil.numberFilter);
        soundFadeIn.setMaxLength(6);

        soundFadeOut.setFilter(ParsingUtil.numberFilter);
        soundFadeOut.setMaxLength(6);

        tag.setMaxLength(5);

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
        addWidget(tag);
        //addWidget(needRedstone);
        addWidget(shouldFuse);
        addWidget(usePriority);
        addWidget(useDelay);
        addWidget(useCondition);
        //addWidget(alwaysPlay);
    }

    @Override
    public void updateWidgetPosition() {
        textSoundName.x = getBaseX(); textSoundName.y = getRowY(0) + getOffsetFontY();
        soundName.x = getNeighbourX(textSoundName); soundName.y = getRowY(0); soundName.setWidth(getEndX() - getNeighbourX(textSoundName) - 20 - horizontalSeparation);
        soundButton.x = getNeighbourX(soundName); soundButton.y = getRowY(0) + getOffsetY(20);

        textCategory.x = getBaseX(); textCategory.y = getRowY(1) + getOffsetFontY();
        listCategory.x = getNeighbourX(textCategory); listCategory.y = getRowY(1) + getOffsetY(20);
        listCategory.updateWidgetPosition();

        textType.x = getNeighbourX(listCategory); textType.y = getRowY(1) + getOffsetFontY();
        listType.x = getNeighbourX(textType); listType.y = getRowY(1) + getOffsetY(20);
        listType.updateWidgetPosition();

        textVolume.x = getBaseX(); textVolume.y = getRowY(2) + getOffsetFontY();
        soundVolume.x = getNeighbourX(textVolume); soundVolume.y = getRowY(2);

        textPitch.x = getNeighbourX(soundVolume); textPitch.y = getRowY(2) + getOffsetFontY();
        soundPitch.x = getNeighbourX(textPitch); soundPitch.y = getRowY(2);

        textFadeIn.x = getBaseX(); textFadeIn.y = getRowY(3) + getOffsetFontY();
        soundFadeIn.x = getNeighbourX(textFadeIn); soundFadeIn.y = getRowY(3);

        textFadeOut.x = getNeighbourX(soundFadeIn); textFadeOut.y = getRowY(3) + getOffsetFontY();
        soundFadeOut.x = getNeighbourX(textFadeOut); soundFadeOut.y = getRowY(3);

        //needRedstone.x = getBaseX(); needRedstone.y = getRowY(3);
        textTag.x = getBaseX(); textTag.y = getRowY(4) + getOffsetFontY();
        tag.x = getNeighbourX(textTag); tag.y = getRowY(4);

        usePriority.x = getNeighbourX(tag); usePriority.y = getRowY(4);
        useCondition.x = getNeighbourX(usePriority); useCondition.y = getRowY(4);
        //alwaysPlay.x = getBaseX(); alwaysPlay.y = getRowY(5);
        shouldFuse.x = getBaseX(); shouldFuse.y = getRowY(5);
        useDelay.x = getBaseX(); useDelay.y = getRowY(5);

    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
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

        textTag.render(matrix, mouseX, mouseY, partialTicks);
        tag.render(matrix, mouseX, mouseY, partialTicks);

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
    public void renderToolTip(PoseStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHovered()) {
            list.add(ChatFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(soundButton.isHovered()) {
            list.add(ChatFormatting.RED + "Tree selection");
            list.add("Select the sound in the list of loaded sounds.");
            list.add(ChatFormatting.GRAY + "You first choose the namespace of the sound.");
            list.add(ChatFormatting.GRAY + "Sounds are separated into folders by '.' (dot), folders are annotated with <>.");
            list.add(ChatFormatting.GRAY + "Anything that isn't a folder is an actual sound, double clicking it will select it.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textCategory.isHovered()) {
            list.add(ChatFormatting.RED + "Category");
            list.add(ChatFormatting.GRAY + "Category the sound should play as.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textType.isHovered()) {
            list.add(ChatFormatting.RED + "Type");
            list.add(ChatFormatting.GRAY + "What kind of sound is it?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textVolume.isHovered()) {
            list.add(ChatFormatting.RED + "Sound Volume");
            list.add(ChatFormatting.DARK_GRAY + "0 to 1");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textPitch.isHovered()) {
            list.add(ChatFormatting.RED + "Sound Pitch");
            list.add(ChatFormatting.DARK_GRAY + "0.5 to 2");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textFadeIn.isHovered()) {
            list.add(ChatFormatting.RED + "Fade In Time");
            list.add(ChatFormatting.GRAY + "How long should it take for this sound to reach max volume when it begins?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textFadeOut.isHovered()) {
            list.add(ChatFormatting.RED + "Fade Out Time");
            list.add(ChatFormatting.GRAY + "How long should it take for this sound to reach min volume when it stops?");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(textTag.isHovered()) {
            list.add(ChatFormatting.RED + "Tag");
            list.add(ChatFormatting.GRAY + "Can identify one or multiple blocks with conditions.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        /*if(needRedstone.isHovered()) {
            list.add(ChatFormatting.RED + "Needs Redstone");
            list.add("This block will not play without a redstone signal.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }*/
        if(shouldFuse.isHovered()) {
            list.add(ChatFormatting.RED + "Should Fuse");
            list.add("If another block is playing the same sound, the ownership will be transferred to whichever is closer.");
            list.add(ChatFormatting.DARK_GRAY + "Volume is summed and pitch is averaged.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(usePriority.isHovered()) {
            list.add(ChatFormatting.RED + "Using Priority");
            list.add("If another block is playing at a higher priority, this one will not play, they won't interact if they're on different channels.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(useDelay.isHovered()) {
            list.add(ChatFormatting.RED + "Using Delay");
            list.add("This block will wait a random amount of time before playing again, not very compatible with fusing and priority.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        if(useCondition.isHovered()) {
            list.add(ChatFormatting.RED + "Using Condition");
            list.add("This block will only play when a condition is met.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }
        /*if(alwaysPlay.isHovered()) {
            list.add(ChatFormatting.RED + "Always play");
            list.add("This block starts playing muted as soon as it loads in to make it sound more seamless.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }*/
    }

    @Override
    public void tick() {
        soundName.tick();

        soundVolume.tick();

        soundPitch.tick();

        soundFadeIn.tick();
        soundFadeOut.tick();

        tag.tick();
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        soundName.setValue(data.getSoundName());
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
        soundVolume.setValue(df.format(data.getVolume()));
        soundPitch.setValue(df.format(data.getPitch()));

        soundFadeIn.setValue(String.valueOf(data.getFadeIn()));
        soundFadeOut.setValue(String.valueOf(data.getFadeOut()));

        tag.setValue(data.getTag());

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
    public void setDataFromField(AmbienceData data) {
        data.setSoundName(soundName.getValue());
        //data.setCategory(SoundCategory.valueOf(listCategory.getSelectedString()).getName());
        data.setCategory(ParsingUtil.tryParseEnum(listCategory.getSelectedString().toUpperCase(), SoundSource.MASTER).getName());
        data.setType(ParsingUtil.tryParseEnum(listType.getSelectedString().toUpperCase(), AmbienceType.AMBIENT).getName());
        //System.out.println(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getValue()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getValue()));
        data.setFadeIn(ParsingUtil.tryParseInt(soundFadeIn.getValue()));
        data.setFadeOut(ParsingUtil.tryParseInt(soundFadeOut.getValue()));

        data.setTag(tag.getValue());

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
