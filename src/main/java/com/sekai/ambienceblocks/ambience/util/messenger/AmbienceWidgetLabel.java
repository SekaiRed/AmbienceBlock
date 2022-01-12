package com.sekai.ambienceblocks.ambience.util.messenger;

import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.client.gui.ambience.EditCondGUI;

public class AmbienceWidgetLabel extends AbstractAmbienceWidgetMessenger {
    public AmbienceWidgetLabel(String label) {
        this.key = label;
        this.label = label;
        this.width = 0;
    }
}
