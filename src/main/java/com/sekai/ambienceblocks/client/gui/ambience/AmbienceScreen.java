package com.sekai.ambienceblocks.client.gui.ambience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AmbienceScreen extends Screen {
    public Screen previousScreen = null;

    protected AmbienceScreen(Component titleIn) {
        super(titleIn);
    }

    public <T extends GuiEventListener & Widget & NarratableEntry> T addCustomWidget(T widget) {
        return addRenderableWidget(widget);
    }

    public void setPreviousScreen(Screen screen) {
        previousScreen = screen;
    }

    //Go to the past screen or just close it
    public void quit() {
        minecraft.setScreen(previousScreen);
    }
}
