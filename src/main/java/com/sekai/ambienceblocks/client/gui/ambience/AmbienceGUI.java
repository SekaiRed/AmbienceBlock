package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.AbstractTab;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.BoundsTab;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.DelayTab;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.MainTab;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class AmbienceGUI extends Screen {
    private final AmbienceTileEntity target;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    public static final int tabEdgeWidth = 16, tabHeight = 16;

    private static final int intNull = Integer.MAX_VALUE;

    private MainTab mainTab;
    private DelayTab delayTab;
    private BoundsTab boundsTab;

    private AbstractTab highlightedTab;

    //private List<String> options = Arrays.asList("Main", "Bounds", "Priority", "Delay");

    public AmbienceGUI(AmbienceTileEntity target) {
        super(new TranslationTextComponent("narrator.screen.globalambiencegui"));

        this.target = target;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        drawMainBackground();

        drawTabs(mouseX, mouseY);

        if(highlightedTab != null)
            highlightedTab.render(mouseX, mouseY, partialTicks);

        /*if(highlightIndex == 0)
            mainTab.render(mouseX, mouseY, partialTicks);*/
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        mainTab = new MainTab(this);
        setHighlightedTab(mainTab);
        mainTab.setFieldFromData(target.data);

        boundsTab = new BoundsTab(this);

        delayTab = new DelayTab(this);
        delayTab.setFieldFromData(target.data);
    }

    @Override
    public void tick() {
        super.tick();

        mainTab.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseType) {
        List<AbstractTab> tabs = getActiveTabs();
        for(int i = 0; i < tabs.size(); i++) {
            if(isMouseInTab((int)mouseX, (int)mouseY, i)) {
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //setHighlightIndex(i);
                setHighlightedTab(getActiveTabs().get(i));
                return super.mouseClicked(mouseX, mouseY, mouseType);
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseType);
    }

    private AbstractTab getHighlightedTab() {
        return highlightedTab;
    }

    private void setHighlightedTab(AbstractTab tab) {
        if(highlightedTab != null)
            highlightedTab.deactivate();

        highlightedTab = tab;
        highlightedTab.activate();
    }

    private List<AbstractTab> getActiveTabs() {
        List<AbstractTab> list = new ArrayList<>();

        list.add(mainTab);

        list.add(boundsTab);

        if(mainTab.useDelay.isChecked())
            list.add(delayTab);

        return list;
    }

    private int getTabWidth() {
        return texWidth/getActiveTabs().size();
    }

    private TabState getTabState(int mouseX, int mouseY, AbstractTab tab) {
        if(getHighlightedTab() == tab) return TabState.HIGHLIGHTED;
        if(getHoveredTab(mouseX, mouseY) == tab) return TabState.HOVERED;
        return TabState.NEUTRAL;
    }

    private AbstractTab getHoveredTab(int mouseX, int mouseY) {
        List<AbstractTab> list = getActiveTabs();
        for(int i = 0; i < list.size(); i++) {
            if(isMouseInTab(mouseX, mouseY, i))
                return list.get(i);
        }
        return null;
    }

    private boolean isMouseInTab(int mouseX, int mouseY, int index) {
        int size = getTabWidth();
        return mouseX > xTopLeft + index * size && mouseX < xTopLeft + index * size + size && mouseY > yTopLeft && mouseY < yTopLeft + tabHeight;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void drawMainBackground() {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(xTopLeft, yTopLeft + tabHeight, 0, tabHeight, texWidth, texHeight - tabHeight);
    }

    public void drawTab(int x, int size, String text, TabState state) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        if(size < 32) size = 32;
        int spaceToFillOut = size - 32;
        int spaceCount = 0;

        this.blit(xTopLeft + x, yTopLeft, 0, state.getySpriteOffset(), 16, 16);
        while(spaceToFillOut > spaceCount) {
            if(spaceToFillOut - spaceCount < 16) {
                System.out.println("last one " + (spaceToFillOut - spaceCount));
                this.blit(xTopLeft + x + spaceCount, yTopLeft, 16, state.getySpriteOffset(), spaceToFillOut - spaceCount, 16);
                spaceCount = spaceToFillOut;
            }
            else {
                System.out.println("average 16");
                this.blit(xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), 16, 16);
                spaceCount += 16;
            }
        }
        this.blit(xTopLeft + x + size - tabEdgeWidth, yTopLeft, 32, state.getySpriteOffset(), 16, 16);

        drawCenteredString(font, text, xTopLeft + x + size/2, yTopLeft + (tabHeight - font.FONT_HEIGHT) / 2 + 2, 0xFFFFFF);
    }

    private void drawTabs(int mouseX, int mouseY) {
        List<AbstractTab> options = getActiveTabs();
        int tabSize = getTabWidth();
        for(int i = 0; i < options.size(); i++) {
            drawTab(tabSize * i, tabSize, options.get(i).getName(), getTabState(mouseX, mouseY, options.get(i)));
        }
    }

    //meta
    public void addWidget(Widget widget) {
        this.children.add(widget);
    }

    enum TabState {
        NEUTRAL(texHeight),
        HIGHLIGHTED(texHeight + 16),
        HOVERED(texHeight + 32);

        public int getySpriteOffset() {
            return ySpriteOffset;
        }

        private int ySpriteOffset;

        TabState(int ySpriteOffset) {
            this.ySpriteOffset = ySpriteOffset;
        }
    }
}
