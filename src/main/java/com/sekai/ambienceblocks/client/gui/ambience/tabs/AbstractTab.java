package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTab {
    protected FontRenderer font;
    protected AmbienceGUI guiRef;
    protected int x, y, width, height;

    private boolean active;
    private List<Widget> widgets = new ArrayList<>();

    protected static final int separation = 8;
    protected static final int rowHeight = 20;

    public AbstractTab(AmbienceGUI guiRef) {
        font = Minecraft.getInstance().fontRenderer;
        this.guiRef = guiRef;
        updateMetaValues(guiRef);
        init();
    }

    //util stuff
    public int getBaseX() {
        return x + separation;
    }

    public int getEndX() {
        return x + width - separation;
    }

    public int getNeighbourX(Widget widget) {
        return widget.x + widget.getWidth() + separation;
    }

    public int getRowY(int row) {
        return y + rowHeight * row + separation * (row + 1) + AmbienceGUI.tabHeight;
    }

    public int getOffsetY(int height) {
        return (rowHeight - height) / 2;
    }

    public int getWidthFromTwoX(int x1, int x2) {
        if(x1 < x2)
            return x2 - x1 - separation * 2;

        if(x1 > x2)
            return x1 - x2 - separation * 2;

        return 0;
    }
    //

    public void updateMetaValues(AmbienceGUI guiRef) {
        this.x = guiRef.xTopLeft;
        this.y = guiRef.yTopLeft;
        this.width = AmbienceGUI.texWidth;
        this.height = AmbienceGUI.texHeight;
    }

    public void addWidget(Widget widget) {
        guiRef.addWidget(widget);
        widgets.add(widget);
    }

    public void activate() {
        active = true;
        for(Widget widget : widgets) {
            widget.visible = true;
            widget.active = true;
        }
    }

    public void deactivate() {
        active = false;
        for(Widget widget : widgets) {
            widget.visible = false;
            widget.active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public abstract String getName();

    public abstract void init();

    public abstract void render(int mouseX, int mouseY, float partialTicks);

    public abstract void tick();

    /**
     * @param data to pass into the gui from the tile.
     */
    public abstract void setFieldFromData(AmbienceTileEntityData data);
    /**
     * @param data to pass into the tile from the gui.
     */
    public abstract void setDataFromField(AmbienceTileEntityData data);
}
