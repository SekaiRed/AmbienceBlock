package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MainTab extends AbstractTab {
    public TextInstance textSoundName;
    public TextFieldWidget soundName;
    //public Button soundButton;

    public TextInstance textVolume;
    public TextFieldWidget soundVolume;

    public TextInstance textPitch;
    public TextFieldWidget soundPitch;

    public CheckboxWidget shouldFuse;
    public CheckboxWidget useDelay;
    public CheckboxWidget usePriority;
    public CheckboxWidget needRedstone;

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
        /*addWidget(soundButton = guiRef.addButton(new Button(getNeighbourX(soundName), getRowY(0) + getOffsetY(20), 20, 20, "...", button -> {
            Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef));
        })));*/

        textVolume = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.volume") + " :", font);
        addWidget(soundVolume = new TextFieldWidget(font, getNeighbourX(textVolume), getRowY(1), 40, 20, "name"));
        soundVolume.setValidator(ParsingUtil.decimalNumberFilter);
        soundVolume.setMaxStringLength(6);

        textPitch = new TextInstance(getNeighbourX(soundVolume), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.pitch") + " :", font);
        addWidget(soundPitch = new TextFieldWidget(font, getNeighbourX(textPitch), getRowY(1), 40, 20, "name"));
        soundPitch.setValidator(ParsingUtil.decimalNumberFilter);
        soundPitch.setMaxStringLength(6);

        addWidget(shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should fuse"), 20, "Should fuse", false));
        addWidget(usePriority = new CheckboxWidget(getNeighbourX(shouldFuse), getRowY(2), 20 + font.getStringWidth("Using priority"), 20, "Using priority", false));
        addWidget(useDelay = new CheckboxWidget(getBaseX(), getRowY(3), 20 + font.getStringWidth("Using delay"), 20, "Using delay", false));
        addWidget(needRedstone = new CheckboxWidget(getNeighbourX(useDelay), getRowY(3), 20 + font.getStringWidth("Needs redstone"), 20, "Needs redstone", false));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textSoundName.render(mouseX, mouseY);
        soundName.render(mouseX, mouseY, partialTicks);
        //soundButton.render(mouseX, mouseY, partialTicks);

        textVolume.render(mouseX, mouseY);
        soundVolume.render(mouseX, mouseY, partialTicks);

        textPitch.render(mouseX, mouseY);
        soundPitch.render(mouseX, mouseY, partialTicks);

        shouldFuse.render(mouseX, mouseY, partialTicks);
        useDelay.render(mouseX, mouseY, partialTicks);
        usePriority.render(mouseX, mouseY, partialTicks);
        needRedstone.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(textSoundName.isHovered()) {
            list.add(TextFormatting.RED + "Sound ID");
            list.add("Choose the sound to play.");
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
        if(needRedstone.isHovered()) {
            list.add(TextFormatting.RED + "Needs Redstone");
            list.add("This block will not play without a redstone signal.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
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

        shouldFuse.setChecked(data.shouldFuse());
        useDelay.setChecked(data.isUsingDelay());
        usePriority.setChecked(data.isUsingPriority());
        needRedstone.setChecked(data.needsRedstone());
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setSoundName(soundName.getText());
        data.setVolume(ParsingUtil.tryParseFloat(soundVolume.getText()));
        data.setPitch(ParsingUtil.tryParseFloat(soundPitch.getText()));

        data.setShouldFuse(shouldFuse.isChecked());
        data.setUseDelay(useDelay.isChecked());
        data.setUsePriority(usePriority.isChecked());
        data.setNeedRedstone(needRedstone.isChecked());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
