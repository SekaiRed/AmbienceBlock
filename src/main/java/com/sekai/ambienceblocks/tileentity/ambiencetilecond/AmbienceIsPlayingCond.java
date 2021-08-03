package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.tileentity.util.AmbienceEquality;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetSound;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
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
    public boolean isTrue(Vector3d playerPos, BlockPos blockPos, World worldIn, AmbienceTileEntity tileIn) {
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
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(EQUAL, equal.ordinal());
        nbt.setString(SOUND, sound);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        equal = AmbienceEquality.values()[nbt.getInteger(EQUAL) < AmbienceEquality.values().length ? nbt.getInteger(EQUAL) : 0];
        sound = nbt.getString(SOUND);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeInt(equal.ordinal());
        buf.writeString(sound);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        this.equal = AmbienceEquality.values()[buf.readInt()];
        this.sound = buf.readString(50);
    }
}
