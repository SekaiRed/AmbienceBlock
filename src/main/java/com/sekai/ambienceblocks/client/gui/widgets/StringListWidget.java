package com.sekai.ambienceblocks.client.gui.widgets;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class StringListWidget extends Widget {
    protected final IPressable onPress;
    //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");
    private final FontRenderer font;

    public ArrayList<String> list = new ArrayList<String>();

    public int selectionIndex = 0;
    public float scrollIndex;

    int separation;
    int optionHeight;

    public StringListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, FontRenderer font, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, new TextComponentString(""));
        this.separation = separation;
        if(optionHeight < font.FONT_HEIGHT) optionHeight = font.FONT_HEIGHT;
        this.optionHeight = optionHeight;
        this.font = font;
        this.onPress = onPress;
    }

    @Override
    public void render(int mouseX, int mouseY, AmbienceScreen.Layer layer) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        if(!this.isVisible())
            return;

        renderGeneric(x - 1, y - 1, width + 2, height + 2, 0.62745f, 0.62745f, 0.62745f, 1f);

        renderBackground();

        double scale = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();//Minecraft.getMinecraft().ingameGUI.getGuiScaleFactor();

        //TODO I'm not sure if displayHeight is a good replacement for mc.getMainWindow().getHeight() from 1.15+
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (this.x * scale), (int) (Minecraft.getMinecraft().displayHeight - ((this.y + height) * scale)),
                (int) (this.width * scale), (int) (this.height * scale));

        GL11.glPushMatrix();

        for(int i = 0; i < list.size(); i++) {
            //renderGeneric(getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 1f - i/((float)list.size() - 1), i/((float)list.size() - 1), 1f, 1f);
            if(selectionIndex == i)
                renderGeneric(getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 0.8f, 0.8f, 0f, 1f);
            else
                renderGeneric(getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 1f, 1f, 1f, 1f);
            //renderGeneric(getElementX() + 1, getElementY(i) + 1, getElementWidth() - 2, getElementHeight() - 2, 0f, 0f, 0f, 1f);
            renderGeneric(getElementX() + 1, getElementY(i) + 1, getElementWidth() - 2, getElementHeight() - 2, (1f - i/((float)list.size() - 1)) * 0.8f, i/((float)list.size() - 1) * 0.8f,  0.8f, 1f);
            drawElementString(i);
        }

        renderGeneric(x + width - 8, y, 8, height, 0.5f, 0.5f, 0.5f, 0.5f);
        renderGeneric(getScrollBarX(), getScrollBarY(), getScrollBarWidth(), getScrollBarHeight(), 0.9f, 0.9f, 0.9f, 1f);

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawElementString(int index) {
        int color = 0xFFFFFF;
        if(selectionIndex == index) color = 0xFFFF00;
        //font.drawStringWithShadow(list.get(index), (float)x + separation, (float)y + getyFontOffset() + separation + (optionHeight + separation) * index + getScrollOffsetY(), color);
        font.drawStringWithShadow(list.get(index), getElementX() + separation, getElementY(index) + getyFontOffset(), color);
    }

    public String getElementString() {
        return list.get(selectionIndex);
    }

    private int getElementX() {
        return x + separation;
    }

    private int getElementY(int index) {
        return (int) (y + separation + (optionHeight + separation) * index + getScrollOffsetY());
    }

    private int getElementWidth() {
        return width - separation * 2;
    }

    private int getElementHeight() {
        return optionHeight;
    }

    /*public boolean mouseScrolled(double x, double y, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        addScroll(-amount);

        return true;
    }*/

    @Override
    public void mouseScrolled(float amount) {
        if (amount > 1f) {
            amount = 1f;
        }

        if (amount < -1f) {
            amount = -1f;
        }

        addScroll(-amount);

        //return true;
    }

    /*@Override
    public void mouseScrolled(float amount) {
        super.mouseScrolled(amount);
        //addScroll(amount);
        mouseScrolled()
    }*/

    public void resetScroll() {
        scrollIndex = 0;
    }

    private void addScroll(double amount) {
        //amount = ((optionHeight / (double) getTotalListHeight()) * amount);
        amount = (optionHeight / ((double) getTotalListHeight() - height)) * amount;
        amount *= 2D;
        if(scrollIndex + amount < 0f) {
            scrollIndex = 0f;
            return;
        }

        if(scrollIndex + amount > 1f) {
            scrollIndex = 1f;
            return;
        }

        scrollIndex += amount;
    }

    private void setScroll(float value) {
        scrollIndex = value;
        if(scrollIndex > 1f) scrollIndex = 1f;
        if(scrollIndex < 0f) scrollIndex = 0f;
    }

    private int getScrollBarX() {
        return x + width - getScrollBarWidth() - 1;
    }

    private int getScrollBarY() {
        return (int) (y + (height - getScrollBarHeight()) * scrollIndex);
    }

    private int getScrollBarWidth() {
        return 6;
    }

    private int getScrollBarHeight() {
        int value = 1;
        if(getTotalListHeight() < height)
            value = height;
        else
            value = (int) ((height / (float) getTotalListHeight()) * (float) height);

        if(value < 1) value = 1;
        return value;
    }

    private float getScrollOffsetY() {
        if(getTotalListHeight() < height)
            return 0;
        else
            return (getTotalListHeight() - height) * -scrollIndex;
    }

    private int getTotalListHeight() {
        return separation + list.size() * (optionHeight + separation);
    }

    private void renderBackground() {
        int left = x;
        int top = y;
        int right = x + width;
        int bottom = y + height;

        AmbienceScreen.drawColoredRect(left, top, right, bottom, 0f, 0f, 0f, 1f);

        /*float left = x;
        float top = y;
        float right = x + width;
        float bottom = y + height;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glColor4f(0f, 0f, 0f, 1f);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(left, bottom, 0).endVertex();
        buffer.pos(right, bottom, 0).endVertex();
        buffer.pos(right, top, 0).endVertex();
        buffer.pos(left, top, 0).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();*/
    }

    private void renderGeneric(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        //changed float to int, might break the commented out code
        int left = x;
        int top = y;
        int right = x + width;
        int bottom = y + height;

        AmbienceScreen.drawColoredRect(left, top, right, bottom, red, green, blue, alpha);

        /*RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glColor4f(red, green, blue, alpha);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(left, bottom, 0).endVertex();
        buffer.pos(right, bottom, 0).endVertex();
        buffer.pos(right, top, 0).endVertex();
        buffer.pos(left, top, 0).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();*/
    }

    private float getyFontOffset() {
        return (optionHeight - font.FONT_HEIGHT) / 2.0f;
    }

    public void addElement(String string) {
        list.add(string);
    }

    public void addElements(List<String> strings) {
        list.addAll(strings);
    }

    public void clearList() {
        list.clear();
    }

    public void clickElement(double pX, double pY) {
        if(pX > getElementX() && pX < getElementX() + getElementWidth()) {
            for (int i = 0; i < list.size(); i++) {
                if (pY > getElementY(i) && pY < getElementY(i) + getElementHeight()) {
                    playDownSound(Minecraft.getMinecraft().getSoundHandler());

                    boolean onClick = false;
                    boolean onDoubleClick = false;

                    if(onPress != null) {
                        if(getSelectionIndex() != i)
                            onClick = true;//onPress.onClick(this, i, list.get(i));
                        else
                            onDoubleClick = true;//onPress.onDoubleClick(this, i, list.get(i));
                    }
                    setSelectionIndex(i);

                    if(onClick)
                        onPress.onClick(this, i, list.get(i));
                    if(onDoubleClick)
                        onPress.onDoubleClick(this, i, list.get(i));

                    return;
                }
            }
        }
    }

    public int getAmountOfElements() {
        return list.size();
    }

    public String getSelectionContent() {
        return list.get(selectionIndex);
    }

    public int getSelectionIndex() {
        return selectionIndex;
    }

    public void setSelectionIndex(int selectionIndex) {
        this.selectionIndex = selectionIndex;
    }

    public void setSelectionIndexToLast() {
        this.selectionIndex = list.size() - 1;
    }

    public void setSelectionByString(String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(name)) {
                setSelectionIndex(i);
                //scrollIndex = (float) ((getElementY(i) - y - separation) / ((double) getTotalListHeight() - height));
                setScroll(0f);
                setScroll((float) ((getElementY(i) - y - separation) / ((double) getTotalListHeight() - height)));
            }
        }
    }

    @Override
    public void mouseClicked(int pX, int pY, int pType) {
        if (this.active && this.isVisible()) {
            if (this.isValidClickButton(pType)) {
                boolean flag = this.clicked(pX, pY);
                if (flag) {
                    clickElement(pX, pY);
                }
            }
        }
    }

    public interface IPressable {
        void onClick(StringListWidget list, int index, String name);
        void onDoubleClick(StringListWidget list, int index, String name);
    }
}
