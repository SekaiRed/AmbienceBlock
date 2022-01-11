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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//data holder class
public class AmbienceData {
    public static final int maxPriorities = 99;
    public static final int maxChannels = 9;

    //main
    private String soundName = "";
    //TODO using names is fine but with the buffer atleast use ordinals, it would decrease packet size and will always be compatible
    private String category = SoundSource.MASTER.getName();
    private String type = AmbienceType.AMBIENT.getName();
    private boolean shouldFuse = false;
    private String introName = "";
    private String outroName = "";
    private String tag = "";
    //private boolean alwaysPlay = false;

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
    private boolean isLocatable = true;

    //offset
    private AmbienceWorldSpace space = AmbienceWorldSpace.RELATIVE;
    private Vec3 offset = new Vec3(0, 0, 0);

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
    public CompoundTag toNBT(CompoundTag compound) {
        compound.putString("musicName", this.soundName);
        compound.putString("category", this.category);
        compound.putString("type", this.type);
        compound.putString("tag", this.tag);

        if(AmbienceType.MUSIC.getName().equals(type)) {
            compound.putBoolean("shouldFuse", this.shouldFuse);
            compound.putString("introName", this.introName);
            compound.putString("outroName", this.outroName);
        }

        compound.putFloat("volume", this.volume);
        compound.putFloat("pitch", this.pitch);
        compound.putInt("fadeIn", this.fadeIn);
        compound.putInt("fadeOut", this.fadeOut);

        compound.putBoolean("usePriority",this.usePriority);
        if(usePriority) {
            compound.putInt("priority", this.priority);
            compound.putInt("channel", this.channel);
        }

        compound.putInt("space", space.ordinal());
        compound.put("bounds", BoundsUtil.toNBT(this.bounds));
        compound.put("offset", NBTHelper.writeVec3d(this.offset));
        compound.putBoolean("isGlobal", this.isGlobal);
        compound.putBoolean("isLocatable", this.isLocatable);

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            compound.putBoolean("useDelay", this.useDelay);
            if (useDelay) {
                compound.putInt("minDelay", this.minDelay);
                compound.putInt("maxDelay", this.maxDelay);
                compound.putBoolean("canPlayOverSelf", this.canPlayOverSelf);
                compound.putBoolean("shouldStopPrevious", this.shouldStopPrevious);

                compound.putFloat("minRandVolume", this.minRandVolume);
                compound.putFloat("maxRandVolume", this.maxRandVolume);

                compound.putFloat("minRandPitch", this.minRandPitch);
                compound.putFloat("maxRandPitch", this.maxRandPitch);
            }
        }

        compound.putBoolean("useCondition", this.useCondition);
        if(useCondition) {
            /*compound.putInt("condSize", this.conditions.size());
            for(int i = 0; i < conditions.size(); i++) {
                compound.put("cond" + i, CondsUtil.toNBT(conditions.get(i)));
            }*/

            ListTag conds = new ListTag();
            conditions.forEach(cond -> {
                conds.add(CondsUtil.toNBT(cond));
            });
            compound.put("conds", conds);

            /*ListNBT conds = compound.getList("conds", 0); //wtf is a type
            conds.forEach(inbt -> {
                conditions.add(CondsUtil.fromNBT((CompoundTag) inbt));
            });*/
        }

        return compound;
    }

    public void fromNBT(CompoundTag compound) {
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
        this.fadeIn = compound.getInt("fadeIn");
        this.fadeOut = compound.getInt("fadeOut");

        this.usePriority = compound.getBoolean("usePriority");
        if(usePriority) {
            this.priority = compound.getInt("priority");
            this.channel = compound.getInt("channel");
        }

        this.space = AmbienceWorldSpace.values()[compound.getInt("space") < AmbienceEquality.values().length ? compound.getInt("space") : 0];
        this.bounds = BoundsUtil.fromNBT(compound.getCompound("bounds"));
        this.offset = NBTHelper.readVec3d(compound.getCompound("offset"));
        this.isGlobal = compound.getBoolean("isGlobal");
        //TODO why would you do that
        if(compound.contains("isLocatable"))
            this.isLocatable = compound.getBoolean("isLocatable");
        else
            this.isLocatable = true;

        if(AmbienceType.AMBIENT.getName().equals(type)) {
            this.useDelay = compound.getBoolean("useDelay");
            if (useDelay) {
                this.minDelay = compound.getInt("minDelay");
                this.maxDelay = compound.getInt("maxDelay");
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
            conditions.clear();

            //if it's encoded in the old format, read it as such
            if (isOldConditionFormat(compound)) {
                int size = compound.getInt("condSize");
                for(int i = 0; i < size; i++) {
                    //conditions.add(CondsUtil.fromNBT((CompoundTag) Objects.requireNonNull(compound.get("cond" + i))));
                    conditionAdd(CondsUtil.fromNBT((CompoundTag) Objects.requireNonNull(compound.get("cond" + i))));
                }
            } else {
                ListTag conds = compound.getList("conds", 10); //wtf is a type and why did 10 work out, i'm not complaining
                conds.forEach(inbt -> {
                    //conditions.add(CondsUtil.fromNBT((CompoundTag) inbt));
                    conditionAdd(CondsUtil.fromNBT((CompoundTag) inbt));
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

    private boolean isOldConditionFormat(CompoundTag nbt) {
        //if there is atleast a cond0 key it means it is the old format
        return nbt.contains("cond0");
    }

    //Buffer util
    public void toBuff(FriendlyByteBuf buf) {
        //Encode the data for the buffer
        buf.writeUtf(this.soundName, 50);
        buf.writeUtf(this.category, 20);
        buf.writeUtf(this.type, 10);
        buf.writeUtf(this.tag, 5);
        //buf.writeBoolean(this.alwaysPlay);

        if(AmbienceType.MUSIC.getName().equals(type)) {
            buf.writeBoolean(this.shouldFuse);
            buf.writeUtf(this.introName);
            buf.writeUtf(this.outroName);
        }

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

    public void fromBuff(FriendlyByteBuf buf) {
        //Decode the data from the buffer
        //TOD readUTF could be not right
        this.soundName = buf.readUtf(50);
        this.category = buf.readUtf(20);
        this.type = buf.readUtf(10);
        this.tag = buf.readUtf(5);
        //this.alwaysPlay = buf.readBoolean();

        if(AmbienceType.MUSIC.getName().equals(type)) {
            this.shouldFuse = buf.readBoolean();
            /*this.introName = buf.readString(50);
            this.outroName = buf.readString(50);*/
            this.introName = buf.readUtf(50);
            this.outroName = buf.readUtf(50);
        }

        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.fadeIn = buf.readInt();
        this.fadeOut = buf.readInt();

        this.usePriority = buf.readBoolean();
        if(usePriority) {
            this.priority = buf.readInt();
            this.channel = buf.readInt();
        }

        //yeowch
        int spaceID = buf.readInt();
        this.space =  AmbienceWorldSpace.values()[spaceID< AmbienceWorldSpace.values().length?spaceID:0];
        this.bounds = BoundsUtil.fromBuff(buf);
        this.offset = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
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
                //conditions.add(CondsUtil.fromBuff(buf));
                conditionAdd(CondsUtil.fromBuff(buf));
            }
        }
    }
    ////

    //Bounds
    public boolean isWithinBounds(Player player, Vec3 origin) {
        /*if(AmbiencePosition.RELATIVE.equals(getSpace()))
            return bounds.isWithinBounds(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
        else
            return bounds.isWithinBounds(player, getOffset());*/
        return bounds.isWithinBounds(player, origin);
    }

    public double distanceFromCenter(Player player, Vec3 origin) {
        /*if(AmbiencePosition.RELATIVE.equals(getSpace()))
            return bounds.distanceFromCenter(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
        else
            return bounds.distanceFromCenter(player, getOffset());*/
        return bounds.distanceFromCenter(player, origin);
    }

    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        /*if(AmbiencePosition.RELATIVE.equals(getSpace()))
            return bounds.getPercentageHowCloseIsPlayer(player, ParsingUtil.blockPosToVec3d(origin).add(getOffset()));
        else
            return bounds.getPercentageHowCloseIsPlayer(player, getOffset());*/
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

    public Vec3 getOffset() {
        return offset;
    }

    public void setOffset(Vec3 offset) {
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

    //TODO I could make this lazy..
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
        /*float[] c = new float[4];
        int hue = 0;
        for(int i = 0; i < getSoundName().length(); i++) {
            if(getSoundName().charAt(i) != ':')
                hue += getSoundName().charAt(i) - 97;
        }
        hue *= 2;

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

        return c;*/
    }

    public AmbienceData copy() {
        AmbienceData data = new AmbienceData();

        //I'm pretty sure this is illegal in multiple countries but this is the simplest and maintainable way I found to do this.
        //I have no idea why everyone hates clone() lol
        CompoundTag nbt = new CompoundTag();
        toNBT(nbt);
        data.fromNBT(nbt);

        return data;
    }

    @Override
    public String toString() {
        return "AmbienceData{" +
                "soundName='" + soundName + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", shouldFuse=" + shouldFuse +
                ", introName='" + introName + '\'' +
                ", outroName='" + outroName + '\'' +
                ", tag='" + tag + '\'' +
                ", volume=" + volume +
                ", pitch=" + pitch +
                ", fadeIn=" + fadeIn +
                ", fadeOut=" + fadeOut +
                ", usePriority=" + usePriority +
                ", priority=" + priority +
                ", channel=" + channel +
                ", bounds=" + bounds +
                ", isGlobal=" + isGlobal +
                ", isLocatable=" + isLocatable +
                ", space=" + space +
                ", offset=" + offset +
                ", useDelay=" + useDelay +
                ", minDelay=" + minDelay +
                ", maxDelay=" + maxDelay +
                ", minRandVolume=" + minRandVolume +
                ", maxRandVolume=" + maxRandVolume +
                ", minRandPitch=" + minRandPitch +
                ", maxRandPitch=" + maxRandPitch +
                ", canPlayOverSelf=" + canPlayOverSelf +
                ", shouldStopPrevious=" + shouldStopPrevious +
                ", useCondition=" + useCondition +
                ", conditions=" + conditions +
                '}';
    }
}
