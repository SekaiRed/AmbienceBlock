package com.sekai.ambienceblocks.tileentity;

import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.AbstractBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.SphereBounds;
import com.sekai.ambienceblocks.tileentity.ambiencetilecond.AbstractCond;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.CondsUtil;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//data holder class
public class AmbienceTileEntityData
{
    public static final int maxPriorities = 99;
    public static final int maxChannels = 9;

    //main
    private String soundName = "";
    private boolean shouldFuse = false;
    private String category = SoundCategory.MASTER.getName();

    //sounds
    private float volume = 1.0f;
    private float pitch = 1.0f;
    private int fadeIn = 0;
    private int fadeOut = 0;

    //priority
    private boolean usePriority = false;
    private int priority = 0;
    private int channel = 0;

    //bounds
    private AbstractBounds bounds = new SphereBounds(16D);
    private boolean isGlobal = false;

    //offset
    private Vector3d offset = new Vector3d(0, 0, 0);

    //delay
    private boolean useDelay = false;
    private int minDelay = 0;
    private int maxDelay = 0;
    private float minRandVolume = 0;
    private float maxRandVolume = 0;
    private float minRandPitch = 0;
    private float maxRandPitch = 0;
    private boolean canPlayOverSelf = false;
    private boolean shouldStopPrevious = false;

    //condition
    private boolean useCondition = false;
    private List<AbstractCond> conditions = new ArrayList<>();

    //NBT util
    public CompoundNBT toNBT(CompoundNBT compound) {
        compound.putString("musicName", this.soundName);
        compound.putBoolean("shouldFuse", this.shouldFuse);
        compound.putString("category", this.category);

        compound.putFloat("volume", this.volume);
        compound.putFloat("pitch", this.pitch);
        compound.putInt("fadeIn", this.fadeIn);
        compound.putInt("fadeOut", this.fadeOut);

        compound.putBoolean("usePriority",this.usePriority);
        if(usePriority) {
            compound.putInt("priority", this.priority);
            compound.putInt("channel", this.channel);
        }

        compound.put("bounds", BoundsUtil.toNBT(this.bounds));
        compound.put("offset", NBTHelper.writeVec3d(this.offset));
        compound.putBoolean("isGlobal", this.isGlobal);

        compound.putBoolean("useDelay", this.useDelay);
        if(useDelay) {
            compound.putInt("minDelay", this.minDelay);
            compound.putInt("maxDelay", this.maxDelay);
            compound.putBoolean("canPlayOverSelf", this.canPlayOverSelf);
            compound.putBoolean("shouldStopPrevious", this.shouldStopPrevious);

            compound.putFloat("minRandVolume", this.minRandVolume);
            compound.putFloat("maxRandVolume", this.maxRandVolume);

            compound.putFloat("minRandPitch", this.minRandPitch);
            compound.putFloat("maxRandPitch", this.maxRandPitch);
        }

        compound.putBoolean("useCondition", this.useCondition);
        if(useCondition) {
            compound.putInt("condSize", this.conditions.size());
            for(int i = 0; i < conditions.size(); i++) {
                compound.put("cond" + i, CondsUtil.toNBT(conditions.get(i)));
            }
        }

        return compound;
    }

    public void fromNBT(CompoundNBT compound) {
        this.soundName = compound.getString("musicName");
        this.shouldFuse = compound.getBoolean("shouldFuse");
        this.category = compound.getString("category");

        this.volume = compound.getFloat("volume");
        this.pitch = compound.getFloat("pitch");
        this.fadeIn = compound.getInt("fadeIn");
        this.fadeOut = compound.getInt("fadeOut");

        this.usePriority = compound.getBoolean("usePriority");
        if(usePriority) {
            this.priority = compound.getInt("priority");
            this.channel = compound.getInt("channel");
        }


        this.bounds = BoundsUtil.fromNBT(compound.getCompound("bounds"));
        this.offset = NBTHelper.readVec3d(compound.getCompound("offset"));
        this.isGlobal = compound.getBoolean("isGlobal");

        this.useDelay = compound.getBoolean("useDelay");
        if(useDelay) {
            this.minDelay = compound.getInt("minDelay");
            this.maxDelay = compound.getInt("maxDelay");
            this.canPlayOverSelf = compound.getBoolean("canPlayOverSelf");
            this.shouldStopPrevious = compound.getBoolean("shouldStopPrevious");

            this.minRandVolume = compound.getFloat("minRandVolume");
            this.maxRandVolume = compound.getFloat("maxRandVolume");

            this.minRandPitch = compound.getFloat("minRandPitch");
            this.maxRandPitch = compound.getFloat("maxRandPitch");
        }

        this.useCondition = compound.getBoolean("useCondition");
        if(useCondition) {
            conditions.clear();
            int size = compound.getInt("condSize");
            for(int i = 0; i < size; i++) {
                conditions.add(CondsUtil.fromNBT((CompoundNBT) Objects.requireNonNull(compound.get("cond" + i))));
            }
        }

    }
    ////

    //Buffer util
    public void toBuff(PacketBuffer buf) {
        //Encode the data for the buffer
        buf.writeString(this.soundName, 50);
        buf.writeBoolean(this.shouldFuse);
        buf.writeString(this.category, 20);

        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeInt(this.fadeIn);
        buf.writeInt(this.fadeOut);

        buf.writeBoolean(this.usePriority);
        if(usePriority) {
            buf.writeInt(this.priority);
            buf.writeInt(this.channel);
        }

        //buf.writeDouble(this.offDistance);
        BoundsUtil.toBuff(this.bounds, buf);
        buf.writeDouble(this.offset.x);
        buf.writeDouble(this.offset.y);
        buf.writeDouble(this.offset.z);
        buf.writeBoolean(this.isGlobal);

        buf.writeBoolean(this.useDelay);
        if(useDelay) {
            buf.writeInt(this.minDelay);
            buf.writeInt(this.maxDelay);
            buf.writeBoolean(this.canPlayOverSelf);
            buf.writeBoolean(this.shouldStopPrevious);

            buf.writeFloat(this.minRandVolume);
            buf.writeFloat(this.maxRandVolume);

            buf.writeFloat(this.minRandPitch);
            buf.writeFloat(this.maxRandPitch);
        }

        buf.writeBoolean(this.useCondition);
        if(useCondition) {
            buf.writeInt(this.conditions.size());
            for (AbstractCond condition : conditions) {
                CondsUtil.toBuff(condition, buf);
            }
        }
    }

    public void fromBuff(PacketBuffer buf) {
        //Decode the data from the buffer
        this.soundName = buf.readString(50);
        this.shouldFuse = buf.readBoolean();
        this.category = buf.readString(20);

        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.fadeIn = buf.readInt();
        this.fadeOut = buf.readInt();

        this.usePriority = buf.readBoolean();
        if(usePriority) {
            this.priority = buf.readInt();
            this.channel = buf.readInt();
        }

        this.bounds = BoundsUtil.fromBuff(buf);
        this.offset = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.isGlobal = buf.readBoolean();

        this.useDelay = buf.readBoolean();
        if(useDelay) {
            this.minDelay = buf.readInt();
            this.maxDelay = buf.readInt();
            this.canPlayOverSelf = buf.readBoolean();
            this.shouldStopPrevious = buf.readBoolean();

            this.minRandVolume = buf.readFloat();
            this.maxRandVolume = buf.readFloat();

            this.minRandPitch = buf.readFloat();
            this.maxRandPitch = buf.readFloat();
        }

        this.useCondition = buf.readBoolean();
        if(useCondition) {
            conditions.clear();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                conditions.add(CondsUtil.fromBuff(buf));
            }
        }
    }
    ////

    //Bounds
    public boolean isWithinBounds(PlayerEntity player, BlockPos origin) {
        return bounds.isWithinBounds(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
    }

    public double distanceFromCenter(PlayerEntity player, BlockPos origin) {
        return bounds.distanceFromCenter(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
    }

    public double getPercentageHowCloseIsPlayer(PlayerEntity player, BlockPos origin) {
        return bounds.getPercentageHowCloseIsPlayer(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
    }

    //Getter and setter
    public String getSoundName() { return soundName; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public boolean shouldFuse() {
        return shouldFuse;
    }

    public void setShouldFuse(boolean shouldFuse) {
        this.shouldFuse = shouldFuse;
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

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public int getPriority() { return priority; }

    public void setPriority(int priority) {
        if(priority < 0)
            this.priority = 0;

        if(priority >= maxPriorities)
            this.priority = maxPriorities - 1;

        this.priority = priority;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        /*this.channel = channel;*/
        if(channel < 0)
            this.channel = 0;

        if(channel >= maxChannels)
            this.channel = maxChannels - 1;

        this.channel = channel;
    }

    public boolean isUsingPriority() {
        return usePriority;
    }

    public void setUsePriority(boolean usePriority) {
        this.usePriority = usePriority;
    }

    public boolean isUsingCondition() {
        return useCondition;
    }

    public void setUseCondition(boolean useCondition) {
        this.useCondition = useCondition;
    }

    public List<AbstractCond> getConditions() {
        return conditions;
    }

    public void setConditions(List<AbstractCond> conditions) {
        this.conditions = conditions;
    }

    public AbstractBounds getBounds() {
        return bounds;
    }

    public void setBounds(AbstractBounds bounds) { this.bounds = bounds; }

    public Vector3d getOffset() {
        return offset;
    }

    public void setOffset(Vector3d offset) {
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

    public float getMinRandVolume() {
        return minRandVolume;
    }

    public void setMinRandVolume(float minRandVolume) {
        this.minRandVolume = minRandVolume;
    }

    public float getMaxRandVolume() {
        return maxRandVolume;
    }

    public void setMaxRandVolume(float maxRandVolume) {
        this.maxRandVolume = maxRandVolume;
    }

    public float getMinRandPitch() {
        return minRandPitch;
    }

    public void setMinRandPitch(float minRandPitch) {
        this.minRandPitch = minRandPitch;
    }

    public float getMaxRandPitch() {
        return maxRandPitch;
    }

    public void setMaxRandPitch(float maxRandPitch) {
        this.maxRandPitch = maxRandPitch;
    }

    public boolean isUsingRandomVolume() {
        return minRandVolume != 0f || maxRandVolume != 0f;
    }

    public float getMinRandomVolume() {
        return getVolume() - minRandVolume;
    }

    public float getMaxRandomVolume() {
        return getVolume() + maxRandVolume;
    }

    public boolean isUsingRandomPitch() {
        return minRandPitch != 0f || maxRandPitch != 0f;
    }

    public float getMinRandomPitch() {
        return getPitch() - minRandPitch;
    }

    public float getMaxRandomPitch() {
        return getPitch() + maxRandPitch;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean canPlayOverSelf() {
        return canPlayOverSelf;
    }

    public void setCanPlayOverSelf(boolean canPlayOverSelf) {
        this.canPlayOverSelf = canPlayOverSelf;
    }

    public boolean shouldStopPrevious() {
        return shouldStopPrevious;
    }

    public void setShouldStopPrevious(boolean shouldStopPrevious) {
        this.shouldStopPrevious = shouldStopPrevious;
    }

    //utility
    public int getDelay() {
        int min = getMinDelay(), max = getMaxDelay();
        if(min > max || min == max) return max;
        return (int) ((max - min) * Math.random() + min);
    }

    public float[] getColor() {
        float[] c = new float[4];
        int hue = 0;
        for(int i = 0; i < getSoundName().length(); i++) {
            if(getSoundName().charAt(i) != ':')
                hue += getSoundName().charAt(i) - 97;
        }
        //hue = hue%360;
        float U = (float) Math.cos(hue*Math.PI/180);
        float W = (float) Math.sin(hue*Math.PI/180);

        float or = 1;
        float og = 0;
        float ob = 0;
        //r
        c[0] = (float) ((.299+.701*U+.168*W)*or
                        + (.587-.587*U+.330*W)*og
                        + (.114-.114*U-.497*W)*ob);
        c[1] = (float) ((.299-.299*U-.328*W)*or
                        + (.587+.413*U+.035*W)*og
                        + (.114-.114*U+.292*W)*ob);
        c[2] = (float) ((.299-.3*U+1.25*W)*or
                        + (.587-.588*U-1.05*W)*og
                        + (.114+.886*U-.203*W)*ob);
        c[3] = 1f;

        return c;
    }
}
