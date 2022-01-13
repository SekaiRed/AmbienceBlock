package com.sekai.ambienceblocks.ambience;

import com.sekai.ambienceblocks.ambience.bounds.AbstractBounds;
import com.sekai.ambienceblocks.ambience.bounds.SphereBounds;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceType;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.CondsUtil;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//data holder class
public class AmbienceData
{
    public static final int maxPriorities = 99;
    public static final int maxChannels = 9;

    //main
    private String soundName = "";
    private String category = SoundCategory.MASTER.getName();
    private String type = AmbienceType.AMBIENT.getName();
    private boolean shouldFuse = false;
    private String introName = "";
    private String outroName = "";
    private String tag = "";

    //sounds
    private float volume = 1.0f;
    private float pitch = 1.0f;
    private int fadeIn = 0;
    private int fadeOut = 0;

    //priority
    private boolean usePriority = false;
    private int priority = 0;
    private int channel = 0;
    private boolean allowSamePriority = true;

    //bounds
    private AbstractBounds bounds = new SphereBounds(16D);
    private boolean isGlobal = false;
    private boolean isLocatable = true;

    //offset
    private AmbienceWorldSpace space = AmbienceWorldSpace.RELATIVE;
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
    public NBTTagCompound toNBT(NBTTagCompound compound) {
        compound.setString("musicName", this.soundName);
        compound.setString("category", this.category);
        compound.setString("type", this.type);
        compound.setString("tag", this.tag);

        if(AmbienceType.MUSIC.getName().equals(type)) {
            compound.setBoolean("shouldFuse", this.shouldFuse);
            compound.setString("introName", this.introName);
            compound.setString("outroName", this.outroName);
        }

        compound.setFloat("volume", this.volume);
        compound.setFloat("pitch", this.pitch);
        compound.setInteger("fadeIn", this.fadeIn);
        compound.setInteger("fadeOut", this.fadeOut);

        compound.setBoolean("usePriority",this.usePriority);
        if(usePriority) {
            compound.setInteger("priority", this.priority);
            compound.setInteger("channel", this.channel);
            compound.setBoolean("allowSamePriority", this.allowSamePriority);
        }

        compound.setInteger("space", space.ordinal());
        compound.setTag("bounds", BoundsUtil.toNBT(this.bounds));
        compound.setTag("offset", NBTHelper.writeVec3d(this.offset));
        compound.setBoolean("isGlobal", this.isGlobal);
        compound.setBoolean("isLocatable", this.isLocatable);

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            compound.setBoolean("useDelay", this.useDelay);
            if (useDelay) {
                compound.setInteger("minDelay", this.minDelay);
                compound.setInteger("maxDelay", this.maxDelay);
                compound.setBoolean("canPlayOverSelf", this.canPlayOverSelf);
                compound.setBoolean("shouldStopPrevious", this.shouldStopPrevious);

                compound.setFloat("minRandVolume", this.minRandVolume);
                compound.setFloat("maxRandVolume", this.maxRandVolume);

                compound.setFloat("minRandPitch", this.minRandPitch);
                compound.setFloat("maxRandPitch", this.maxRandPitch);
            }
        }

        compound.setBoolean("useCondition", this.useCondition);
        if(useCondition) {
            /*compound.setInteger("condSize", this.conditions.size());
            for(int i = 0; i < conditions.size(); i++) {
                compound.setTag("cond" + i, CondsUtil.toNBT(conditions.get(i)));
            }*/

            NBTTagList conds = new NBTTagList();
            conditions.forEach(cond -> {
                conds.appendTag(CondsUtil.toNBT(cond));
            });
            compound.setTag("conds", conds);
        }

        return compound;
    }

    public void fromNBT(NBTTagCompound compound) {
        this.soundName = compound.getString("musicName");
        this.category = compound.getString("category");
        this.type = compound.getString("type");
        this.tag = compound.getString("tag");

        if(AmbienceType.MUSIC.getName().equals(type)) {
            this.shouldFuse = compound.getBoolean("shouldFuse");
            this.introName = compound.getString("introName");
            this.outroName = compound.getString("outroName");
        }

        this.volume = compound.getFloat("volume");
        this.pitch = compound.getFloat("pitch");
        this.fadeIn = compound.getInteger("fadeIn");
        this.fadeOut = compound.getInteger("fadeOut");

        this.usePriority = compound.getBoolean("usePriority");
        if(usePriority) {
            this.priority = compound.getInteger("priority");
            this.channel = compound.getInteger("channel");
            //this.allowSamePriority = compound.getBoolean("allowSamePriority");
            this.allowSamePriority = compound.hasKey("allowSamePriority") ? compound.getBoolean("allowSamePriority") : true;
        }

        this.space = AmbienceWorldSpace.values()[compound.getInteger("space") < AmbienceEquality.values().length ? compound.getInteger("space") : 0];
        this.bounds = BoundsUtil.fromNBT((NBTTagCompound) compound.getTag("bounds"));
        this.offset = NBTHelper.readVec3d((NBTTagCompound) compound.getTag("offset"));
        this.isGlobal = compound.getBoolean("isGlobal");
        //TODO why would you do that
        // If I had more time I could wrap all nbt queries with a method and a default
        // parameter to avoid null values, I mean you can mess things up with NBTedit
        if(compound.hasKey("isLocatable"))
            this.isLocatable = compound.getBoolean("isLocatable");
        else
            this.isLocatable = true;

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            this.useDelay = compound.getBoolean("useDelay");
            if (useDelay) {
                this.minDelay = compound.getInteger("minDelay");
                this.maxDelay = compound.getInteger("maxDelay");
                this.canPlayOverSelf = compound.getBoolean("canPlayOverSelf");
                this.shouldStopPrevious = compound.getBoolean("shouldStopPrevious");

                this.minRandVolume = compound.getFloat("minRandVolume");
                this.maxRandVolume = compound.getFloat("maxRandVolume");

                this.minRandPitch = compound.getFloat("minRandPitch");
                this.maxRandPitch = compound.getFloat("maxRandPitch");
            }
        }

        this.useCondition = compound.getBoolean("useCondition");
        if(useCondition) {
            /*conditions.clear();
            int size = compound.getInteger("condSize");
            for(int i = 0; i < size; i++) {
                conditions.add(CondsUtil.fromNBT((NBTTagCompound) Objects.requireNonNull(compound.getTag("cond" + i))));
            }*/

            conditions.clear();

            //if it's encoded in the old format, read it as such
            if (isOldConditionFormat(compound)) {
                int size = compound.getInteger("condSize");
                for(int i = 0; i < size; i++) {
                    //conditions.add(CondsUtil.fromNBT((CompoundNBT) Objects.requireNonNull(compound.get("cond" + i))));
                    conditionAdd(CondsUtil.fromNBT((NBTTagCompound) Objects.requireNonNull(compound.getTag("cond" + i))));
                }
            } else {
                NBTTagList conds = compound.getTagList("conds", 10); //wtf is a type and why did 10 work out, i'm not complaining
                conds.forEach(inbt -> {
                    //conditions.add(CondsUtil.fromNBT((CompoundNBT) inbt));
                    conditionAdd(CondsUtil.fromNBT((NBTTagCompound) inbt));
                });
            }
        }
    }
    ////

    private void conditionAdd(AbstractCond cond) {
        if(conditions.size() < AmbienceConfig.maxAmountOfConditions) {
            conditions.add(cond);
        }
    }

    private boolean isOldConditionFormat(NBTTagCompound nbt) {
        //if there is atleast a cond0 key it means it is the old format
        return nbt.hasKey("cond0");
    }

    //Buffer util
    public void writeBuff(PacketBuffer buf) {
        /*ByteBufUtils.writeUTF8String(buf, this.soundName);
        ByteBufUtils.writeUTF8String(buf, this.category);
        ByteBufUtils.writeUTF8String(buf, this.type);*/
        buf.writeString(this.soundName);
        buf.writeString(this.category);
        buf.writeString(this.type);
        buf.writeString(this.tag);

        if(AmbienceType.MUSIC.getName().equals(type)) {
            buf.writeBoolean(this.shouldFuse);
            /*ByteBufUtils.writeUTF8String(buf, this.introName);
            ByteBufUtils.writeUTF8String(buf, this.outroName);*/
            buf.writeString(this.introName);
            buf.writeString(this.outroName);
        }

        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeInt(this.fadeIn);
        buf.writeInt(this.fadeOut);

        buf.writeBoolean(this.usePriority);
        if(usePriority) {
            buf.writeInt(this.priority);
            buf.writeInt(this.channel);
            buf.writeBoolean(this.allowSamePriority);
        }

        //buf.writeDouble(this.offDistance);
        buf.writeInt(space.ordinal());
        BoundsUtil.toBuff(this.bounds, buf);
        buf.writeDouble(this.offset.x);
        buf.writeDouble(this.offset.y);
        buf.writeDouble(this.offset.z);
        buf.writeBoolean(this.isGlobal);
        buf.writeBoolean(this.isLocatable);

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            buf.writeBoolean(this.useDelay);
            if (useDelay) {
                buf.writeInt(this.minDelay);
                buf.writeInt(this.maxDelay);
                buf.writeBoolean(this.canPlayOverSelf);
                buf.writeBoolean(this.shouldStopPrevious);

                buf.writeFloat(this.minRandVolume);
                buf.writeFloat(this.maxRandVolume);

                buf.writeFloat(this.minRandPitch);
                buf.writeFloat(this.maxRandPitch);
            }
        }

        buf.writeBoolean(this.useCondition);
        if(useCondition) {
            buf.writeInt(this.conditions.size());
            for (AbstractCond condition : conditions) {
                CondsUtil.toBuff(condition, buf);
            }
        }
    }

    public void readBuff(PacketBuffer buf) {
        //Decode the data from the buffer
        /*this.soundName = ByteBufUtils.readUTF8String(buf);
        this.category = ByteBufUtils.readUTF8String(buf);
        this.type = ByteBufUtils.readUTF8String(buf);*/
        this.soundName = buf.readString(50);
        this.category = buf.readString(20);
        this.type = buf.readString(20);
        this.tag = buf.readString(5);
        //this.alwaysPlay = buf.readBoolean();

        if(AmbienceType.MUSIC.getName().equals(type)) {
            this.shouldFuse = buf.readBoolean();
            /*this.introName = ByteBufUtils.readUTF8String(buf);
            this.outroName = ByteBufUtils.readUTF8String(buf);*/
            this.introName = buf.readString(50);
            this.outroName = buf.readString(50);
        }

        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.fadeIn = buf.readInt();
        this.fadeOut = buf.readInt();

        this.usePriority = buf.readBoolean();
        if(usePriority) {
            this.priority = buf.readInt();
            this.channel = buf.readInt();
            this.allowSamePriority = buf.readBoolean();
        }

        //yeowch
        int spaceID = buf.readInt();
        this.space =  AmbienceWorldSpace.values()[spaceID<AmbienceWorldSpace.values().length?spaceID:0];
        this.bounds = BoundsUtil.fromBuff(buf);
        this.offset = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.isGlobal = buf.readBoolean();
        this.isLocatable = buf.readBoolean();

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            this.useDelay = buf.readBoolean();
            if (useDelay) {
                this.minDelay = buf.readInt();
                this.maxDelay = buf.readInt();
                this.canPlayOverSelf = buf.readBoolean();
                this.shouldStopPrevious = buf.readBoolean();

                this.minRandVolume = buf.readFloat();
                this.maxRandVolume = buf.readFloat();

                this.minRandPitch = buf.readFloat();
                this.maxRandPitch = buf.readFloat();
            }
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

    //Bounds
    public boolean isWithinBounds(EntityPlayer player, Vector3d origin) {
        return bounds.isWithinBounds(player, origin);
    }

    public double distanceFromCenter(EntityPlayer player, Vector3d origin) {
        return bounds.distanceFromCenter(player, origin);
    }

    public double getPercentageHowCloseIsPlayer(EntityPlayer player, Vector3d origin) {
        return bounds.getPercentageHowCloseIsPlayer(player, origin);
    }

    //Getter and setter
    public String getSoundName() { return soundName; }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public String getIntroName() {
        return introName;
    }

    public void setIntroName(String introName) {
        this.introName = introName;
    }

    public String getOutroName() {
        return outroName;
    }

    public void setOutroName(String introName) {
        this.outroName = introName;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

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

    public boolean canPlayAtSamePriority() {
        return allowSamePriority;
    }

    public void setCanPlayAtSamePriority(boolean allowSamePriority) {
        this.allowSamePriority = allowSamePriority;
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

    /*public boolean shouldAlwaysPlay() {
        return alwaysPlay;
    }

    public void setAlwaysPlay(boolean alwaysPlay) {
        this.alwaysPlay = alwaysPlay;
    }*/

    public AbstractBounds getBounds() {
        return bounds;
    }

    public void setBounds(AbstractBounds bounds) { this.bounds = bounds; }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isLocatable() {
        return isLocatable;
    }

    public void setLocatable(boolean locatable) {
        isLocatable = locatable;
    }

    public AmbienceWorldSpace getSpace() {
        return space;
    }

    public void setSpace(AmbienceWorldSpace space) {
        this.space = space;
    }

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
        if(min >= max) return max;
        return (int) ((max - min) * Math.random() + min);
    }

    //only used by the ambience block finder
    public float[] getColor() {
        float[] c = new float[4];
        int hue = 0;
        for(int i = 0; i < getSoundName().length(); i++) {
            if(getSoundName().charAt(i) != ':' && getSoundName().charAt(i) != '.')
                hue += getSoundName().charAt(i) - 97;
        }
        hue *= 2;

        hue%=255;

        Color color = Color.getHSBColor(hue/255f, 1f, 1f);

        c[0] = color.getRed()/255f;
        c[1] = color.getGreen()/255f;
        c[2] = color.getBlue()/255f;
        c[3] = 1f;

        return c;
    }

    public AmbienceData copy() {
        AmbienceData data = new AmbienceData();

        //I'm pretty sure this is illegal in multiple countries but this is the simplest and maintainable way I found to do this.
        //I have no idea why everyone hates clone() lol
        NBTTagCompound nbt = new NBTTagCompound();
        toNBT(nbt);
        data.fromNBT(nbt);

        return data;
    }
}
