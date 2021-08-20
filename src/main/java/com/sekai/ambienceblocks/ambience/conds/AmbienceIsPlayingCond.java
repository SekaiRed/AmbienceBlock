package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetSound;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AmbienceIsPlayingCond extends AbstractCond {
    private AmbienceEquality equal;
    private String sound;

    private static final String EQUAL = "equal";
    private static final String SOUND = "sound";

    public AmbienceIsPlayingCond(AmbienceEquality equal, String sound) {
        this.equal = equal;
        this.sound = sound;
    }

    @Override
    public AbstractCond clone() {
        AmbienceIsPlayingCond cond = new AmbienceIsPlayingCond(equal, sound);
        return cond;
    }

    @Override
    public String getName() {
        return "ambience.isplaying";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + sound;
    }

    @Override
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(AmbienceController.instance.isSoundPlaying(sound));
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
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putString(SOUND, sound);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        //equal = AmbienceEquality.values()[nbt.getInt(EQUAL) < AmbienceEquality.values().length ? nbt.getInt(EQUAL) : 0];
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        sound = nbt.getString(SOUND);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(sound, 50);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        //this.equal = AmbienceEquality.values()[buf.readInt()];
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        this.sound = buf.readString(50);
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
