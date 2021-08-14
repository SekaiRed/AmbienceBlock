package com.sekai.ambienceblocks.world;

import com.sekai.ambienceblocks.tileentity.AmbienceData;
import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWorldSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class CompendiumEntry implements IAmbienceSource {
    public AmbienceData data = new AmbienceData();

    public CompendiumEntry(AmbienceData data) {
        this.data = data;
    }

    @Override
    public AmbienceData getData() {
        return data;
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
