package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ChooseSoundGUI extends AmbienceScreen implements StringListWidget.IPressable {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    private AmbienceScreen prevScreen;
    private TextField targetField;
    public FontRenderer font;

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    StringListWidget list;
    Button play;
    Button stop;

    private String selectedDomain = "";
    private String selected = "";
    private List<String> activeList = new ArrayList<>();
    private String biasSelectionName = "";

    PositionedSoundRecord previewSound;
    boolean closing = false;
    //SimpleSound.master(new SoundEvent(new ResourceLocation(resultSound)), 1.0f, 0.75f)

    //reflection ;w;
    public Field soundRegistryField;// = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_217948_a");
    public SoundRegistry soundRegistry;

    public ChooseSoundGUI(AmbienceScreen prevScreen, TextField targetField) {
        this.font = mc.fontRenderer;
        this.prevScreen = prevScreen;
        this.targetField = targetField;

        soundRegistryField = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147697_e");
        soundRegistryField.setAccessible(true);
        try {
            soundRegistry = (SoundRegistry) soundRegistryField.get(mc.getSoundHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //I can't get sounds
        //if(soundRegistry == null)
        Set<ResourceLocation> allSounds = soundRegistry.getKeys();

        String previousString = targetField.getText();

        if(previousString.split(":").length <= 2) {
            if(previousString.split(":").length == 1) {
                selectedDomain = "minecraft";

                selected = previousString;
                boolean validate = false;

                for (ResourceLocation element : allSounds) {
                    if(element.getResourceDomain().equals(selectedDomain) && element.getResourcePath().equals(selected)) validate = true;
                }

                if(validate) {
                    int firstIndex = selected.lastIndexOf(".") + 1;

                    biasSelectionName = selected.substring(firstIndex);

                    selected = selected.replaceAll(selected.substring(firstIndex), "");
                } else {
                    selectedDomain = ""; selected = "";
                }
            } else {
                selectedDomain = previousString.split(":")[0];
                if(!selectedDomain.equals("")) {
                    selected = previousString.split(":")[1];

                    boolean validate = false;
                    for (ResourceLocation element : allSounds) {
                        if(element.getResourceDomain().equals(selectedDomain) && element.getResourcePath().equals(selected)) validate = true;
                    }

                    if(validate) {
                        int firstIndex = selected.lastIndexOf(".") + 1;

                        biasSelectionName = selected.substring(firstIndex);

                        selected = selected.replaceAll(selected.substring(firstIndex), "");
                    } else {
                        selectedDomain = ""; selected = "";
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        play = new Button(xTopLeft + separation/4, yTopLeft + texHeight + separation/4, 60, 20, new TextComponentString("Play"), button -> {
            playSoundPreview();
        });
        addWidget(play);

        stop = new Button(xTopLeft + 60 + separation/2, yTopLeft + texHeight + separation/4, 60, 20, new TextComponentString("Stop"), button -> {
            stopSoundPreview();
        });
        addWidget(stop);

        addWidget(list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(selectedDomain.equals("")) {
                    selectedDomain = name;
                    updateList();
                } else {
                    if(name.equals("<...>")) {
                        if(selected.equals("")) {
                            selectedDomain = "";
                            updateList();
                        } else {
                            int firstIndex = selected.lastIndexOf(".", selected.lastIndexOf(".") - 1) + 1;
                            int lastIndex = selected.lastIndexOf(".") + 1;

                            selected = selected.replaceAll(selected.substring(firstIndex, lastIndex), "");
                            updateList();
                        }
                    } else {
                        if(name.contains("<")) {
                            name = name.replace("<", "").replace(">", "");
                            selected += name + ".";
                            updateList();
                        } else {
                            selected += name;
                            onConfirm();
                        }
                    }
                }
            }
        }));

        updateList();
    }

    public void playSoundPreview() {
        if(!selectedDomain.equals("") && !list.getSelectionContent().contains("<"))
        {
            String resultSound = selectedDomain + ":" + selected + list.getSelectionContent();
            stopSoundPreview();
            //previewSound = SimpleSound.master(new SoundEvent(new ResourceLocation(resultSound)), 1.0f, 0.75f);
            //mc.getSoundHandler().play(previewSound);

            previewSound = PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation(resultSound)), 1.0F);
            mc.getSoundHandler().playSound(previewSound);
        }
        //.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void stopSoundPreview() {
        if(previewSound != null) {
            mc.getSoundHandler().stopSound(previewSound);
            previewSound = null;
        }
    }

    public void updateList() {
        Set<ResourceLocation> allSounds = soundRegistry.getKeys();

        activeList.clear();
        list.clearList();
        list.setSelectionIndex(0);
        list.resetScroll();
        if(selectedDomain.equals("")) {
            for (ResourceLocation element : allSounds) {
                if(!activeList.contains(element.getResourceDomain())) activeList.add(element.getResourceDomain());
            }
        } else {
            for (ResourceLocation element : allSounds) {
                if(selectedDomain.equals(element.getResourceDomain())) {
                    //String name = element.getPath().replace(selected,"").split("\\.")[0];
                    String name = element.getResourcePath().replaceAll("\\b" + selected + "\\b", "").split("\\.")[0];
                    if(element.getResourcePath().replace(selected,"").chars().filter(ch -> ch == '.').count() > 0) {
                        //is a folder
                        name = "<" + name + ">";
                    }  //is not a folder

                    if(element.getResourcePath().contains(selected) && !activeList.contains(name)) activeList.add(name);
                }
            }
            Collections.sort(activeList);
            activeList.add(0, "<...>");
        }

        list.addElements(activeList);

        if(!biasSelectionName.equals("")) {
            list.setSelectionByString(biasSelectionName);
            biasSelectionName = "";
        }
    }

    @Override
    public void drawScreen(int p_render_1_, int p_render_2_, float p_render_3_) {
        drawMainBackground();

        drawCenteredString(font, selectedDomain + ":" + selected, xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        super.drawScreen(p_render_1_, p_render_2_, p_render_3_);

        /*list.render(matrix, p_render_1_, p_render_2_);

        play.render(matrix, p_render_1_, p_render_2_, p_render_3_);
        stop.render(matrix, p_render_1_, p_render_2_, p_render_3_);*/
    }

    @Override
    public void onGuiClosed() {
        mc.getSoundHandler().stopSound(previewSound);
        if(!closing) {
            closing = true;
            mc.displayGuiScreen(prevScreen);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onClick(StringListWidget list, int index, String name) {
    }

    @Override
    public void onDoubleClick(StringListWidget list, int index, String name) {
        targetField.setText(name);
        mc.getSoundHandler().stopSound(previewSound);
        mc.displayGuiScreen(prevScreen);
    }

    public void onConfirm() {
        targetField.setText(selectedDomain + ":" + selected);
        mc.displayGuiScreen(prevScreen);
        mc.getSoundHandler().stopSound(previewSound);
        //targetField.setText(list.getElementString());
        targetField.setText(selectedDomain + ":" + selected);
    }

    public void drawMainBackground() {
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }
}

