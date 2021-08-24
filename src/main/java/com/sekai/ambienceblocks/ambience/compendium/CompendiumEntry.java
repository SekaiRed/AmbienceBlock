package com.sekai.ambienceblocks.ambience.compendium;

import com.sekai.ambienceblocks.ambience.bounds.NoneBounds;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

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
        ClientPlayerEntity player = Minecraft.getInstance().player;
        Vector3d oPos = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vector3d(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vector3d(0.5, 0.5, 0.5));
    }

    @Override
    public boolean isWithinBounds(PlayerEntity player) {
        return true;
    }
    @Override
    public double distanceTo(PlayerEntity player) {
        return 0D;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(PlayerEntity player) {
        return 1D;
    }
}
