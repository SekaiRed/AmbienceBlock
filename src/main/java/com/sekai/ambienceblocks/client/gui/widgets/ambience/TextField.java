package com.sekai.ambienceblocks.client.gui.widgets.ambience;

import com.google.common.base.Predicate;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.ITextComponent;

public class TextField extends Widget {
    GuiTextField internalField;

    //font, getNeighbourX(textMinDelay), getRowY(0), 50, 20, new TextComponentString("")
    public TextField(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
        internalField = new GuiTextField(0, font, x, y, width, height);
    }

    @Override
    public void tick() {
        super.tick();
        //can't use a getter setter so this is the next best thing
        if(internalField.x != x || internalField.y != y) {
            internalField.x = x;
            internalField.y = y;
        }
    }

    @Override
    public void render(int pX, int pY, AmbienceScreen.Layer layer) {
        super.render(pX, pY, layer);
        if (this.isVisible())
            internalField.drawTextBox();
    }

    //needed to remove focus on fields
    @Override
    public boolean clicked(double pX, double pY) {
        return true;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        //without the active check you can click and edit text of inactive widgets
        if(active)
            internalField.mouseClicked(mouseX, mouseY, button);
    }

    //TODO maybe bring back setX and setY to notify instead of this
    @Override
    public void forceUpdatePosition() {
        internalField.x = x;
        internalField.y = y;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() || internalField.getVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        internalField.setVisible(visible);
    }

    @Override
    public void setFocused(boolean isFocusedIn) {
        super.setFocused(isFocusedIn);
        internalField.setFocused(isFocusedIn);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused() || internalField.isFocused();
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        return internalField.textboxKeyTyped(typedChar, keyCode);
    }

    public void setText(String textIn)
    {
        internalField.setText(textIn);
    }

    public String getText()
    {
        return internalField.getText();
    }

    public void setValidator(Predicate<String> theValidator)
    {
        internalField.setValidator(theValidator);
    }

    public void setMaxStringLength(int length)
    {
        internalField.setMaxStringLength(length);
    }

    public int getMaxStringLength()
    {
        return internalField.getMaxStringLength();
    }

    public int getCursorPosition()
    {
        return internalField.getCursorPosition();
    }

    public boolean getEnableBackgroundDrawing()
    {
        return internalField.getEnableBackgroundDrawing();
    }

    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn)
    {
        internalField.setEnableBackgroundDrawing(enableBackgroundDrawingIn);
        //this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    public void setTextColor(int color)
    {
        internalField.setTextColor(color);
        //this.enabledColor = color;
    }

    public void setDisabledTextColour(int color)
    {
        internalField.setDisabledTextColour(color);
        //this.disabledColor = color;
    }
}
