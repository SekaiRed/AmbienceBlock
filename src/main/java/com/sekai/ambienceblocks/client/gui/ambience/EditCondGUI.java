package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.CondTab;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWidgetHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class EditCondGUI extends Screen {
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

    private Button editCond;
    private Button confirm;
    private Button back;

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

        editCond = addButton(new Button(xTopLeft + offset, yTopLeft + offset, 80, 20, "Edit", button -> {
            cond.getDataFromWidgets(condWidgets);
            minecraft.displayGuiScreen(new ChooseCondGUI(this));
        }));

        confirm = addButton(new Button(xTopLeft + 8, yTopLeft + texHeight + 8, 100, 20, "Confirm", button -> {
            cond.getDataFromWidgets(condWidgets);
            tab.replaceCond(index, cond);
            minecraft.displayGuiScreen(prevScreen);
        }));

        back = addButton(new Button(xTopLeft + texWidth - 8 - 100, yTopLeft + texHeight + 8, 100, 20, "Back", button -> {
            prevScreen.forceUpdateCondList();
            minecraft.displayGuiScreen(prevScreen);
        }));

        condWidgets.clear();

        addWidgets(cond.getWidgets());
        int indexX = offset;
        for(AmbienceWidgetHolder element : condWidgets) {
            Widget widget = element.get();
            widget.x = xTopLeft + indexX;
            widget.y = yTopLeft + offset + rowHeight + separation;
            indexX += widget.getWidth() + separation;
        }

        //initialized = true;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground();

        for(AmbienceWidgetHolder widget : condWidgets) {
            widget.get().render(p_render_1_, p_render_2_, p_render_3_);
        }

        editCond.render(p_render_1_, p_render_2_, p_render_3_);

        drawString(font, cond.getName(), editCond.x + editCond.getWidth() + separation, editCond.y + 4, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(prevScreen);
    }

    public void onConfirm() {
        prevScreen.forceUpdateCondList();
        minecraft.displayGuiScreen(prevScreen);
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

    private void addWidget(AmbienceWidgetHolder widget) {
        this.condWidgets.add(widget);
        this.children.add(widget.get());
        if(widget.get() instanceof Button)
            this.buttons.add(widget.get());
    }

    private void addWidgets(List<AmbienceWidgetHolder> widgets) {
        for(AmbienceWidgetHolder widget : widgets) {
            this.condWidgets.add(widget);
            this.children.add(widget.get());
            if(widget.get() instanceof Button)
                this.buttons.add(widget.get());
        }
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

    public void drawMainBackground() {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }
}
