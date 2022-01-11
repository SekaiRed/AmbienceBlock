package com.sekai.ambienceblocks.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
    public class StringListWidget extends AbstractWidget {
    protected final IPressable onPress;
    //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");
    private final Font font;

    public ArrayList<String> list = new ArrayList<String>();

    public int selectionIndex = 0;
    public float scrollIndex;

    int separation;
    int optionHeight;

    public StringListWidget(int xIn, int yIn, int widthIn, int heightIn, int separation, int optionHeight, Font font, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, new TextComponent(""));
        this.separation = separation;
        if(optionHeight < font.lineHeight) optionHeight = font.lineHeight;
        this.optionHeight = optionHeight;
        this.font = font;
        this.onPress = onPress;
    }

    @Override
    public void render(PoseStack matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);
        render(matrix, p_render_1_, p_render_2_);
    }

    public void render(PoseStack matrix, int mouseX, int mouseY) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        if(!this.visible)
            return;

        renderGeneric(matrix, x - 1, y - 1, width + 2, height + 2, 0.62745f, 0.62745f, 0.62745f, 1f);

        renderBackground(matrix);

        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        //Inspired from ScrollPanel

        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
        /*GL11.glScissor((int) (this.x * scale), (int) (Minecraft.getInstance().getWindow().getHeight() - ((this.y + height) * scale)),
                (int) (this.width * scale), (int) (this.height * scale));*/
        RenderSystem.enableScissor((int)(this.x  * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((this.y + this.height) * scale)),
                (int)(width * scale), (int)(height * scale));

        //GL11.glPushMatrix();

        for(int i = 0; i < list.size(); i++) {
            //renderGeneric(getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 1f - i/((float)list.size() - 1), i/((float)list.size() - 1), 1f, 1f);
            if(selectionIndex == i)
                renderGeneric(matrix, getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 0.8f, 0.8f, 0f, 1f);
            else
                renderGeneric(matrix, getElementX(), getElementY(i), getElementWidth(), getElementHeight(), 1f, 1f, 1f, 1f);
            //renderGeneric(getElementX() + 1, getElementY(i) + 1, getElementWidth() - 2, getElementHeight() - 2, 0f, 0f, 0f, 1f);
            renderGeneric(matrix, getElementX() + 1, getElementY(i) + 1, getElementWidth() - 2, getElementHeight() - 2, (1f - i/((float)list.size() - 1)) * 0.8f, i/((float)list.size() - 1) * 0.8f,  0.8f, 1f);
            drawElementString(matrix, i);
        }

        renderGeneric(matrix, x + width - 8, y, 8, height, 0.5f, 0.5f, 0.5f, 0.5f);
        renderGeneric(matrix, getScrollBarX(), getScrollBarY(), getScrollBarWidth(), getScrollBarHeight(), 0.9f, 0.9f, 0.9f, 1f);

        //GL11.glPopMatrix();

        //GL11.glDisable(GL11.GL_SCISSOR_TEST);>
        RenderSystem.disableScissor();
    }

    private void drawElementString(PoseStack matrix, int index) {
        int color = 0xFFFFFF;
        if(selectionIndex == index) color = 0xFFFF00;
        //font.drawStringWithShadow(list.get(index), (float)x + separation, (float)y + getyFontOffset() + separation + (optionHeight + separation) * index + getScrollOffsetY(), color);
        font.drawShadow(matrix, list.get(index), getElementX() + separation, getElementY(index) + getyFontOffset(), color);
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

    public boolean mouseScrolled(double x, double y, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        addScroll(-amount);

        return true;
    }

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

    private void renderBackground(PoseStack stack) {
        /*float left = x;
        float top = y;
        float right = x + width;
        float bottom = y + height;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        //RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        //RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        GL11.glColor4f(0f, 0f, 0f, 1f);
        //buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION); >
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(left, bottom, 0).endVertex();
        buffer.vertex(right, bottom, 0).endVertex();
        buffer.vertex(right, top, 0).endVertex();
        buffer.vertex(left, top, 0).endVertex();
        tessellator.end();

        //RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        //RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();*/

        renderGeneric(stack, x, y, width, height, 0f, 0f, 0f, 1f);
    }

    private void renderGeneric(PoseStack stack, int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        float left = x;
        float top = y;
        float right = x + width;
        float bottom = y + height;

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
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        //fillGradient(stack.last().pose(), bufferbuilder, p_168742_, p_168743_, p_168744_, p_168745_, p_168748_, p_168746_, p_168747_);
        Matrix4f matrix4f = stack.last().pose(); //??? why tho
        bufferbuilder.vertex(matrix4f, left, bottom, 0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, right, bottom, 0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, right, top, 0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, left, top, 0).color(red, green, blue, alpha).endVertex();
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private float getyFontOffset() {
        return (optionHeight - font.lineHeight) / 2.0f;
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
                    playDownSound(Minecraft.getInstance().getSoundManager());

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
    public boolean mouseClicked(double pX, double pY, int pType) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pType)) {
                boolean flag = this.clicked(pX, pY);
                if (flag) {
                    clickElement(pX, pY);
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, new TranslatableComponent("narration.string_list"));
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onClick(StringListWidget list, int index, String name);
        void onDoubleClick(StringListWidget list, int index, String name);
    }
}
