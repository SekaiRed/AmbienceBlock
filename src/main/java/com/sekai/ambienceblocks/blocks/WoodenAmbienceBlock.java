package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.Main;
import com.sekai.ambienceblocks.init.ModBlocks;
import com.sekai.ambienceblocks.init.ModItems;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class WoodenAmbienceBlock extends Block implements IHasModel, ITileEntityProvider {
    public WoodenAmbienceBlock(String name, Material material)
    {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(Main.MYTAB);

        this.hasTileEntity = true;

        setHardness(2.0F);
        setResistance(3.0F);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote)
            return true;

        if(Minecraft.getMinecraft().world.getTileEntity(pos) == null)
            return true;

        if(!(Minecraft.getMinecraft().world.getTileEntity(pos) instanceof AmbienceTileEntity))
            return true;

        //Minecraft.getMinecraft().displayGuiScreen(new AmbienceGUI((AmbienceTileEntity) Minecraft.getMinecraft().world.getTileEntity(pos)));
        Main.proxy.openAmbienceGUI((AmbienceTileEntity) Minecraft.getMinecraft().world.getTileEntity(pos));

        return true;
        //return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new AmbienceTileEntity();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new AmbienceTileEntity();
    }

    @Override
    public void registerModels()
    {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
