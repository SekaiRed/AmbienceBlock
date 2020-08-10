package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName;
    public TextFieldWidget soundName;

    public TextInstance textVolume;
    public TextFieldWidget soundVolume;

    public TextInstance textPitch;
    public TextFieldWidget soundPitch;

    public CheckboxButton useDelay;

    public MainTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public void init() {
        textSoundName = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.music_id") + " :", font);
        addWidget(soundName = new TextFieldWidget(font, getNeighbourX(textSoundName), getRowY(0), getEndX() - getNeighbourX(textSoundName), 20, "name"));
        soundName.setMaxStringLength(50);

        textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.volume") + " :", font);
        addWidget(soundVolume = new TextFieldWidget(font, getNeighbourX(textVolume), getRowY(1), 40, 20, "name"));
        soundVolume.setValidator(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxStringLength(6);

        textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.pitch") + " :", font);
        addWidget(soundPitch = new TextFieldWidget(font, getNeighbourX(textPitch), getRowY(1), 40, 20, "name"));
        soundPitch.setValidator(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxStringLength(6);

        addWidget(useDelay = new CheckboxButton(getBaseX(), getRowY(2), 20 + font.getStringWidth("Using delay"), 20, "Using delay", false));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textSoundName.render();
        soundName.render(mouseX, mouseY, partialTicks);

        textVolume.render();
        soundVolume.render(mouseX, mouseY, partialTicks);

        textPitch.render();
        soundPitch.render(mouseX, mouseY, partialTicks);

        useDelay.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        soundName.tick();

        soundVolume.tick();

        soundPitch.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        soundName.setText(data.getSoundName());
        soundVolume.setText(String.valueOf(data.getVolume()));
        soundPitch.setText(String.valueOf(data.getPitch()));

        setCheckBoxChecked(useDelay, data.isUsingDelay());
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setSoundName(soundName.getText());
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getText()));
    }

    public void setCheckBoxChecked(CheckboxButton check, boolean b) {
        if(b && !check.isChecked())
            check.onPress();
        if(!b && check.isChecked())
            check.onPress();
    }
}
