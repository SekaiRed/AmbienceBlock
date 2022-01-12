package com.sekai.ambienceblocks.ambience.compendium;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.bounds.NoneBounds;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
    public Vec3 getOrigin() {
        LocalPlayer player = Minecraft.getInstance().player;
        Vec3 oPos = new Vec3(player.getX(), player.getY(), player.getZ());
        if(AmbienceWorldSpace.RELATIVE.equals(data.getSpace()))
            return oPos.add(data.getOffset()).add(new Vec3(0.5, 0.5, 0.5));
        else
            return data.getOffset().add(new Vec3(0.5, 0.5, 0.5));
    }

    @Override
    public boolean isWithinBounds(Player player) {
        return true;
    }
    @Override
    public double distanceTo(Player player) {
        return 0D;
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player) {
        return 1D;
    }
}
