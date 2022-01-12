package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class AmbienceTileEntity extends BlockEntity implements IAmbienceSource {
    public static final int MAGIC_PACKET_NUM = 420;
    public AmbienceData data = new AmbienceData();

    /*public AmbienceTileEntity(final BlockEntity<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }*/

    public AmbienceTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public AmbienceTileEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(RegistryHandler.AMBIENCE_TILE_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        data.toNBT(compound);
        compound = super.save(compound);
        return compound;
        //return super.save(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        data.toNBT(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        data.fromNBT(compound);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        data.toNBT(tag);
        super.save(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        data.fromNBT(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        //return new ClientboundBlockEntityDataPacket(this.worldPosition, MAGIC_PACKET_NUM, this.getUpdateTag());
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    //I have no idea if this is a good idea
    @Override
    public CompoundTag getTileData() {
        CompoundTag tag = new CompoundTag();
        data.toNBT(tag);
        super.save(tag);
        return tag;
    }

    //interface
    @Override
    public AmbienceData getData() {
        return data;
    }

    @Override
    public Vec3 getOrigin() {
        Vec3 oPos = new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vec3(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vec3(0.5, 0.5, 0.5));
    }

    //Very hacky solution, I'm amazed it works but I'm not sure if it will in the future
    @Override
    public void onLoad() {
        if(level != null && level.isClientSide()) {
            AmbienceController.instance.addAmbienceTileEntity(this);
        }
        super.onLoad();
    }
}
