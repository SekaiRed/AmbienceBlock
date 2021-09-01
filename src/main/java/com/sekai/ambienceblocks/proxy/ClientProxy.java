package com.sekai.ambienceblocks.proxy;

import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.CompendiumGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    public void registerItemRenderer(Item item, int meta, String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    public void openAmbienceGUI(IAmbienceSource source) {
        Minecraft.getMinecraft().displayGuiScreen(new AmbienceGUI(source));
    }

    public void openCompendiumGUI() {
        Minecraft.getMinecraft().displayGuiScreen(new CompendiumGUI());
    }
}
