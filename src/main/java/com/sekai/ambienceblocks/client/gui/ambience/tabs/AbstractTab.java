package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AbstractTab {
    protected FontRenderer font;
    protected AmbienceGUI screen;
    protected int x, y, width, height;

    private boolean active;
    public final List<Widget> widgets = new ArrayList<>();
    //public final List<Widget> buttons = new ArrayList<>();

    protected static final int horizontalSeparation = 6;
    protected static final int verticalSeparation = 8;
    protected static final int rowHeight = 18;
    protected static final int checkboxOffset = 4;

    public AbstractTab(AmbienceGUI screen) {
        font = Minecraft.getMinecraft().fontRenderer;
        this.screen = screen;
        updateMetaValues(screen);
        //init();
    }

    public void refreshWidgets() {
        /*for(Widget widget : widgets) {
            if(!guiRef.getChildrens().contains(widget))
                guiRef.addWidget(widget);
        }

        for(Widget widget : buttons) {
            if(!guiRef.getChildrens().contains(widget))
                guiRef.addButton(widget);
        }*/
    }

    public void updateMetaValues(AmbienceGUI guiRef) {
        this.x = guiRef.xTopLeft;
        this.y = guiRef.yTopLeft;
        this.width = AmbienceGUI.texWidth;
        this.height = AmbienceGUI.texHeight;
    }

    public void addWidget(Widget widget) {
        screen.addWidget(widget);
        widgets.add(widget);

        //add subwidgets like for the scrolllist's button and stringlist
        if(widget.hasSubWidgets()) {
            for(Widget w : widget.getSubWidgets())
                screen.addWidget(w);
        }
    }

    /*public void addButton(Widget widget) {
        guiRef.addButton(widget);
        buttons.add(widget);
    }*/

    public abstract String getName();

    public abstract String getShortName();

    public abstract void initialInit();

    public abstract void updateWidgetPosition();

    //public void secondInit() {}
    /*public void init() {

    }*/

    public abstract void render(int mouseX, int mouseY);

    public abstract void renderToolTip(int mouseX, int mouseY);

    public abstract void tick();

    /**
     * @param data to pass into the gui from the tile.
     */
    public abstract void setFieldFromData(AmbienceData data);
    /**
     * @param data to pass into the tile from the gui.
     */
    public abstract void setDataFromField(AmbienceData data);

    public void activate() {
        active = true;
        for(Widget widget : widgets) {
            if(widget != null) {
                //widget.visible = true;
                widget.setVisible(true);
                widget.active = true;
            }
        }
        onActivate();
    }

    public abstract void onActivate();

    public void deactivate() {
        active = false;
        for(Widget widget : widgets) {
            if(widget != null) {
                //widget.visible = false;
                widget.setVisible(false);
                widget.active = false;
            }
        }
        onDeactivate();
    }

    public abstract void onDeactivate();

    public boolean isActive() {
        return active;
    }

    //util stuff
    protected int getBaseX() {
        return x + horizontalSeparation;
    }

    protected int getEndX() {
        return x + width - horizontalSeparation;
    }

    protected int getEndY() {
        return y + height - verticalSeparation;
    }

    protected int getNeighbourX(Widget widget) {
        return widget.x + widget.width + horizontalSeparation;
    }

    protected int getRowY(int row) {
        return y + rowHeight * row + verticalSeparation * (row + 1) + AmbienceGUI.tabHeight;
    }

    protected int getOffsetY(int height) {
        return (rowHeight - height) / 2 + 1;
    }

    public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        //ArrayList<StringTextComponent> textComponentList = new ArrayList<>();
        //for (String textLine : textLines) textComponentList.add(new StringTextComponent(textLine));
        GuiUtils.drawHoveringText(textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
    }
    //
}
