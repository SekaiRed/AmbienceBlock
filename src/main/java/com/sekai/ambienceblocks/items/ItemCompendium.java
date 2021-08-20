package com.sekai.ambienceblocks.items;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.CompendiumGUI;
import com.sekai.ambienceblocks.packets.PacketAskCompendiumGui;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class ItemCompendium extends Item {
    public ItemCompendium() {
        super(new Properties().group(Main.TAB));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote())
            return super.onItemRightClick(worldIn, playerIn, handIn);

        //TODO open compendium GUI here
        //Minecraft.getInstance().displayGuiScreen(new CompendiumGUI());
        PacketHandler.NET.sendToServer(new PacketAskCompendiumGui());

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
