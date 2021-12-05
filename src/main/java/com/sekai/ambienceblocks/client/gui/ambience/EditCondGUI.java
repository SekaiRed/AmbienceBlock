package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.ambience.util.messenger.*;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.CondTab;
import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditCondGUI extends AmbienceScreen implements IFetchCond {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    private boolean initialized = false;
    public int xTopLeft, yTopLeft;

    protected static final int offset = 4;//8;
    protected static final int separation = 8;//16;
    protected static final int rowHeight = 20;

    //private AmbienceGUI prevScreen;
    private AmbienceScreen prevScreen;
    private IFetchCond condFetcher;
    private AbstractCond oldCond;
    private AbstractCond newCond;

    //private boolean offToEditSoundLol = false;

    private List<AmbienceWidgetHolder> condWidgets = new ArrayList<>();
    //the internal reference to get the values to and pass back to the cond
    //private List<AbstractAmbienceWidgetMessenger> widgetMessengers = new ArrayList<>();
    private HashMap<AbstractAmbienceWidgetMessenger, AmbienceWidgetHolder> widgetLink = new HashMap<>();

    private Button editCond;
    private Button confirm;
    private Button back;

    boolean closing = false;

    private boolean condInit = false;

    private boolean isFieldBeingEdited = false;
    private TextField fieldBeingEdited;

    private int counter;

    public EditCondGUI(AmbienceScreen prevScreen, IFetchCond condFetcher, AbstractCond oldCond) {
        super(prevScreen);
        //this.prevScreen = prevScreen;
        this.condFetcher = condFetcher;
        this.oldCond = oldCond;
        this.newCond = oldCond.copy();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        this.clearWidgets();
        widgetLink.clear();
        condWidgets.clear();
        //buttons.clear();

        editCond = new Button(xTopLeft + offset, yTopLeft + offset, 80, 20, new TextComponentString("Edit"), button -> {
            collectData();
            mc.displayGuiScreen(new ChooseCondGUI(this));
        });
        addWidget(editCond);

        confirm = new Button(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20, new TextComponentString("Confirm"), button -> {
            /*collectData();
            tab.replaceCond(index, cond);
            //mc.displayGuiScreen(prevScreen);
            quitFromScreen();*/

            collectData();
            condFetcher.fetch(newCond, oldCond);
            quitFromScreen();
        });
        addWidget(confirm);

        back = new Button(xTopLeft + texWidth - 4 - 100, yTopLeft + texHeight + 4, 100, 20, new TextComponentString("Back"), button -> {
            //prevScreen.forceUpdateCondList();
            //mc.displayGuiScreen(prevScreen);
            /*if(getPreviousScreen() instanceof AmbienceGUI)
                ((AmbienceGUI) getPreviousScreen()).forceUpdateCondList();*/

            quitFromScreen();
        });
        addWidget(back);

        condWidgets.clear();

        addCondWidgets(newCond.getWidgets());
        updateCondWidgetPos();

        /*if(!condInit) {
            addCondWidgets(cond.getWidgets());
            updateCondWidgetPos();
            condInit = true;
        } else {
            for(Widget widget : buttons) {
                if(!children.contains(widget))
                    addButton(widget);
            }
        }*/

        //initialized = true;
    }

    private void collectData() {
        List<AbstractAmbienceWidgetMessenger> data = new ArrayList<>();
        for(AbstractAmbienceWidgetMessenger messenger : widgetLink.keySet()) {
            if(messenger instanceof AmbienceWidgetString) {
                String value = ((TextField) widgetLink.get(messenger).get()).getText();
                AmbienceWidgetString widget = (AmbienceWidgetString) messenger;
                widget.setValue(value);
            }
            if(messenger instanceof AmbienceWidgetSound) {
                String value = ((TextField) widgetLink.get(messenger).get()).getText();
                AmbienceWidgetSound widget = (AmbienceWidgetSound) messenger;
                widget.setValue(value);
            }
            if(messenger instanceof AmbienceWidgetCheckbox) {
                boolean value = ((Checkbox) widgetLink.get(messenger).get()).isChecked();
                AmbienceWidgetCheckbox widget = (AmbienceWidgetCheckbox) messenger;
                widget.setValue(value);
            }
            data.add(messenger);
        }
        newCond.getDataFromWidgets(data);
    }

    //this is the part of the code that interprets AmbienceWidgetMessenger to create the widgets
    //most likely a dumb way to do this but I can't think of anything else
    private void addCondWidgets(List<AbstractAmbienceWidgetMessenger> widgets) {
        for(int i = 0; i < widgets.size(); i++) {
            AbstractAmbienceWidgetMessenger widget = widgets.get(i);

            //AmbienceWidgetHolder label = new AmbienceWidgetHolder("", new TextInstance(0, (rowHeight - font.FONT_HEIGHT)/2, 0xFFFFFF, widget.getLabel(), font));
            //addWidget(label);
            if(!widget.getLabel().isEmpty()) {
                AmbienceWidgetHolder label = new AmbienceWidgetHolder("", new TextInstance(0, (rowHeight - font.FONT_HEIGHT) / 2, 0xFFFFFF, widget.getLabel(), font));
                addWidget(label);
            }

            if(widget instanceof AmbienceWidgetString) {
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new TextField(font, 0, 0, widget.getWidth(), 20, new TextComponentString("")));
                TextField text = (TextField) holder.get();
                text.setText(((AmbienceWidgetString) widget).getValue());
                text.setValidator(((AmbienceWidgetString) widget).getValidator());
                if(((AmbienceWidgetString) widget).getCharLimit() > 0) {
                    text.setMaxStringLength(((AmbienceWidgetString) widget).getCharLimit());
                }
                addWidget(holder);
                //widgetMessengers.add(widget);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetEnum) {
                AmbienceWidgetEnum wEnum = (AmbienceWidgetEnum) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, widget.getWidth(), 20, new TextComponentString(ParsingUtil.getCachedEnumName(wEnum.getValue())), button -> {
                    //test = test.next();
                    //button.setMessage(new StringTextComponent(test.getName()));
                    wEnum.next();
                    button.setMessage(new TextComponentString(ParsingUtil.getCachedEnumName(wEnum.getValue())));
                }));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wCond = (AmbienceWidgetCond) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, widget.getWidth(), 20, new TextComponentString(wCond.getCond().getListDescription()), button -> {
                    //isFieldBeingEdited = true;
                    //printConditionHash("get cond", wCond.getCond());
                    mc.displayGuiScreen(new EditCondGUI(this, this, wCond.getCond()));
                }));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetSound) {
                //System.out.println(++counter);
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new TextField(font,0, 0, widget.getWidth() - separation * 2 - 20, 20, new TextComponentString("")));
                AmbienceWidgetHolder button = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, 20, 20, new TextComponentString("..."), b -> {
                    isFieldBeingEdited = true;
                    fieldBeingEdited = (TextField) holder.get();
                    Minecraft.getMinecraft().displayGuiScreen(new ChooseSoundGUI(this, (TextField) holder.get()));
                }));
                TextField text = (TextField) holder.get();
                //System.out.println(text);
                text.setMaxStringLength(50);
                if(isFieldBeingEdited) {
                    //this is a lazy fix lol
                    //System.out.println("new" + fieldBeingEdited + ", " + fieldBeingEdited.getText());
                    text.setText(fieldBeingEdited.getText());
                    //fieldBeingEdited = null;
                    //isFieldBeingEdited = false;
                } else {
                    text.setText(((AmbienceWidgetSound) widget).getValue());
                }
                //text.setText("fuck my ass");
                addWidget(holder);
                addWidget(button);
                //addButton(button.get());
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetScroll) {
                AmbienceWidgetScroll wScroll = (AmbienceWidgetScroll) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new ScrollListWidget(0, 0, widget.getWidth(), 20, 4, 16, 5, wScroll.getValues(), this, new ScrollListWidget.IPressable() {
                    @Override
                    public void onChange(ScrollListWidget list, int index, String name) {
                        wScroll.setValue(name);
                    }
                }));
                ScrollListWidget scrollListWidget = (ScrollListWidget) holder.get();
                scrollListWidget.setSelectionByString(wScroll.getValue());
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetCheckbox) {
                AmbienceWidgetCheckbox wCheck = (AmbienceWidgetCheckbox) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new Checkbox(0, 0, widget.getWidth(), 20, new TextComponentString(""), wCheck.getValue()));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
        }
    }

    private void updateCondWidgetPos() {
        int indexX = offset + separation;
        int indexY = offset + rowHeight + separation;
        for(AmbienceWidgetHolder element : condWidgets) {
            Widget widget = element.get();
            if(indexX + widget.width + separation > texWidth - offset) {
                indexX = offset + separation;
                indexY += rowHeight + separation;
            }
            widget.x = xTopLeft + indexX;
            widget.y = yTopLeft + indexY + (rowHeight - widget.height)/2;
            //widget.y = yTopLeft + offset + rowHeight + separation;
            indexX += widget.width + separation;

            //TODO to what lengths will you go just to avoid putting in a setPos method in widgets
            if(widget instanceof ScrollListWidget) ((ScrollListWidget)widget).updateWidgetPosition();
        }
    }

    @Override
    public void drawScreen(int p_render_1_, int p_render_2_, float p_render_3_) {
        drawMainBackground();

        drawString(font, newCond.getName(), editCond.x + editCond.width + separation, editCond.y + 6, 0xFFFFFF);

        super.drawScreen(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        /*for(AmbienceWidgetHolder widget : condWidgets) {
            if(widget.get() instanceof TextFieldWidget) {
                ((TextFieldWidget) widget.get()).tick();
            }
        }*/
    }

    public void setCond(AbstractCond cond) {
        this.newCond = cond;
    }

    /*private void addWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.add(widget);
        this.children.add(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.add(widget.get());
    }*/

    private void addWidget(AmbienceWidgetHolder widget) {
        condWidgets.add(widget);
        this.addWidget(widget.get());
        //todo does this work lol
        /*this.condWidgets.add(widget);
        this.children.add(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.add(widget.get());
        if(widget.get() instanceof ScrollListWidget)
            ((ScrollListWidget) widget.get()).addWidget(this);*/
    }

    private void addWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets)
            addWidget(widget);
    }

    /*private void addWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets) {
            this.condWidgets.add(widget);
            this.children.add(widget.get());
            if(widget.get() instanceof Button)
                this.buttons.add(widget.get());
            if(widget.get() instanceof ScrollListWidget)
                ((ScrollListWidget) widget.get()).addWidget(this);
        }
    }*/

    private void removeWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.remove(widget);
        this.removeWidget(widget.get());
        /*this.children.remove(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.remove(widget.get());*/
    }

    private void removeWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets) {
            removeWidget(widget);
            //this.condWidgets.remove(widget);
            /*this.children.remove(widget.get());
            if(widget.get() instanceof Button)
                this.buttons.remove(widget.get());*/
        }
    }

    public void drawMainBackground() {
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    @Override
    public void fetch(AbstractCond newCond, AbstractCond oldCond) {
        //Upon returning to this screen after "fetching" a condition, this happens
        for(AbstractAmbienceWidgetMessenger widget : widgetLink.keySet()) {
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wc = (AmbienceWidgetCond) widget;
                if(wc.getCond() == oldCond/*wc.getCond().equals(oldCond)*/) {
                    wc.setCond(newCond);
                    widgetLink.get(widget).get().setMessage(new TextComponentString(newCond.getListDescription()));
                    collectData();
                }
            }
        }
    }
}
