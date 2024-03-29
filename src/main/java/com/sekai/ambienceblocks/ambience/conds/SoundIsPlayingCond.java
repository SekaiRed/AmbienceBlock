package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetSound;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SoundIsPlayingCond extends AbstractCond {
    private AmbienceEquality equal;
    private String sound;

    private static final String EQUAL = "equal";
    private static final String SOUND = "sound";

    public SoundIsPlayingCond(AmbienceEquality equal, String sound) {
        this.equal = equal;
        this.sound = sound;
    }

    @Override
    public AbstractCond clone() {
        SoundIsPlayingCond cond = new SoundIsPlayingCond(equal, sound);
        return cond;
    }

    @Override
    public String getName() {
        return "sound.isplaying";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + sound;
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        //return equal.testFor(AmbienceController.instance.isSoundPlaying(sound));
        return equal.testFor(AmbienceController.instance.reflection.isPlaying(sound));
        //return true;
    }

    //gui

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetSound(SOUND, "Sound :", 160, sound));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if(SOUND.equals(widget.getKey()) && widget instanceof AmbienceWidgetSound)
                sound = ((AmbienceWidgetSound) widget).getValue();
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(SOUND, sound);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        //equal = AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        sound = nbt.getString(SOUND);
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(equal.ordinal());
        buf.writeUtf(sound, StaticUtil.LENGTH_SOUND);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        //this.equal = AmbienceEquality.values()[buf.readInt()];
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.sound = buf.readUtf(StaticUtil.LENGTH_SOUND);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
        json.addProperty(SOUND, sound);
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        sound = json.get(SOUND).getAsString();
    }
}
