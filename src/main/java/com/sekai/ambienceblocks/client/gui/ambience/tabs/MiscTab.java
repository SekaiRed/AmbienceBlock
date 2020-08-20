package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class MiscTab extends AbstractTab {
    Button copy;
    Button paste;

    //TextInstance presets;

    public MiscTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Misc";
    }

    @Override
    public void init() {
        addWidget(copy = guiRef.addButton(new Button(getBaseX(), getRowY(0) + getOffsetY(20), 60, 20, "Copy", button -> {
            AmbienceController.instance.setClipboard(guiRef.getData());
        })));

        addWidget(paste = guiRef.addButton(new Button(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 60, 20, "Paste", button -> {
            guiRef.setData(AmbienceController.instance.getClipboard());
        })));

        //presets = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Presets :", font);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        copy.render(mouseX, mouseY, partialTicks);
        paste.render(mouseX, mouseY, partialTicks);

        //presets.render();
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(copy.isHovered()) {
            list.add(TextFormatting.RED + "Copy");
            list.add("Copy the current GUIs parameters.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(paste.isHovered()) {
            list.add(TextFormatting.RED + "Paste");
            list.add("Paste the copied parameters onto this block.");
            GuiUtils.drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {

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
