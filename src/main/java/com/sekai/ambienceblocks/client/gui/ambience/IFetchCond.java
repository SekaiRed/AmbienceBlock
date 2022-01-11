package com.sekai.ambienceblocks.client.gui.ambience;

import com.sekai.ambienceblocks.ambience.conds.AbstractCond;

public interface IFetchCond {
    void fetch(AbstractCond newCond, AbstractCond oldCond);
}
