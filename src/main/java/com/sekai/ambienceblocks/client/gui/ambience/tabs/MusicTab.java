package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.ChooseSoundGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class MusicTab extends AbstractTab {
    public TextInstance textIntroName = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.intro") + " :", font);
    public EditBox introName = new EditBox(font, getNeighbourX(textIntroName), getRowY(0), getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation, 20, TextComponent.EMPTY);
    public Button introButton = new Button(getNeighbourX(introName), getRowY(0) + getOffsetY(20), 20, 20, new TextComponent("..."), button -> {
        Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, introName));
    });
    /*guiRef.addButton(new Button(getNeighbourX(introName), getRowY(0) + getOffsetY(20), 20, 20, new StringTextComponent("..."), button -> {
        Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, introName));
    }));*/

    public TextInstance textOutroName = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.outro") + " :", font);
    public EditBox outroName = new EditBox(font, getNeighbourX(textOutroName), getRowY(1), getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation, 20, TextComponent.EMPTY);
    public Button outroButton = new Button(getNeighbourX(outroName), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("..."), button -> {
        Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, outroName));
    });
    /*guiRef.addButton(new Button(getNeighbourX(outroName), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("..."), button -> {
        Minecraft.getInstance().setScreen(new ChooseSoundGUI(this.guiRef, outroName));
    }));*/

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
        introName.setMaxLength(50);
        addWidget(introName);
        addButton(introButton);

        outroName.setMaxLength(50);
        addWidget(outroName);
        addButton(outroButton);
        //addButton(copy);
        //addButton(paste);
        //scroll.addWidget(this);
        //addButton(test);
    }

    @Override
    public void updateWidgetPosition() {
        textIntroName.x = getBaseX(); textIntroName.y = getRowY(0) + getOffsetFontY();
        introName.x = getNeighbourX(textIntroName); introName.y = getRowY(0); introName.setWidth(getEndX() - getNeighbourX(textIntroName) - 20 - horizontalSeparation);
        introButton.x = getNeighbourX(introName); introButton.y = getRowY(0) + getOffsetY(20);

        textOutroName.x = getBaseX(); textOutroName.y = getRowY(1) + getOffsetFontY();
        outroName.x = getNeighbourX(textOutroName); outroName.y = getRowY(1); outroName.setWidth(getEndX() - getNeighbourX(textOutroName) - 20 - horizontalSeparation);
        outroButton.x = getNeighbourX(outroName); outroButton.y = getRowY(1) + getOffsetY(20);

        //copy.x = getBaseX(); copy.y = getRowY(0);
        //paste.x = getNeighbourX(copy); paste.y = getRowY(0);
        //scroll.x = getBaseX(); scroll.y = getRowY(1);
        //scroll.updateWidgetPosition();
        //test.x = getBaseX(); test.y = getRowY(2);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
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
    public void renderToolTip(PoseStack matrix, int mouseX, int mouseY) {
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
        introName.setValue(data.getIntroName());
        outroName.setValue(data.getOutroName());
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setIntroName(introName.getValue());
        data.setOutroName(outroName.getValue());
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}