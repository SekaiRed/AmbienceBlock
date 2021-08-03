package com.sekai.ambienceblocks.client.gui.widgets.ambience;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import com.sekai.ambienceblocks.util.Unused;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Widget {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    public int width;
    public int height;
    public int x;
    public int y;
    public ITextComponent message;
    private AmbienceScreen.Layer layer;
    public boolean active = true;
    private boolean visible = true;
    public boolean isHovered = false;
    private boolean focused;

    public Widget(int x, int y, int width, int height, ITextComponent message) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
        this.layer = AmbienceScreen.Layer.LOWEST;
    }

    public void tick() {}

    public void render(int pX, int pY, AmbienceScreen.Layer layer) {
        if (this.visible) {
            this.isHovered = pX >= this.x && pY >= this.y && pX < this.x + this.width && pY < this.y + this.height;
        }
    }

    /*public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
    }*/

    public boolean clicked(double pX, double pY) {
        return this.active && pX >= (double)this.x && pY >= (double)this.y && pX < (double)(this.x + this.width) && pY < (double)(this.y + this.height);
    }

    public void onClick(double mouseX, double mouseY) {
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.active) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    //this.playDownSound(Minecraft.getMinecraft().getSoundHandler());
                    this.onClick(mouseX, mouseY);
                }
            }
        }
    }

    public void mouseScrolled(float amount) {}

    public boolean hasSubWidgets() {
        return false;
    }

    public List<Widget> getSubWidgets() {
        return new ArrayList<>();
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    //called when the position is modified, only used by a few widgets (TextField)
    public void forceUpdatePosition() {}

    public ITextComponent getMessage() {
        return this.message;
    }

    public void setMessage(ITextComponent component) {
        message = component;
    }

    public AmbienceScreen.Layer getLayer() {
        return layer;
    }

    public void setLayer(AmbienceScreen.Layer layer) {
        this.layer = layer;
    }

    public boolean isHovered() {
        return this.isHovered || this.focused;
    }

    @Unused(type = Unused.Type.REMOVE)
    public boolean changeFocus(boolean focus) {
        if (this.active && this.visible) {
            this.focused = !this.focused;
            this.onFocusedChanged(this.focused);
            return this.focused;
        } else {
            return false;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void onFocusedChanged(boolean focused) {
    }

    public boolean isFocused() {
        return this.focused;
    }

    protected void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void playDownSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
