package com.sekai.ambienceblocks.ambience.compendium;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.bounds.NoneBounds;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

public class CompendiumEntry implements IAmbienceSource {
    public AmbienceData data = new AmbienceData();

    public CompendiumEntry(AmbienceData data) {
        this.data = data;
        updateData(this.data);
    }

    private void updateData(AmbienceData data) {
        //Should always be true by default
        data.setBounds(new NoneBounds());
        data.setGlobal(true);
        data.setLocatable(false);
    }

    //I thought this would be way easier to pull off, what the fuck
    public CompendiumEntry copy() {
        return new CompendiumEntry(data.copy());
    }

    @Override
    public AmbienceData getData() {
        return data;
    }

    public void setData(AmbienceData data) {
        this.data = data;
        updateData(this.data);
    }

    @Override
    public Vector3d getOrigin() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Vector3d oPos = new Vector3d(player.getPositionVector());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vector3d(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vector3d(0.5, 0.5, 0.5));
    }

    @Override
    public boolean isWithinBounds(EntityPlayer player) {
        return true;
    }
    @Override
    public double distanceTo(EntityPlayer player) {
        return 0D;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(EntityPlayer player) {
        return 1D;
    }
}