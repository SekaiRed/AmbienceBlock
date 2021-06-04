package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.AbstractTab;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ScrollListWidget extends Widget {
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

    public ScrollListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, int listLength, ArrayList<String> list, FontRenderer font, IPressable onChange) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""));
        internalWidth = widthIn;
        internalHeight = heightIn;
        this.separation = separation;
        if(optionHeight < font.FONT_HEIGHT) optionHeight = font.FONT_HEIGHT;
        this.optionHeight = optionHeight;
        this.font = font;
        this.onChange = onChange;
        this.list = list;

        setWidth(0);
        setHeight(0);

        main = new Button(0, 0, 30, 20, new StringTextComponent(""), button -> {
            mainPressed();
        });
        if(list.size()!=0)
            main.setMessage(new StringTextComponent(list.get(0)));
        main.x = xIn;
        main.y = yIn;
        main.setWidth(internalWidth);
        main.setHeight(internalHeight);

        this.width = widthIn;
        this.height = heightIn;

        scrollList = new StringListWidget(x + 1, y + internalHeight, internalWidth - 2, internalHeight * listLength/*4*/, separation, optionHeight, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(scrollList.visible)
                    setIndex(index);
                //onChange.onChange(sw, index, name);
            }
        });
        for (String s : list) scrollList.addElement(s);

        index = 0;

        hideMenu();
    }

    public void mainPressed() {
        if(!this.visible) return;

        if(state.equals(State.NEUTRAL) && list.size()!=0) {
            showMenu();
        }
        else if(state.equals(State.LIST_UP)) {
            hideMenu();
        }
    }

    public void showMenu() {
        state = State.LIST_UP;
        scrollList.visible = true;
        scrollList.active = true;
        scrollList.setSelectionByString(list.get(index));
    }

    public void hideMenu() {
        state = State.NEUTRAL;
        scrollList.visible = false;
        scrollList.active = false;
    }

    public void setIndex(int index) {
        if(index < 0 || index >= list.size())
            return;

        if(this.index != index) {
            main.setMessage(new StringTextComponent(list.get(index)));
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

    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        if(!this.visible)
            return;

        main.render(matrix, mouseX, mouseY, 0);

        if(state.equals(State.LIST_UP) && scrollList != null) {
            scrollList.render(matrix, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pX, double pY, int pType) {
        if(state.equals(State.LIST_UP) && scrollList != null && this.visible) {
            //scrollList.mouseClicked(pX, pY, pType);
        }
        return false;
    }

    public void addWidget(AbstractTab gui) {
        gui.addButton(main);
        //gui.addWidget(scrollList);
        gui.addWidget(this);
    }

    public void addWidget(AmbienceScreen gui) {
        gui.addWidget(main);
        gui.addWidget(scrollList);
        gui.addWidget(this);
    }

    public String getSelectedString() {
        return list.get(index);
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onChange(ScrollListWidget list, int index, String name);
    }

    private enum State {
        NEUTRAL,
        LIST_UP;
    }
}