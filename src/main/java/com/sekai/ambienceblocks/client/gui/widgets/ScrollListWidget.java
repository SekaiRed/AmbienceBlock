package com.sekai.ambienceblocks.client.gui.widgets;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.AbstractTab;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

public class ScrollListWidget extends Widget {
    private final AmbienceScreen screen;

    public ArrayList<String> list;
    public int index;

    protected final IPressable onChange;
    private State state = State.NEUTRAL;

    //internal
    int internalWidth;
    int internalHeight;

    //list options
    int separation;
    int optionHeight;
    FontRenderer font;

    public Button main;

    public StringListWidget scrollList;

    public ScrollListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, int listLength, ArrayList<String> list, AmbienceScreen screen, IPressable onChange) {
        super(xIn, yIn, widthIn, heightIn, new TextComponentString(""));
        internalWidth = widthIn;
        internalHeight = heightIn;
        this.separation = separation;
        this.font = screen.font;
        if(optionHeight < font.FONT_HEIGHT) optionHeight = font.FONT_HEIGHT;
        this.optionHeight = optionHeight;
        this.screen = screen;
        this.onChange = onChange;
        this.list = list;

        main = new Button(0, 0, 30, 20, new TextComponentString(""), button -> {
            mainPressed();
        });
        if(list.size()!=0)
            main.setMessage(new TextComponentString(list.get(0)));
        main.x = xIn;
        main.y = yIn;
        /*main.setWidth(internalWidth);
        main.setHeight(internalHeight);*/
        main.width = internalWidth;
        main.height = internalHeight;

        scrollList = new StringListWidget(x + 1, y + internalHeight, internalWidth - 2, internalHeight * listLength + separation - 1, separation, optionHeight, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(scrollList.isVisible())
                    setIndex(index);
                //onChange.onChange(sw, index, name);
            }
        });
        for (String s : list) scrollList.addElement(s);

        index = 0;

        hideMenu();

        //register widgets
        screen.addWidget(main);
        screen.addWidget(scrollList);
    }

    public void mainPressed() {
        if(!this.isVisible()) return;

        if(state.equals(State.NEUTRAL) && list.size()!=0) {
            showMenu();
        }
        else if(state.equals(State.LIST_UP)) {
            hideMenu();
        }
    }

    public void showMenu() {
        state = State.LIST_UP;
        scrollList.setVisible(true);
        //scrollList.visible = true;
        scrollList.active = true;
        scrollList.setSelectionByString(list.get(index));
    }

    public void hideMenu() {
        state = State.NEUTRAL;
        scrollList.setVisible(false);
        //scrollList.visible = false;
        scrollList.active = false;
    }

    public void setIndex(int index) {
        if(index < 0 || index >= list.size())
            return;

        if(this.index != index) {
            main.setMessage(new TextComponentString(list.get(index)));
            hideMenu();
            this.index = index;
            onChange.onChange(this, index, list.get(index));
        } else {
            hideMenu();
        }
    }

    public void setSelectionByString(String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(name)) {
                setIndex(i);
            }
        }
    }

    public void updateWidgetPosition() {
        main.x = x;
        main.y = y;
        scrollList.x = x + 1;
        scrollList.y = y + internalHeight + 1;
    }

    public void render(int mouseX, int mouseY) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        /* If I register widgets through the ambience screen I don't need to render them manually
        if(!this.visible)
            return;

        main.render(mouseX, mouseY, AmbienceScreen.Layer.LOWEST);

        if(state.equals(State.LIST_UP) && scrollList != null) {
            scrollList.render(mouseX, mouseY, AmbienceScreen.Layer.HIGHEST);
        }*/
    }

    @Override
    public boolean hasSubWidgets() {
        return true;
    }

    @Override
    public List<Widget> getSubWidgets() {
        List<Widget> list = super.getSubWidgets();
        list.add(main);
        list.add(scrollList);
        return list;
    }

    //TODO maybe bring back setX and setY to notify instead of this
    @Override
    public void forceUpdatePosition() {
        main.x = x;
        main.y = y;
        scrollList.x = x;
        scrollList.y = y;
    }

    @Override
    public void setLayer(AmbienceScreen.Layer layer) {
        super.setLayer(layer);
        main.setLayer(layer);
        scrollList.setLayer(layer);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        main.setVisible(visible);
    }

    /* unused apparently
    @Override
    public boolean mouseClicked(double pX, double pY, int pType) {
        if(state.equals(State.LIST_UP) && scrollList != null && this.visible) {
            //scrollList.mouseClicked(pX, pY, pType);
        }
        return false;
    }*/

    /*public void addWidget(AbstractTab gui) {
        gui.addButton(main);
        //gui.addWidget(scrollList);
        gui.addWidget(this);
    }

    public void addWidget(AmbienceScreen gui) {
        gui.addWidget(main);
        gui.addWidget(scrollList);
        gui.addWidget(this);
    }*/

    public String getSelectedString() {
        return list.get(index);
    }

    public interface IPressable {
        void onChange(ScrollListWidget list, int index, String name);
    }

    private enum State {
        NEUTRAL,
        LIST_UP;
    }
}