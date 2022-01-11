package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseSoundGUI extends AmbienceScreen implements StringListWidget.IPressable {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    //private Screen prevScreen;
    private EditBox targetField;
    public Font font;

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    Minecraft mc = Minecraft.getInstance();

    StringListWidget list;
    Button play;
    Button stop;
    Button cancel;

    private String selectedDomain = "";
    private String selected = "";
    private List<String> activeList = new ArrayList<>();
    private String biasSelectionName = "";

    SimpleSoundInstance previewSound;
    boolean closing = false;
    //SimpleSound.master(new SoundEvent(new ResourceLocation(resultSound)), 1.0f, 0.75f)

    public ChooseSoundGUI(Screen prevScreen, EditBox targetField) {
        super(new TranslatableComponent("narrator.screen.choosesound"));

        this.font = mc.font;
        //this.prevScreen = prevScreen;
        setPreviousScreen(prevScreen);
        this.targetField = targetField;

        String previousString = targetField.getValue();

        if(previousString.split(":").length <= 2) {
            if(previousString.split(":").length == 1) {
                selectedDomain = "minecraft";

                selected = previousString;
                boolean validate = false;

                for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                    if(element.getNamespace().equals(selectedDomain) && element.getPath().equals(selected)) validate = true;
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
                    for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                        if(element.getNamespace().equals(selectedDomain) && element.getPath().equals(selected)) validate = true;
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        xTopLeft = (this.width - texWidth) / 2;
        yTopLeft = (this.height - texHeight) / 2;

        play = new Button(xTopLeft + separation/4, yTopLeft + texHeight + separation/4, 60, 20, new TextComponent("Play"), button -> {
            playSoundPreview();
        });
        addCustomWidget(play);

        stop = new Button(xTopLeft + 60 + separation/2, yTopLeft + texHeight + separation/4, 60, 20, new TextComponent("Stop"), button -> {
            stopSoundPreview();
        });
        addCustomWidget(stop);

        cancel = new Button(xTopLeft + texWidth - 80 - separation/4, yTopLeft + texHeight + 4, 80, 20, new TextComponent("Cancel"), button -> {
            //mc.setScreen(prevScreen);
            stopSoundPreview();
            quit();
        });
        addCustomWidget(cancel);

        /*cancel = addButton(new Button(xTopLeft + texWidth - 80 - 4, yTopLeft + texHeight + 4, 80, 20, new StringTextComponent("Cancel"), button -> {
            minecraft.displayGuiScreen(null);
        }));*/

        addCustomWidget(list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
            @Override
            public void onClick(StringListWidget list, int index, String name) {

            }

            @Override
            public void onDoubleClick(StringListWidget list, int index, String name) {
                if(selectedDomain.isEmpty()) {
                    selectedDomain = name;
                    updateList();
                } else {
                    if(name.equals("<...>")) {
                        if(selected.isEmpty()) {
                            selectedDomain = "";
                        } else {
                            int firstIndex = selected.lastIndexOf(".", selected.lastIndexOf(".") - 1) + 1;
                            int lastIndex = selected.lastIndexOf(".") + 1;

                            selected = selected.replaceAll(selected.substring(firstIndex, lastIndex), "");
                        }
                        updateList();
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
            previewSound = SimpleSoundInstance.forUI(new SoundEvent(new ResourceLocation(resultSound)), 1.0f, 0.75f);
            mc.getSoundManager().play(previewSound);
        }
        //.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void stopSoundPreview() {
        if(previewSound != null) {
            mc.getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    public void updateList() {
        activeList.clear();
        list.clearList();
        list.setSelectionIndex(0);
        list.resetScroll();
        if(selectedDomain.equals("")) {
            for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                if(!activeList.contains(element.getNamespace())) activeList.add(element.getNamespace());
            }
        } else {
            for (ResourceLocation element : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
                if(selectedDomain.equals(element.getNamespace())) {
                    //String name = element.getPath().replace(selected,"").split("\\.")[0];
                    String name = element.getPath().replaceAll("\\b" + selected + "\\b", "").split("\\.")[0];
                    if(element.getPath().replace(selected,"").chars().filter(ch -> ch == '.').count() > 0) {
                        //is a folder
                        name = "<" + name + ">";
                    }  //is not a folder

                    if(element.getPath().contains(selected) && !activeList.contains(name)) activeList.add(name);
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
    public void render(PoseStack matrix, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrix, p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground(matrix);

        drawCenteredString(matrix, font, selectedDomain + ":" + selected, xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        list.render(matrix, p_render_1_, p_render_2_);

        play.render(matrix, p_render_1_, p_render_2_, p_render_3_);
        stop.render(matrix, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void onClose() {
        mc.getSoundManager().stop(previewSound);
        /*if(!closing) {
            closing = true;
            mc.displayGuiScreen(prevScreen);
        }*/
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onClick(StringListWidget list, int index, String name) {
    }

    @Override
    public void onDoubleClick(StringListWidget list, int index, String name) {
        targetField.setValue(name);
        mc.getSoundManager().stop(previewSound);
        quit();
        //mc.setScreen(prevScreen);
    }

    public void onConfirm() {
        targetField.setValue(selectedDomain + ":" + selected);
        //mc.setScreen(prevScreen);
        quit();
        mc.getSoundManager().stop(previewSound);
        //targetField.setText(list.getElementString());
        targetField.setValue(selectedDomain + ":" + selected);
    }

    public void drawMainBackground(PoseStack matrix) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        this.blit(matrix, xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }
}

