package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.ambience.util.messenger.*;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.presets.textfield.CustomTextField;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    /*private CondTab tab;
    private AbstractCond cond;
    private int index;*/

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
    private CustomTextField fieldBeingEdited;

    public EditCondGUI(AmbienceScreen prevScreen, IFetchCond condFetcher, AbstractCond oldCond) {
        super(new TranslationTextComponent("narrator.screen.editcond"));
        this.prevScreen = prevScreen;
        this.condFetcher = condFetcher;
        this.oldCond = oldCond;
        this.newCond = oldCond.copy();
    }

    //private int counter;

    /*public EditCondGUI(AmbienceScreen prevScreen, CondTab tab, AbstractCond cond, int index) {
        super(new TranslationTextComponent("narrator.screen.editcond"));

        this.prevScreen = prevScreen;
        this.tab = tab;
        this.cond = cond;
        this.index = index;
    }*/

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        System.out.println("init");

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        widgetLink.clear();
        condWidgets.clear();
        buttons.clear();

        editCond = addButton(new Button(xTopLeft + offset, yTopLeft + offset, 80, 20, new StringTextComponent("Edit"), button -> {
            collectData();

            minecraft.displayGuiScreen(new ChooseCondGUI(this));
        }));

        confirm = addButton(new Button(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20, new StringTextComponent("Confirm"), button -> {
            /*collectData();
            if(tab != null)
                tab.replaceCond(index, cond);

            minecraft.displayGuiScreen(prevScreen);*/
            collectData();

            printConditionHash("confirm new", newCond);
            printConditionHash("confirm old", oldCond);

            condFetcher.fetch(newCond, oldCond);

            minecraft.displayGuiScreen(prevScreen);
        }));

        back = addButton(new Button(xTopLeft + texWidth - 4 - 100, yTopLeft + texHeight + 4, 100, 20, new StringTextComponent("Back"), button -> {
            /*if(prevScreen instanceof AmbienceGUI)
                ((AmbienceGUI) prevScreen).forceUpdateCondList();*/
            ///TODO ???

            minecraft.displayGuiScreen(prevScreen);
        }));

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
                String value = ((CustomTextField) widgetLink.get(messenger).get()).getText();
                AmbienceWidgetString widget = (AmbienceWidgetString) messenger;
                widget.setValue(value);
            }
            if(messenger instanceof AmbienceWidgetSound) {
                String value = ((CustomTextField) widgetLink.get(messenger).get()).getText();
                AmbienceWidgetSound widget = (AmbienceWidgetSound) messenger;
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

            AmbienceWidgetHolder label = new AmbienceWidgetHolder("", new TextInstance(0, (rowHeight - font.FONT_HEIGHT)/2, 0xFFFFFF, widget.getLabel(), font));
            addWidget(label);
            //addWidget(new TextInstance(0, (rowHeight - font.FONT_HEIGHT)/2, 0xFFFFFF, widget.getLabel(), font));

            if(widget instanceof AmbienceWidgetString) {
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, widget.getWidth(), 20, ""));
                CustomTextField text = (CustomTextField) holder.get();
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
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, widget.getWidth(), 20, new StringTextComponent(ParsingUtil.getCachedEnumName(wEnum.getValue())), button -> {
                    //test = test.next();
                    //button.setMessage(new StringTextComponent(test.getName()));
                    wEnum.next();
                    button.setMessage(new StringTextComponent(ParsingUtil.getCachedEnumName(wEnum.getValue())));
                }));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wCond = (AmbienceWidgetCond) widget;
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, widget.getWidth(), 20, new StringTextComponent(wCond.getCond().getListDescription()), button -> {
                    //isFieldBeingEdited = true;
                    printConditionHash("get cond", wCond.getCond());
                    Minecraft.getInstance().displayGuiScreen(new EditCondGUI(this, this, wCond.getCond()));
                }));
                addWidget(holder);
                widgetLink.put(widget, holder);
            }
            if(widget instanceof AmbienceWidgetSound) {
                //System.out.println(++counter);
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, widget.getWidth() - separation * 2 - 20, 20, ""));
                AmbienceWidgetHolder button = new AmbienceWidgetHolder(widget.getKey(), new Button(0, 0, 20, 20, new StringTextComponent("..."), b -> {
                    isFieldBeingEdited = true;
                    fieldBeingEdited = (CustomTextField) holder.get();
                    Minecraft.getInstance().displayGuiScreen(new ChooseSoundGUI(this, (CustomTextField) holder.get()));
                }));
                CustomTextField text = (CustomTextField) holder.get();
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
        }
    }

    private void updateCondWidgetPos() {
        int indexX = offset;
        int indexY = offset + rowHeight + separation;
        for(AmbienceWidgetHolder element : condWidgets) {
            Widget widget = element.get();
            //new line if the widget that is about to be added leaks out of the main window
            if(indexX + widget.getWidth() + separation > texWidth - offset) {
                indexX = offset + separation;
                indexY += rowHeight + separation;
            }
            widget.x = xTopLeft + indexX;
            widget.y = yTopLeft + indexY + (rowHeight - widget.getHeightRealms())/2;
            //widget.y = yTopLeft + offset + rowHeight + separation;
            indexX += widget.getWidth() + separation;
            //moved down here so that the position truly is the next one to be moved to the new line
            /*if(indexX > texWidth - offset) {
                indexX = offset;
                indexY += rowHeight + separation;
            }*/
            if(widget instanceof ScrollListWidget) ((ScrollListWidget)widget).updateWidgetPosition();
        }
    }

    @Override
    public void render(MatrixStack matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground(matrix);

        for(AmbienceWidgetHolder widget : condWidgets) {
            widget.get().render(matrix, p_render_1_, p_render_2_, p_render_3_);

            if(widget.get() instanceof ScrollListWidget) ((ScrollListWidget)widget.get()).render(matrix, p_render_1_, p_render_2_);
        }

        editCond.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawString(matrix, font, newCond.getName(), editCond.x + editCond.getWidth() + separation, editCond.y + 6, 0xFFFFFF);
    }

    //Doesn't help with esc closure and causes problems
    /*@Override
    public void onClose() {
        //System.out.println(closing + " " + prevScreen);
        if(!closing) {
            closing = true;
            minecraft.displayGuiScreen(prevScreen);
        }
    }*/

    /*public void onConfirm() {
        prevScreen.forceUpdateCondList();
        if(!closing) {
            closing = true;
            minecraft.displayGuiScreen(prevScreen);
        }
    }*/

    @Override
    public void tick() {
        super.tick();

        for(AmbienceWidgetHolder widget : condWidgets) {
            if(widget.get() instanceof TextFieldWidget) {
                ((TextFieldWidget) widget.get()).tick();
            }
        }
    }

    public void setCond(AbstractCond cond) {
        this.newCond = cond;
    }

    private void addWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.add(widget);
        this.children.add(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.add(widget.get());
        if(widget.get() instanceof ScrollListWidget)
            ((ScrollListWidget) widget.get()).addWidget(this);
    }

    private void addWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets)
            addWidget(widget);
    }

    private void removeWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.remove(widget);
        this.children.remove(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.remove(widget.get());
    }

    private void removeWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets) {
            this.condWidgets.remove(widget);
            this.children.remove(widget.get());
            if(widget.get() instanceof Button)
                this.buttons.remove(widget.get());
        }
    }

    public void drawMainBackground(MatrixStack matrix) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(matrix, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    @Override
    public void fetch(AbstractCond newCond, AbstractCond oldCond) {
        //Upon returning to this screen after "fetching" a condition, this happens
        System.out.println("fetched " + newCond + " from old " + oldCond);
        for(AbstractAmbienceWidgetMessenger widget : widgetLink.keySet()) {
            if(widget instanceof AmbienceWidgetCond) {
                AmbienceWidgetCond wc = (AmbienceWidgetCond) widget;
                System.out.println(wc.getCond() + " is here " + oldCond + " / " + newCond + " " + (wc.getCond() == oldCond)/*wc.getCond().equals(oldCond)*/);
                System.out.println("wc : " + wc.getCond().hashCode() + ", old : " + oldCond.hashCode() + ", new : " + newCond.hashCode());
                if(wc.getCond() == oldCond/*wc.getCond().equals(oldCond)*/) {
                    System.out.println("and reached " + wc);
                    wc.setCond(newCond);
                    widgetLink.get(widget).get().setMessage(new StringTextComponent(newCond.getListDescription()));
                    collectData();
                }
            }
        }
    }

    public static void printConditionHash(String comment, AbstractCond cond) {
        if(comment.isEmpty())
            System.out.println(cond.hashCode() + " : " + cond.getListDescription());
        else
            System.out.println(comment + " : " + cond.hashCode() + " -> " + cond.getListDescription());
    }

    public static void printConditionHash(AbstractCond cond) {
        //System.out.println(cond.hashCode() + " : " + cond.getListDescription());
        printConditionHash("", cond);
    }
}
