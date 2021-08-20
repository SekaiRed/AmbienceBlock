package com.sekai.ambienceblocks.ambience;

import com.sekai.ambienceblocks.ambience.AmbienceData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

//Makes it a valid ambience source for ambience controller
//Added to provide ambience to things that lack a tile entity
public interface IAmbienceSource {
    AmbienceData getData();
    Vector3d getOrigin();
    //boolean isWithinBounds(PlayerEntity player);
    //double distanceTo(PlayerEntity player);

    default boolean isWithinBounds(PlayerEntity player) {
        return getData().isWithinBounds(player, getOrigin());
    }
    default double distanceTo(PlayerEntity player) {
        return getData().distanceFromCenter(player, getOrigin());
    }
    default double getPercentageHowCloseIsPlayer(PlayerEntity player) {
        return getData().getPercentageHowCloseIsPlayer(player, getOrigin());
    }
}
