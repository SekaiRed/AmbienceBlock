package com.sekai.ambienceblocks.items;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.packets.PacketAskCompendiumGui;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemCompendium extends Item {
    public ItemCompendium() {
        super(new Properties().tab(Main.TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(!worldIn.isClientSide())
            return super.use(worldIn, playerIn, handIn);

        //TODO open compendium GUI here
        //Minecraft.getInstance().displayGuiScreen(new CompendiumGUI());
        PacketHandler.NET.sendToServer(new PacketAskCompendiumGui());

        return super.use(worldIn, playerIn, handIn);
    }
}
