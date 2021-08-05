package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.client.gui.ambience.tabs.*;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.util.AmbienceType;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AmbienceGUI extends AmbienceScreen {
    private final AmbienceTileEntity target;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    public static final int tabEdgeWidth = 16, tabHeight = 16;

    private MainTab mainTab = new MainTab(this);
    private MusicTab musicTab = new MusicTab(this);
    private BoundsTab boundsTab = new BoundsTab(this);
    //private FuseTab fuseTab = new FuseTab(this);
    private PriorityTab priorityTab = new PriorityTab(this);
    private DelayTab delayTab = new DelayTab(this);
    private CondTab condTab = new CondTab(this);
    private MiscTab miscTab = new MiscTab(this);

    private AbstractTab highlightedTab;

    private Button confirmChanges;
    private Button cancel;

    private boolean help;
    private Button bHelp;

    private boolean initialized = false;

    public AmbienceGUI(AmbienceTileEntity target) {
        super(new TranslationTextComponent("narrator.screen.globalambiencegui"));

        this.target = target;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        drawMainBackground(matrix);

        drawTabs(matrix, mouseX, mouseY);

        if(highlightedTab != null)
            highlightedTab.render(matrix, mouseX, mouseY, partialTicks);

        if(help && highlightedTab != null)
            highlightedTab.renderToolTip(matrix, mouseX, mouseY);

        drawHelp(matrix);

        if(AmbienceController.debugMode)
            drawDebug();
    }

    private void drawDebug() {
        for(Widget widget : highlightedTab.buttons) {
            float left = widget.x;
            float top = widget.y;
            float right = widget.x + widget.getWidth();
            float bottom = widget.y + widget.getHeightRealms();

            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(GL11.GL_SMOOTH);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            GL11.glColor4f(0.0f, 1.0f, 1.0f, 0.2f);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            buffer.pos(left, bottom, 0).endVertex();
            buffer.pos(right, bottom, 0).endVertex();
            buffer.pos(right, top, 0).endVertex();
            buffer.pos(left, top, 0).endVertex();
            tessellator.draw();

            RenderSystem.shadeModel(GL11.GL_FLAT);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
        }
        for(Widget widget : highlightedTab.widgets) {
            float left = widget.x;
            float top = widget.y;
            float right = widget.x + widget.getWidth();
            float bottom = widget.y + widget.getHeightRealms();

            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(GL11.GL_SMOOTH);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            GL11.glColor4f(1.0f, 0.0f, 1.0f, 0.2f);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            buffer.pos(left, bottom, 0).endVertex();
            buffer.pos(right, bottom, 0).endVertex();
            buffer.pos(right, top, 0).endVertex();
            buffer.pos(left, top, 0).endVertex();
            tessellator.draw();

            RenderSystem.shadeModel(GL11.GL_FLAT);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
        }
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        List<AbstractTab> tabs = getAllTabs();

        if(!initialized)
        {
            for(AbstractTab tab : tabs) {
                tab.updateMetaValues(this);
                tab.initialInit();
                tab.updateWidgetPosition();
                //if(tab instanceof MainTab) setHighlightedTab(tab);
                //else tab.deactivate();
                tab.deactivate();
                if(tab instanceof MainTab) setHighlightedTab(tab);
            }

            loadDataFromTile();

            initialized = true;
        }
        else
        {
            for(AbstractTab tab : tabs) {
                tab.updateMetaValues(this);
                tab.updateWidgetPosition();
                tab.refreshWidgets();
            }
        }

        confirmChanges = addButton(new Button(xTopLeft + 4, yTopLeft + texHeight + 4, 100, 20, new StringTextComponent("Confirm Changes"), button -> {
            saveDataToTile();

            Minecraft.getInstance().displayGuiScreen(null);
        }));

        cancel = addButton(new Button(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20, new StringTextComponent("Cancel"), button -> {
            Minecraft.getInstance().displayGuiScreen(null);
        }));

        help = false;
        bHelp = addButton(new Button(xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 16, 16, new StringTextComponent(""), button -> { clickHelp(); }));

        //loadDataFromTile();
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

    private List<AbstractTab> getAllTabs() {
        List<AbstractTab> list = new ArrayList<>();

        list.add(mainTab);

        list.add(musicTab);

        list.add(boundsTab);

        //list.add(fuseTab);

        list.add(priorityTab);

        list.add(delayTab);

        list.add(condTab);

        list.add(miscTab);

        return list;
    }

    private List<AbstractTab> getActiveTabs() {
        List<AbstractTab> list = new ArrayList<>();

        list.add(mainTab);

        try {
            if(AmbienceType.MUSIC.equals(mainTab.getType()))
                list.add(musicTab);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


        list.add(boundsTab);

        //if(mainTab.shouldFuse.isChecked())
        //    list.add(fuseTab);

        if(mainTab.usePriority.isChecked())
            list.add(priorityTab);

        if(mainTab.useDelay.isChecked() && AmbienceType.AMBIENT.equals(mainTab.getType()))
            list.add(delayTab);

        if(mainTab.useCondition.isChecked())
            list.add(condTab);

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

    public void drawMainBackground(MatrixStack matrix) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(matrix, xTopLeft, yTopLeft + tabHeight, 0, tabHeight, texWidth, texHeight - tabHeight);
    }

    public void drawTab(MatrixStack matrix, int x, int size, String text, TabState state) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        if(size < 32) size = 32;
        int spaceToFillOut = size - 32;
        int spaceCount = 0;

        this.blit(matrix, xTopLeft + x, yTopLeft, 0, state.getySpriteOffset(), 16, 16);
        while(spaceToFillOut > spaceCount) {
            if(spaceToFillOut - spaceCount < 16) {
                this.blit(matrix, xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), spaceToFillOut - spaceCount, 16);
                spaceCount = spaceToFillOut;
            }
            else {
                this.blit(matrix, xTopLeft + x + tabEdgeWidth + spaceCount, yTopLeft, 16, state.getySpriteOffset(), 16, 16);
                spaceCount += 16;
            }
        }
        this.blit(matrix, xTopLeft + x + size - tabEdgeWidth, yTopLeft, 32, state.getySpriteOffset(), 16, 16);

        drawCenteredString(matrix, font, text, xTopLeft + x + size/2, yTopLeft + (tabHeight - font.FONT_HEIGHT) / 2 + 2, 0xFFFFFF);
    }

    private void drawTabs(MatrixStack matrix, int mouseX, int mouseY) {
        List<AbstractTab> options = getActiveTabs();
        int tabSize = getTabWidth();
        String tabName;
        for(int i = 0; i < options.size(); i++) {
            //if(i == options.size() - 1)
            //    tabSize = texWidth - tabSize * i;

            if(font.getStringWidth(options.get(i).getName()) < tabSize - 8)
                tabName = options.get(i).getName();
            else
                tabName = options.get(i).getShortName();

            if(i != options.size() - 1)
                drawTab(matrix, tabSize * i, tabSize, tabName, getTabState(mouseX, mouseY, options.get(i)));
            else
                drawTab(matrix, tabSize * i, texWidth - tabSize * i, tabName, getTabState(mouseX, mouseY, options.get(i)));
        }
    }

    private void drawHelp(MatrixStack matrix) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(matrix, xTopLeft + texWidth - 16 - 8, yTopLeft + texHeight - 16 - 8, 48, getHelpState().getySpriteOffset(), 16, 16);
    }

    private TabState getHelpState() {
        if(bHelp.isHovered()) return TabState.HOVERED;
        if(help) return TabState.HIGHLIGHTED;
        return TabState.NEUTRAL;
    }

    public List<? extends IGuiEventListener> getChildrens() {
        return children;
    }

    //meta
    public void forceUpdateCondList() {
        condTab.updateGuiCondList();
    }

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
