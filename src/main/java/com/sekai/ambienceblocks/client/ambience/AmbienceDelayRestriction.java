package com.sekai.ambienceblocks.client.ambience;

import com.sekai.ambienceblocks.ambience.IAmbienceSource;

public class AmbienceDelayRestriction {
    private final IAmbienceSource source;
    private int tickLeft;

    private final int originalTick;

    public AmbienceDelayRestriction(IAmbienceSource source, int tickLeft) {
        this.source = source;
        this.tickLeft = tickLeft;
        this.originalTick = tickLeft;
    }

    public IAmbienceSource getSource() {
        return source;
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
        return getSource().getData().getSoundName() +
                ", " + AmbienceController.instance.getAmbienceSourceName(source) +
                ", originalTick=" + originalTick +
                ", tickLeft=" + tickLeft;
    }
}
