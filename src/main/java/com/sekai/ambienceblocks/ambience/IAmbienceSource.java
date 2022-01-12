package com.sekai.ambienceblocks.ambience;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

//Makes it a valid ambience source for ambience controller
//Added to provide ambience to things that lack a tile entity
public interface IAmbienceSource {
    AmbienceData getData();
    Vec3 getOrigin();
    //boolean isWithinBounds(PlayerEntity player);
    //double distanceTo(PlayerEntity player);

    default boolean isWithinBounds(Player player) {
        return getData().isWithinBounds(player, getOrigin());
    }
    default double distanceTo(Player player) {
        return getData().distanceFromCenter(player, getOrigin());
    }
    default double getPercentageHowCloseIsPlayer(Player player) {
        return getData().getPercentageHowCloseIsPlayer(player, getOrigin());
    }
}
