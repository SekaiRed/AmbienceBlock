package com.sekai.ambienceblocks.ambience;

import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.entity.player.EntityPlayer;

public interface IAmbienceSource {
    AmbienceData getData();
    Vector3d getOrigin();
    //boolean isWithinBounds(PlayerEntity player);
    //double distanceTo(PlayerEntity player);

    default boolean isWithinBounds(EntityPlayer player) {
        return getData().isWithinBounds(player, getOrigin());
    }
    default double distanceTo(EntityPlayer player) {
        return getData().distanceFromCenter(player, getOrigin());
    }
    default double getPercentageHowCloseIsPlayer(EntityPlayer player) {
        return getData().getPercentageHowCloseIsPlayer(player, getOrigin());
    }
}