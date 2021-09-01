package com.sekai.ambienceblocks.items;

import com.sekai.ambienceblocks.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCompendium extends ItemBase {
    public ItemCompendium(String name) {
        super(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        //Minecraft.getMinecraft().displayGuiScreen(new CompendiumGUI());
        //PacketHandler.NETWORK.
        Main.proxy.openCompendiumGUI();

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
