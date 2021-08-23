package com.sekai.ambienceblocks.ambience.util.messenger;

import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.client.gui.ambience.EditCondGUI;

public class AmbienceWidgetCond extends AbstractAmbienceWidgetMessenger {
    AbstractCond cond;

    public AmbienceWidgetCond(String key, String label, int width, AbstractCond cond) {
        EditCondGUI.printConditionHash("initial widget", cond);
        this.key = key;
        this.label = label;
        this.width = width;
        this.cond = cond;
    }

    public AbstractCond getCond() {
        return cond;
    }

    public void setCond(AbstractCond cond) {
        this.cond = cond;
    }
}
