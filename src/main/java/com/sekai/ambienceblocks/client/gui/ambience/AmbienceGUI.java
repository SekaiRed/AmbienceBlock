package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.*;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AmbienceGUI extends Screen {
    private final AmbienceTileEntity target;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    public static final int tabEdgeWidth = 16, tabHeight = 16;

    private static final int intNull = Integer.MAX_VALUE;

    private MainTab mainTab;
    private BoundsTab boundsTab;
    private PriorityTab priorityTab;
    private DelayTab delayTab;
    private MiscTab miscTab;

    private AbstractTab highlightedTab;

    private Button confirmChanges;

    private boolean help;
    private Button bHelp;

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

        if(help && highlightedTab != null)
            highlightedTab.renderToolTip(mouseX, mouseY);

        drawHelp();

        /*if(highlightIndex == 0)
            mainTab.render(mouseX, mouseY, partialTicks);*/
    }

    @Override
    protected void init() {
        super.init();

        //SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        /*for (ResourceLocation element : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
            System.out.println(element.getNamespace() + ":" + element.getPath());
        }*/

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        mainTab = new MainTab(this);
        setHighlightedTab(mainTab);

        boundsTab = new BoundsTab(this);
        boundsTab.deactivate();

        priorityTab = new PriorityTab(this);
        priorityTab.deactivate();

        delayTab = new DelayTab(this);
        delayTab.deactivate();

        miscTab = new MiscTab(this);
        miscTab.deactivate();

        confirmChanges = addButton(new Button(xTopLeft + 8, yTopLeft + texHeight + 8, 100, 20, "Confirm Changes", button -> {
            saveDataToTile();
        }));

        help = false;
        bHelp = addButton(new Button(xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 16, 16, "", button -> { clickHelp(); }));

        loadDataFromTile();
    }

    private void clickHelp() {
        help = !help;
    }

    private void loadDataFromTile() {
        /*mainTab.setFieldFromData(target.data);

        for(AbstractTab tab : getActiveTabs()) if(!(tab instanceof MainTab)) tab.setFieldFromData(target.data);*/
        setData(target.data);
    }

    private void saveDataToTile() {
        /*AmbienceTileEntityData data = new AmbienceTileEntityData();

        for(AbstractTab tab : getActiveTabs()) tab.setDataFromField(data);

        PacketHandler.NET.sendToServer(new PacketUpdateAmbienceTE(target.getPos(), data));*/
        PacketHandler.NET.sendToServer(new PacketUpdateAmbienceTE(target.getPos(), getData()));
    }

    public AmbienceTileEntityData getData() {
        AmbienceTileEntityData data = new AmbienceTileEntityData();

        for(AbstractTab tab : getActiveTabs()) tab.setDataFromField(data);

        return data;
    }

    public void setData(AmbienceTileEntityData data) {
        mainTab.setFieldFromData(data);

        for(AbstractTab tab : getActiveTabs()) if(!(tab instanceof MainTab)) tab.setFieldFromData(data);
    }

    @Override
    public void tick() {
        super.tick();

        if(highlightedTab != null)
            highlightedTab.tick();
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

        if(mainTab.usePriority.isChecked())
            list.add(priorityTab);

        if(mainTab.useDelay.isChecked())
            list.add(delayTab);

        list.add(miscTab);

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
                this.blit(xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), spaceToFillOut - spaceCount, 16);
                spaceCount = spaceToFillOut;
            }
            else {
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

    private void drawHelp() {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 48, getHelpState().getySpriteOffset(), 16, 16);
    }

    private TabState getHelpState() {
        if(bHelp.isHovered()) return TabState.HOVERED;
        if(help) return TabState.HIGHLIGHTED;
        return TabState.NEUTRAL;
    }

    //meta
    public void addWidget(Widget widget) {
        this.children.add(widget);
    }

    @Nonnull
    public <T extends Widget> T addButton(@Nonnull T widget) {
        return super.addButton(widget);
    }

    enum TabState {
        NEUTRAL(texHeight),
        HIGHLIGHTED(texHeight + 16),
        HOVERED(texHeight + 32);

        public int getySpriteOffset() {
            return ySpriteOffset;
        }

        private final int ySpriteOffset;

        TabState(int ySpriteOffset) {
            this.ySpriteOffset = ySpriteOffset;
        }
    }
}
