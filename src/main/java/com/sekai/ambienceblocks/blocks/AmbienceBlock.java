package com.sekai.ambienceblocks.blocks;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.util.RegistryHandler;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class AmbienceBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final ActionResultType RESULT = ActionResultType.SUCCESS;

    public AmbienceBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1.0F, 3600000.0F).sound(SoundType.METAL).noDrops());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return RegistryHandler.AMBIENCE_TILE_ENTITY.get().create();
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote())
            return RESULT;

        if(Minecraft.getInstance().world.getTileEntity(pos) == null)
            return RESULT;

        if(!(Minecraft.getInstance().world.getTileEntity(pos) instanceof AmbienceTileEntity))
            return RESULT;

        Minecraft.getInstance().displayGuiScreen(new AmbienceGUI((AmbienceTileEntity) Minecraft.getInstance().world.getTileEntity(pos)));

        return RESULT;
    }
}
