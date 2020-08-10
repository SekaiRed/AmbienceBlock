package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.AbstractBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.SphereBounds;
import com.sekai.ambienceblocks.util.BoundsUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

//data holder class
public class AmbienceTileEntityData
{
    //main
    private String soundName = "";
    private boolean shouldFuse = true;
    private boolean needRedstone = false;

    //sounds
    private float volume = 1.0f;
    private float pitch = 1.0f;

    //priority
    private int priority = 0;
    private boolean usePriority = false;

    //bounds
    private AbstractBounds bounds = new SphereBounds(16D);

    //offset
    private BlockPos offset = new BlockPos(0, 0, 0);

    //delay
    private int minDelay = 0;
    private int maxDelay = 0;
    private boolean useDelay = false;

    //other
    private boolean isGlobal = false;

    //NBT util
    public CompoundNBT toNBT(CompoundNBT compound) {
        compound.putString("musicName", this.soundName);
        compound.putBoolean("shouldFuse", this.shouldFuse);
        compound.putBoolean("needRedstone", this.needRedstone);

        compound.putFloat("volume", this.volume);
        compound.putFloat("pitch", this.pitch);

        compound.putInt("priority",this.priority);
        compound.putBoolean("usePriority",this.usePriority);

        compound.put("bounds", BoundsUtil.toNBT(this.bounds));

        compound.put("offset", NBTUtil.writeBlockPos(offset));

        compound.putInt("minDelay",this.minDelay);
        compound.putInt("maxDelay",this.maxDelay);
        compound.putBoolean("useDelay", this.useDelay);

        compound.putBoolean("isGlobal", this.isGlobal);
        return compound;
    }

    public void fromNBT(CompoundNBT compound) {
        this.soundName = compound.getString("musicName");
        this.shouldFuse = compound.getBoolean("shouldFuse");
        this.needRedstone = compound.getBoolean("needRedstone");

        this.volume = compound.getFloat("volume");
        this.pitch = compound.getFloat("pitch");

        this.priority = compound.getInt("priority");
        this.usePriority = compound.getBoolean("usePriority");

        this.bounds = BoundsUtil.fromNBT(compound.getCompound("bounds"));

        this.offset = NBTUtil.readBlockPos(compound.getCompound("offset"));

        this.minDelay = compound.getInt("minDelay");
        this.maxDelay = compound.getInt("maxDelay");
        this.useDelay = compound.getBoolean("useDelay");

        this.isGlobal = compound.getBoolean("isGlobal");
    }
    ////

    //Buffer util
    public void toBuff(PacketBuffer buf) {
        //Encode the data for the buffer
        buf.writeString(this.soundName, 50);
        buf.writeBoolean(this.shouldFuse);
        buf.writeBoolean(this.needRedstone);

        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);

        buf.writeInt(this.priority);
        buf.writeBoolean(this.usePriority);

        //buf.writeDouble(this.offDistance);
        BoundsUtil.toBuff(this.bounds, buf);

        buf.writeBlockPos(this.offset);

        buf.writeInt(this.minDelay);
        buf.writeInt(this.maxDelay);
        buf.writeBoolean(this.useDelay);

        buf.writeBoolean(this.isGlobal);
    }

    public void fromBuff(PacketBuffer buf) {
        //Decode the data from the buffer
        this.soundName = buf.readString(50);
        this.shouldFuse = buf.readBoolean();
        this.needRedstone = buf.readBoolean();

        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();

        this.priority = buf.readInt();
        this.usePriority = buf.readBoolean();

        //this.offDistance = buf.readDouble();
        this.bounds = BoundsUtil.fromBuff(buf);

        this.offset = buf.readBlockPos();

        this.minDelay = buf.readInt();
        this.maxDelay = buf.readInt();
        this.useDelay = buf.readBoolean();

        this.isGlobal = buf.readBoolean();
    }
    ////

    //Bounds
    public boolean isWithinBounds(PlayerEntity player, BlockPos origin) {
        return bounds.isWithinBounds(player, origin.add(getOffset()));
    }

    public double distanceFromCenter(PlayerEntity player, BlockPos origin) {
        return bounds.distanceFromCenter(player, origin.add(getOffset()));
    }

    public double getPercentageHowCloseIsPlayer(PlayerEntity player, BlockPos origin) {
        return bounds.getPercentageHowCloseIsPlayer(player, origin.add(getOffset()));
    }

    //Getter and setter
    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public boolean shouldFuse() {
        return shouldFuse;
    }

    public void setShouldFuse(boolean shouldFuse) {
        this.shouldFuse = shouldFuse;
    }

    public boolean needsRedstone() {
        return needRedstone;
    }

    public void setNeedRedstone(boolean needRedstone) {
        this.needRedstone = needRedstone;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getPriority() { return priority; }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isUsingPriority() {
        return usePriority;
    }

    public void setUsePriority(boolean usePriority) {
        this.usePriority = usePriority;
    }

    public AbstractBounds getBounds() {
        return bounds;
    }

    public void setBounds(AbstractBounds bounds) { this.bounds = bounds; }

    public BlockPos getOffset() {
        return offset;
    }

    public void setOffset(BlockPos offset) {
        this.offset = offset;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    public boolean isUsingDelay() {
        return useDelay;
    }

    public void setUseDelay(boolean useDelay) {
        this.useDelay = useDelay;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}
