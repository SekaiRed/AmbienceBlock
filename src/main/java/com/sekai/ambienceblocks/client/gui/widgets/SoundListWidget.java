package com.sekai.ambienceblocks.client.gui.widgets;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import com.sekai.ambienceblocks.util.Unused;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

//Maybe I could implement it with the unused icons
@Unused(type = Unused.Type.POTENTIAL)
public class SoundListWidget extends Widget {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");
    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;
    protected static final int separation = 8;

    private StringListWidget list;

    public SoundListWidget(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, new TextComponentString(""));

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        //list = new StringListWidget(xTopLeft);
    }
}
