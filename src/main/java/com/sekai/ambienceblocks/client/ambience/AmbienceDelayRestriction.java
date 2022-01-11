package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.ambience.IAmbienceSource;

public class AmbienceDelayRestriction {
    private final IAmbienceSource origin;
    private int tickLeft;

    //rather than getting a new delay amount I just use the original one, because if it failed to play
    //then you might as well keep trying with the same one, the player would never realize and it reduce queries
    private final int originalTick;

    public AmbienceDelayRestriction(IAmbienceSource origin, int tickLeft) {
        this.origin = origin;
        this.tickLeft = tickLeft;
        this.originalTick = tickLeft;
    }

    public IAmbienceSource getOrigin() {
        return origin;
    }

    public void tick() {
        tickLeft--;
    }

    public boolean isDone() {
        return tickLeft <= 0;
    }

    public void restart() {
        tickLeft = originalTick;
    }

    @Override
    public String toString() {
        return getOrigin().getData().getSoundName() +
                ", " + AmbienceController.instance.getAmbienceSourceName(origin) +
                ", originalTick=" + originalTick +
                ", tickLeft=" + tickLeft;
    }
}
