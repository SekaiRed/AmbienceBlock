package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.client.gui.widgets.ScrollListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AmbienceScreen extends GuiScreen {
    //main texture i use throughout the mod's GUI
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final Gui gui = new Gui(); //easy access to all of the Gui methods without creating 20 of them per render tick

    public Minecraft mc;
    public FontRenderer font;

    //unified widget list, the only special case is textfields for now (TODO could be changed with an optional keyHandling method in Widget)
    public final List<Widget> widgets = new ArrayList<>();

    public AmbienceScreen() {
        this.mc = Minecraft.getMinecraft();
        this.font = mc.fontRenderer;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < Layer.values().length; ++i) {
            for (Widget widget : widgets) {
                if(widget.getLayer().equals(Layer.values()[i]))
                    widget.render(mouseX, mouseY, Layer.values()[i]);
            }
        }
        if(AmbienceController.debugMode) drawDebug();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (Widget widget : widgets) {
            widget.tick();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            for(Widget widget : widgets) {
                if(widget.clicked(mouseX, mouseY)) {
                    widget.mouseClicked(mouseX, mouseY, mouseButton);

                    //TODO really hacky and disgusting solution, bring back boolean results
                    if(widget instanceof StringListWidget) return;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        mouseScroll(Mouse.getEventDWheel());
    }

    public void mouseScroll(float amount) {
        if (amount != 0D)
        {
            for(Widget widget : widgets) {
                widget.mouseScrolled(amount);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        //System.out.println("keycode : " + keyCode);
        //esc was pressed
        if(keyCode == 1)
            Minecraft.getMinecraft().displayGuiScreen(null);

        //iterate through all widgets and handle the textfield code, is this even gonna work?
        for(Widget w : widgets) {
            if(w instanceof TextField)
                ((TextField) w).textboxKeyTyped(typedChar, keyCode);
        }
    }

    public void addWidget(Widget widget) {
        if(!widgets.contains(widget))
            widgets.add(widget);
    }

    public void clearWidgets() {
        widgets.clear();
    }

    protected void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    public enum Layer {
        LOWEST(0f),
        HIGHEST(1f);

        private final float zLevel;

        Layer(float zLevel) {
            this.zLevel = zLevel;
        }

        public float getZLevel() {
            return zLevel;
        }

        private Layer increase() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        private Layer decrease() {
            return values()[(this.ordinal() - 1) % values().length];
        }
    }

    private void drawDebug() {
        for(Widget w : widgets) {
            if(!w.isVisible())
                continue;

            float[] colors = new float[]{1f, 1f, 1f, 0.25f};
            if(w instanceof Button)
                colors[0] = 0f;
            if(w instanceof Checkbox)
                colors[1] = 0f;
            if(w instanceof TextField)
                colors[2] = 0f;
            if(w instanceof TextInstance) {
                colors[1] = 0f;
                colors[2] = 0f;
            }
            if(w instanceof ScrollListWidget) {
                colors[0] = 0f;
                colors[2] = 0f;
            }
            if(w instanceof StringListWidget) {
                colors[0] = 0f;
                colors[1] = 0f;
            }
            drawColoredRectAlt(w.x, w.y, w.width, w.height, colors[0], colors[1], colors[2], colors[3]);
        }
    }

    //utility
    public static void drawColoredRectAlt(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        drawColoredRect(x, y, x + width, y+ height, red, green, blue, alpha);
    }

    public static void drawColoredRect(int left, int top, int right, int bottom, float red, float green, float blue, float alpha) {
        int zLevel = 0; //????

        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)zLevel).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)zLevel).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)zLevel).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)zLevel).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        /*float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();*/
    }

    /*public static void drawTexturedModalRectWithSize(int x, int y, int u, int v, int width, int height, int spriteWidth, int spriteHeight, float zLevel)
    {
        final float uScale = 1f / spriteWidth;
        final float vScale = 1f / spriteHeight;

        GlStateManager.func_227740_m_();
        GlStateManager.func_227644_a_(GlStateManager.SourceFactor.SRC_ALPHA.field_225655_p_, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.field_225654_o_, GlStateManager.SourceFactor.ONE.field_225655_p_, GlStateManager.DestFactor.ZERO.field_225654_o_);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(org.lwjgl.opengl.GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.func_225582_a_(x        , y + height, zLevel).func_225583_a_( u          * uScale, ((v + height) * vScale)).endVertex();
        wr.func_225582_a_(x + width, y + height, zLevel).func_225583_a_((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.func_225582_a_(x + width, y         , zLevel).func_225583_a_((u + width) * uScale, ( v           * vScale)).endVertex();
        wr.func_225582_a_(x        , y         , zLevel).func_225583_a_( u          * uScale, ( v           * vScale)).endVertex();
        tessellator.draw();

        GlStateManager.func_227737_l_();
    }*/

    /* code landfill, please keep moving and not look here
    drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
            this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
            this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
            float f = 85.0F;
            float f1 = 180.0F;
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f2 = 32.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)(this.width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double)(this.width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double)(this.width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double)(this.width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
            tessellator.draw();
            this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle"), this.width / 2, 105, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm1"), this.width / 2, 125, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm2"), this.width / 2, 135, 16777215);
            this.confirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.cancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
     */
}
