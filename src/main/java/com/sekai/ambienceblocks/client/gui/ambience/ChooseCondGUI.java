package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.conds.AlwaysTrueCond;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.util.CondsUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class ChooseCondGUI extends AmbienceScreen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    private StringListWidget list;

    private Button cancel;

    boolean closing = false;

    public ChooseCondGUI(EditCondGUI prevScreen) {
        super(prevScreen);
        //this.prevScreen = prevScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawMainBackground();

        drawCenteredString(font, "Choose a condition", xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    public void drawMainBackground() {
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    @Override
    public void initGui() {
        super.initGui();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                AbstractCond returnCond = new AlwaysTrueCond();
                CondsUtil.CondList[] conds = CondsUtil.CondList.values();
                for (CondsUtil.CondList cond : conds) {
                    if(cond.getDefault().getName().equals(name)) returnCond = cond.getDefault();
                }
                if(getPreviousScreen() instanceof EditCondGUI)
                    ((EditCondGUI) getPreviousScreen()).setCond(returnCond);
                //prevScreen.setCond(returnCond);
                //displayOriginalScreen();
                quitFromScreen();
            }
        });
        addWidget(list);

        CondsUtil.CondList[] conds = CondsUtil.CondList.values();
        for (CondsUtil.CondList cond : conds) {
            list.addElement(cond.getDefault().getName());
        }

        cancel = new Button(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20, new TextComponentString("Cancel"), button -> {
            quitFromScreen();
        });
        addWidget(cancel);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
