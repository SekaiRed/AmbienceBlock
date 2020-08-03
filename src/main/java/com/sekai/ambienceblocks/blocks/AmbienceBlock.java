package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.client.gui.AmbienceTileGUI;
import com.sekai.ambienceblocks.packets.PacketOpenAmbienceGui;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;

public class AmbienceBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AmbienceBlock() {
        super(Block.Properties.create(Material.IRON)
        .hardnessAndResistance(5.0f, 6.0f)
        .sound(SoundType.METAL)
        .harvestLevel(0)
        .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return RegistryHandler.AMBIENCE_TILE_ENTITY.get().create();
    }

    /*@Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.isBlockPowered(pos);
        if (flag != state.get(POWERED)) {
            if (flag) {
                this.triggerNote(worldIn, pos);
            }

            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
        }

    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote())
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        if(Minecraft.getInstance().world.getTileEntity(pos) == null)
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        if(!(Minecraft.getInstance().world.getTileEntity(pos) instanceof AmbienceTileEntity))
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        Minecraft.getInstance().displayGuiScreen(new AmbienceTileGUI((AmbienceTileEntity)Minecraft.getInstance().world.getTileEntity(pos)));

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        /*if(worldIn.isRemote())
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity == null)
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        if(!AmbienceTileEntity.class.isInstance(tileEntity))
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        //((GlobalAmbienceTileEntity) tileEntity).setServerMusicName("towns.smalltownnight", (ServerPlayerEntity)player);
        PacketHandler.NET.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOpenAmbienceGui(pos));

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);*/
    }
}
