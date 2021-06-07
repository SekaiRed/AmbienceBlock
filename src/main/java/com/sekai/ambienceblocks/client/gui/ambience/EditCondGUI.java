package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.CondTab;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.presets.textfield.CustomTextField;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditCondGUI extends AmbienceScreen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    private boolean initialized = false;
    public int xTopLeft, yTopLeft;

    protected static final int offset = 8;
    protected static final int separation = 16;
    protected static final int rowHeight = 20;

    private AmbienceGUI prevScreen;
    private CondTab tab;
    private AbstractCond cond;
    private int index;

    private List<AmbienceWidgetHolder> condWidgets = new ArrayList<>();
    //the internal reference to get the values to and pass back to the cond
    private List<AbstractAmbienceWidgetMessenger> widgetMessengers = new ArrayList<>();
    private HashMap<AbstractAmbienceWidgetMessenger, AmbienceWidgetHolder> widgetLink = new HashMap<>();

    private Button editCond;
    private Button confirm;
    private Button back;

    boolean closing = false;

    public EditCondGUI(AmbienceGUI prevScreen, CondTab tab, AbstractCond cond, int index) {
        super(new TranslationTextComponent("narrator.screen.editcond"));

        this.prevScreen = prevScreen;
        this.tab = tab;
        this.cond = cond;
        this.index = index;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        editCond = addButton(new Button(xTopLeft + offset, yTopLeft + offset, 80, 20, new StringTextComponent("Edit"), button -> {
            //todo cond.getDataFromWidgets(condWidgets);
            collectData();
            minecraft.displayGuiScreen(new ChooseCondGUI(this));
        }));

        confirm = addButton(new Button(xTopLeft + 8, yTopLeft + texHeight + 8, 100, 20, new StringTextComponent("Confirm"), button -> {
            //todo cond.getDataFromWidgets(condWidgets);
            collectData();
            tab.replaceCond(index, cond);
            minecraft.displayGuiScreen(prevScreen);
        }));

        back = addButton(new Button(xTopLeft + texWidth - 8 - 100, yTopLeft + texHeight + 8, 100, 20, new StringTextComponent("Back"), button -> {
            prevScreen.forceUpdateCondList();
            minecraft.displayGuiScreen(prevScreen);
        }));

        condWidgets.clear();

        addCondWidgets(cond.getWidgets());
        //addWidgets(cond.getWidgets());
        updateCondWidgetPos();

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
            data.add(messenger);
        }
        cond.getDataFromWidgets(data);
    }

    //this is the part of the code that interprets AmbienceWidgetMessenger to create the widgets
    //most likely a dumb way to do this but I can't think of anything else
    private void addCondWidgets(List<AbstractAmbienceWidgetMessenger> widgets) {
        for(int i = 0; i < widgets.size(); i++) {
            AbstractAmbienceWidgetMessenger widget = widgets.get(i);
            if(widget instanceof AmbienceWidgetString) {
                AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, widget.getWidth(), 20, ""));
                ((CustomTextField) holder.get()).setText(((AmbienceWidgetString) widget).getValue());
                addWidget(holder);
                widgetMessengers.add(widget);
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
                widgetMessengers.add(widget);
                widgetLink.put(widget, holder);
                /*AmbienceWidgetHolder holder = new AmbienceWidgetHolder(widget.getKey(), new CustomTextField(0, 0, 50, 20, ""));
                ((CustomTextField) holder.get()).setText(((AmbienceWidgetString) widget).getValue());
                addWidget(holder);
                widgetMessengers.add(widget);*/
            }
        }
    }

    private void updateCondWidgetPos() {
        int indexX = offset;
        for(AmbienceWidgetHolder element : condWidgets) {
            Widget widget = element.get();
            widget.x = xTopLeft + indexX;
            widget.y = yTopLeft + offset + rowHeight + separation;
            indexX += widget.getWidth() + separation;
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

        drawString(matrix, font, cond.getName(), editCond.x + editCond.getWidth() + separation, editCond.y + 4, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if(!closing) {
            closing = true;
            minecraft.displayGuiScreen(prevScreen);
        }
    }

    public void onConfirm() {
        prevScreen.forceUpdateCondList();
        if(!closing) {
            closing = true;
            minecraft.displayGuiScreen(prevScreen);
        }
    }

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
        this.cond = cond;
    }

    /*private void addWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.add(widget);
        this.children.add(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.add(widget.get());
    }*/

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
}
