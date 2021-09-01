package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.client.rendering.RenderingEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class MiscTab extends AbstractTab {
    Button copy = new Button(getBaseX(), getRowY(0) + getOffsetY(20), 60, 20, new TextComponentString("Copy"), button -> {
        AmbienceController.instance.setClipboard(screen.getData());
    });
    Button paste = new Button(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 60, 20, new TextComponentString("Paste"), button -> {
        screen.setData(AmbienceController.instance.getClipboard());
    });

    TextInstance tDebug = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("Debug :"), font);
    Button bDebug = new Button(getNeighbourX(copy), getRowY(0) + getOffsetY(20), 40, 20, new TextComponentString("OFF"), button -> {
        if(AmbienceController.debugMode)
            button.setMessage(new TextComponentString("OFF"));
        else
            button.setMessage(new TextComponentString("ON"));
        RenderingEventHandler.clearEvent();
        AmbienceController.debugMode = !AmbienceController.debugMode;
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
        addWidget(copy);
        addWidget(paste);

        addWidget(tDebug);
        addWidget(bDebug);
        //scroll.addWidget(this);
        //addButton(test);
    }

    @Override
    public void updateWidgetPosition() {
        copy.x = getBaseX(); copy.y = getRowY(0);
        paste.x = getNeighbourX(copy); paste.y = getRowY(0);

        tDebug.x = getBaseX(); tDebug.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        bDebug.x = getNeighbourX(tDebug); bDebug.y = getRowY(1) + getOffsetY(20);
        //scroll.x = getBaseX(); scroll.y = getRowY(1);
        //scroll.updateWidgetPosition();
        //test.x = getBaseX(); test.y = getRowY(2);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        /*copy.render(matrix, mouseX, mouseY, partialTicks);
        paste.render(matrix, mouseX, mouseY, partialTicks);*/

        //test.render(mouseX, mouseY, partialTicks);
        //scroll.render(mouseX, mouseY);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if(copy.isHovered()) {
            list.add(TextFormatting.RED + "Copy");
            list.add("Copy the current GUIs parameters.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(paste.isHovered()) {
            list.add(TextFormatting.RED + "Paste");
            list.add("Paste the copied parameters onto this block.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
        if(bDebug.isHovered()) {
            list.add(TextFormatting.RED + "Debug");
            list.add("Turns on or off debug mode.");
            list.add("It highlights some GUI elements and will show you details on the main component of the mod.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
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
    public void setFieldFromData(AmbienceData data) {

    }

    @Override
    public void setDataFromField(AmbienceData data) {

    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }
}
