package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class MiscTab extends AbstractTab {
    Button copy = new Button(getBaseX(), getRowY(0) + getOffsetY(20), 60, 20, new StringTextComponent("Copy"), button -> {
        AmbienceController.instance.setClipboard(guiRef.getData());
    });
    Button paste = new Button(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 60, 20, new StringTextComponent("Paste"), button -> {
        guiRef.setData(AmbienceController.instance.getClipboard());
    });

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

    public MiscTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Misc";
    }

    @Override
    public String getShortName() {
        return "Misc";
    }

    @Override
    public void initialInit() {
        addButton(copy);
        addButton(paste);
        //scroll.addWidget(this);
        //addButton(test);
    }

    @Override
    public void updateWidgetPosition() {
        copy.x = getBaseX(); copy.y = getRowY(0);
        paste.x = getNeighbourX(copy); paste.y = getRowY(0);
        //scroll.x = getBaseX(); scroll.y = getRowY(1);
        //scroll.updateWidgetPosition();
        //test.x = getBaseX(); test.y = getRowY(2);
    }

    public void doubleClicked() {
        System.out.println("double clicked");
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        copy.render(matrix, mouseX, mouseY, partialTicks);
        paste.render(matrix, mouseX, mouseY, partialTicks);
        //test.render(mouseX, mouseY, partialTicks);
        //scroll.render(mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(copy.isHovered()) {
            list.add(TextFormatting.RED + "Copy");
            list.add("Copy the current GUIs parameters.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(paste.isHovered()) {
            list.add(TextFormatting.RED + "Paste");
            list.add("Paste the copied parameters onto this block.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        /*if(musicList.scrollIndex < 1f)
            musicList.scrollIndex += 0.01f;
        else
            musicList.scrollIndex = 0f;*/
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {

    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {

    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
