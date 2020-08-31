package com.sekai.ambienceblocks.client.gui.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChooseSoundGUI extends Screen implements StringListWidget.IPressable {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/ambience_gui.png");

    public static final int texWidth = 256;
    public static final int texHeight = 184;
    public int xTopLeft, yTopLeft;

    private Screen prevScreen;
    private TextFieldWidget targetField;
    public FontRenderer font;
    private SoundRegistry registry = new SoundRegistry();

    protected static final int yOffset = 8;
    protected static final int separation = 16;

    Minecraft mc = Minecraft.getInstance();

    StringListWidget list;

    private String selectedDomain = "";
    private String selected = "";
    private List<String> activeList = new ArrayList<>();
    private String biasSelectionName = "";

    public ChooseSoundGUI(Screen prevScreen, TextFieldWidget targetField) {
        super(new TranslationTextComponent("narrator.screen.choosesound"));

        this.font = mc.fontRenderer;
        this.prevScreen = prevScreen;
        this.targetField = targetField;

        String previousString = targetField.getText();

        System.out.println(previousString);

        if(previousString.split(":").length <= 2) {
            if(previousString.split(":").length == 1) {
                selectedDomain = "minecraft";

                selected = previousString;
                boolean validate = false;

                for (ResourceLocation element : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
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
                    for (ResourceLocation element : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
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

        this.children.add(list = new StringListWidget(xTopLeft + separation, yTopLeft + separation + yOffset, texWidth - separation * 2, texHeight - separation * 2 - yOffset, 4, 16, font, new StringListWidget.IPressable() {
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

    public void updateList() {
        activeList.clear();
        list.clearList();
        list.setSelectionIndex(0);
        list.resetScroll();
        if(selectedDomain.equals("")) {
            for (ResourceLocation element : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
                if(!activeList.contains(element.getNamespace())) activeList.add(element.getNamespace());
            }
        } else {
            for (ResourceLocation element : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
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
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(p_render_1_, p_render_2_, p_render_3_);

        drawMainBackground();

        drawCenteredString(font, selectedDomain + ":" + selected, xTopLeft + texWidth/2, yTopLeft + separation / 2, 0xFFFFFF);

        list.render(p_render_1_, p_render_2_);
    }

    @Override
    public void onClose() {
        mc.displayGuiScreen(prevScreen);
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
        targetField.setText(name);
        mc.displayGuiScreen(prevScreen);
        System.out.println(name);
    }

    public void onConfirm() {
        //targetField.setText(list.getElementString());
        System.out.println(targetField.toString() + ", " + targetField.getText());
        mc.displayGuiScreen(prevScreen);
        //targetField.setText(list.getElementString());
        targetField.setText(selectedDomain + ":" + selected);
    }

    public void drawMainBackground() {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(xTopLeft, yTopLeft, 0, 0, texWidth, texHeight);
    }

    public void addSound(String sound) {
        String[] split = sound.split(":");
        if (split.length != 2) return;

        SoundDomain domain = registry.createOrGetDomain(split[0]);

        SoundHolderElement parent = domain;
        List<String> path = Arrays.asList(split[1].split("\\."));
        for (int i = 0; i < path.size(); i++) {
            String s = path.get(i);
            if(i == path.size() - 1) {
                SoundSound newSound = parent.addSound(sound);
                System.out.println(newSound.getTotalPath());
            } else {
                parent = parent.createOrGetFolder(s);
            }
        }
    }

    //class stuff
    //any element that can be shown in the list
    public class SoundRegistry {
        private List<SoundDomain> domains = new ArrayList<>();

        public SoundDomain createOrGetDomain(String name) {
            for (SoundDomain soundDomain : domains) {
                if (soundDomain.getAsString().equals(name))
                    return soundDomain;
            }

            SoundDomain newDomain = new SoundDomain(name);
            domains.add(newDomain);
            System.out.println(newDomain.getTotalPath());
            return newDomain;
        }
    }

    public abstract class SoundElement {
        public abstract String getAsString();
        public abstract String getTotalPath();
    }

    public abstract class SoundHolderElement extends SoundElement {
        private final List<ChildSoundElement> content = new ArrayList<>();

        public SoundFolder createOrGetFolder(String name) {
            for (ChildSoundElement soundFolder : content) {
                if (soundFolder instanceof SoundFolder)
                    if (((SoundFolder)soundFolder).getAsString().equals(name))
                        return (SoundFolder)soundFolder;
            }

            SoundFolder newFolder = new SoundFolder(this, name);
            content.add(newFolder);
            System.out.println(newFolder.getTotalPath());
            return newFolder;
        }

        public SoundSound addSound(String name) {
            SoundSound sound = new SoundSound(this, name);
            content.add(sound);
            return sound;
        }

        public abstract String getAsString();
    }

    /*public interface ParentSoundElement {
        SoundElement getChild();
    }*/
    public interface ChildSoundElement {
        SoundHolderElement getParent();
    }

    public class SoundDomain extends SoundHolderElement {
        private String name;
        private List<ChildSoundElement> content;

        public SoundDomain(String name) {
            this.name = name;
        }

        @Override
        public String getAsString() {
            return name;
        }

        @Override
        public String getTotalPath() {
            return name + ":";
        }
    }

    public class SoundFolder extends SoundHolderElement implements ChildSoundElement {
        private final String name;
        private final SoundHolderElement parent;

        public SoundFolder(SoundHolderElement parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public String getAsString() {
            return name;
        }

        @Override
        public String getTotalPath() {
            String finalPath = "";
            List<String> path = new ArrayList<>();
            path.add(name);
            SoundHolderElement e = this;
            while(!(e instanceof SoundDomain)) {
                //if(e instanceof ChildSoundElement) {
                    e = ((ChildSoundElement)e).getParent();
                    path.add(e.getAsString());
                //}
            }
            path.add(e.getAsString());

            Collections.reverse(path);

            if(path.size() > 0) {
                finalPath += path.get(0) + ":";
                for(int i = 0; i < path.size() - 1; i++) {
                    int j = i + 1;
                    finalPath += path.get(j) + ".";
                }
                finalPath += path.get(path.size() - 1);
            }

            return finalPath;
        }

        @Override
        public SoundHolderElement getParent() {
            return parent;
        }
    }

    public class SoundSound extends SoundElement implements ChildSoundElement {
        private String name;
        private final SoundHolderElement parent;

        public SoundSound(SoundHolderElement parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public String getAsString() {
            return name;
        }

        @Override
        public String getTotalPath() {
            String finalPath = "";
            List<String> path = new ArrayList<>();
            path.add(name);
            SoundHolderElement e = getParent();
            while(!(e instanceof SoundDomain)) {
                path.add(e.getAsString());
                e = ((ChildSoundElement)e).getParent();
            }
            path.add(e.getAsString());

            Collections.reverse(path);

            if(path.size() > 0) {
                finalPath += path.get(0) + ":";
                for(int i = 0; i < path.size() - 1; i++) {
                    int j = i + 1;
                    finalPath += path.get(j) + ".";
                }
                finalPath += path.get(path.size() - 1);
            }

            return finalPath;
        }

        @Override
        public SoundHolderElement getParent() {
            return parent;
        }
    }
}

