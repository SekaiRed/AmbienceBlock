package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class AmbienceBlock extends Block implements EntityBlock {
    //public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final InteractionResult RESULT = InteractionResult.SUCCESS;

    public AmbienceBlock() {
        super(Properties.of(Material.METAL).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noDrops());
    }

    /*@Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return RegistryHandler.AMBIENCE_TILE_ENTITY.get().create();
    }*/

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegistryHandler.AMBIENCE_TILE_ENTITY.get().create(pos, state);
    }

    //public InteractionResult use(BlockState p_52233_, Level p_52234_, BlockPos p_52235_, Player p_52236_, InteractionHand p_52237_, BlockHitResult p_52238_)
    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!worldIn.isClientSide())
            return RESULT;

        if(Minecraft.getInstance().level.getBlockEntity(pos) == null)
            return RESULT;

        if(!(Minecraft.getInstance().level.getBlockEntity(pos) instanceof AmbienceTileEntity))
            return RESULT;

        Minecraft.getInstance().setScreen(new AmbienceGUI((AmbienceTileEntity) Minecraft.getInstance().level.getBlockEntity(pos)));

        return RESULT;
    }
}
