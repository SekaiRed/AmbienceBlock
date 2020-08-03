package com.sekai.ambienceblocks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DebugOverlay extends Screen {
    public DebugOverlay() {
        super(new TranslationTextComponent("narrator.screen.ambiencedebug"));
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            /*MusicController music = MusicController.instance;
            String musicQueue = "";
            for(int i = 0; i < music.channels.size(); i++)
            {
                musicQueue += music.channels.get(i).getMusicString() + " by " + music.channels.get(i).getOwner().getPos();
            }*/
            //if(Minecraft.getInstance().world != null)
            //    drawString(font, "Music Queue : ", 0,0, 0xFFFFFF);
        }
    }
}
