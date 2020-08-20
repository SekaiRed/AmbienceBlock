package com.sekai.ambienceblocks.client.gui.ambience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class ChooseSoundGUI extends Screen {
    private Screen prevScreen;
    public FontRenderer font = Minecraft.getInstance().fontRenderer;

    Minecraft mc = Minecraft.getInstance();

    public ChooseSoundGUI(Screen prevScreen) {
        super(new TranslationTextComponent("narrator.screen.choosesound"));

        this.font = mc.fontRenderer;
        this.prevScreen = prevScreen;
    }


    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void onClose() {
        mc.displayGuiScreen(prevScreen);
    }

    @Override
    public void tick() {
        super.tick();
    }
}

