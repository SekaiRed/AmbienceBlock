package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class MusicTab extends AbstractTab {
    public TextInstance textIntroName = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.intro") + " :", font);
    public TextFieldWidget introName = new TextFieldWidget(font, getNeighbourX(textIntroName), getRowY(0), getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation, 20, new StringTextComponent(""));
    public Button introButton = guiRef.addButton(new Button(getNeighbourX(introName), getRowY(0) + getOffsetY(20), 20, 20, new StringTextComponent("..."), button -> {
        Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef, introName));
    }));

    public TextInstance textOutroName = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.outro") + " :", font);
    public TextFieldWidget outroName = new TextFieldWidget(font, getNeighbourX(textOutroName), getRowY(1), getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation, 20, new StringTextComponent(""));
    public Button outroButton = guiRef.addButton(new Button(getNeighbourX(outroName), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("..."), button -> {
        Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this.guiRef, outroName));
    }));

    //public CheckboxWidget shouldFuse = new CheckboxWidget(getBaseX(), getRowY(2), 20 + font.getStringWidth("Should fuse") + checkboxOffset, 20, "Should fuse", false);

    /*Button copy = new Button(getBaseX(), getRowY(0) + getOffsetY(20), 60, 20, new StringTextComponent("Copy"), button -> {
        AmbienceController.instance.setClipboard(guiRef.getData());
    });
    Button paste = new Button(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 60, 20, new StringTextComponent("Paste"), button -> {
        guiRef.setData(AmbienceController.instance.getClipboard());
    });*/

    //ScrollListWidget scroll;

    /*Button test = new Button(getBaseX(), getRowY(2) + getOffsetY(20), 60, 20, "yes", button -> {
        System.out.println("gotcha");
    });

    ScrollListWidget scroll = new ScrollListWidget(getBaseX(), getRowY(1) + getOffsetY(20), 200, 20, 4, 16, StaticUtil.getListOfSoundCategories(), font, new ScrollListWidget.IPressable() {
        @Override
        public void onChange(ScrollListWidget list, int index, String name) {

        }
    });*/
    /*
     TODO: Add a preset folder that you can view trough this list
    StringListWidget musicList = new StringListWidget(getBaseX(), getRowY(1), getEndX() - getBaseX(), getEndY() - getRowY(1), 4, 16, font, null);
    TextInstance presets;
    */

    public MusicTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public String getShortName() {
        return "Mus";
    }

    @Override
    public void initialInit() {
        introName.setMaxStringLength(50);
        addWidget(introName);
        addButton(introButton);

        outroName.setMaxStringLength(50);
        addWidget(outroName);
        addButton(outroButton);
        //addButton(copy);
        //addButton(paste);
        //scroll.addWidget(this);
        //addButton(test);
    }

    @Override
    public void updateWidgetPosition() {
        textIntroName.x = getBaseX(); textIntroName.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        introName.x = getNeighbourX(textIntroName); introName.y = getRowY(0); introName.setWidth(getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation);
        introButton.x = getNeighbourX(introName); introButton.y = getRowY(0) + getOffsetY(20);

        textOutroName.x = getBaseX(); textOutroName.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        outroName.x = getNeighbourX(textOutroName); outroName.y = getRowY(1); outroName.setWidth(getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation);
        outroButton.x = getNeighbourX(outroName); outroButton.y = getRowY(1) + getOffsetY(20);

        //copy.x = getBaseX(); copy.y = getRowY(0);
        //paste.x = getNeighbourX(copy); paste.y = getRowY(0);
        //scroll.x = getBaseX(); scroll.y = getRowY(1);
        //scroll.updateWidgetPosition();
        //test.x = getBaseX(); test.y = getRowY(2);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        textIntroName.render(matrix, mouseX, mouseY);
        introName.render(matrix, mouseX, mouseY, partialTicks);
        introButton.render(matrix, mouseX, mouseY, partialTicks);

        textOutroName.render(matrix, mouseX, mouseY);
        outroName.render(matrix, mouseX, mouseY, partialTicks);
        outroButton.render(matrix, mouseX, mouseY, partialTicks);
        //copy.render(matrix, mouseX, mouseY, partialTicks);
        //paste.render(matrix, mouseX, mouseY, partialTicks);
        //test.render(mouseX, mouseY, partialTicks);
        //scroll.render(mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        /*if(copy.isHovered()) {
            list.add(TextFormatting.RED + "Copy");
            list.add("Copy the current GUIs parameters.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(paste.isHovered()) {
            list.add(TextFormatting.RED + "Paste");
            list.add("Paste the copied parameters onto this block.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }*/
    }

    @Override
    public void tick() {

    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        introName.setText(data.getIntroName());
        outroName.setText(data.getOutroName());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setIntroName(introName.getText());
        data.setOutroName(outroName.getText());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}