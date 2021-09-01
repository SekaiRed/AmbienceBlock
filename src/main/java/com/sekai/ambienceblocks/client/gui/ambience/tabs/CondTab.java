package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.ambience.EditCondGUI;
import com.sekai.ambienceblocks.client.gui.ambience.IFetchCond;
import com.sekai.ambienceblocks.client.gui.widgets.StringListWidget;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.conds.AbstractCond;
import com.sekai.ambienceblocks.ambience.conds.AlwaysTrueCond;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CondTab extends AbstractTab implements IFetchCond {
    List<AbstractCond> condList = new ArrayList<>();

    //widgets
    Button edit = new Button(0, 0, 30, 20, new TextComponentString("Edit"), button -> {
        buttonEdit();
    });
    Button add = new Button(0, 0, 30, 20, new TextComponentString("Add"), button -> {
        buttonAdd();
    });
    Button remove = new Button(0, 0, 30, 20, new TextComponentString("Del"), button -> {
        buttonRemove();
    });
    Button up = new Button(0, 0, 30, 20, new TextComponentString("Up"), button -> {
        buttonUp();
    });
    Button down = new Button(0, 0, 30, 20, new TextComponentString("Down"), button -> {
        buttonDown();
    });

    StringListWidget condGuiList = new StringListWidget(getBaseX(), getRowY(0), getEndX() - getBaseX(), getRowY(5) - getRowY(0) - verticalSeparation, 4, 16, font, null);

    public CondTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Condition";
    }

    @Override
    public String getShortName() {
        return "Cond";
    }

    @Override
    public void initialInit() {
        //condList.add(new PlayerPosToValueCond(AmbienceTest.GREATER_THAN, AmbienceAxis.Y, 40D));
        //condList.add(new PlayerPosToValueCond(AmbienceTest.LESSER_THAN, AmbienceAxis.Y, 120D));
        updateGuiCondList();
        //musicList.addElement("[player.test.axis] < Y 120");
        //musicList.addElement("[player.test.axis] > X 50");

        addWidget(edit);
        addWidget(add);
        addWidget(remove);
        addWidget(up);
        addWidget(down);
        addWidget(condGuiList);
    }

    @Override
    public void updateWidgetPosition() {
        edit.x = getBaseX(); edit.y = getRowY(5);
        add.x = getNeighbourX(edit); add.y = getRowY(5);
        remove.x = getNeighbourX(add); remove.y = getRowY(5);
        up.x = getNeighbourX(remove); up.y = getRowY(5);
        down.x = getNeighbourX(up); down.y = getRowY(5);
        condGuiList.x = getBaseX(); condGuiList.y = getRowY(0);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        /*AmbienceScreen.Layer layer = AmbienceScreen.Layer.LOWEST;
        edit.render(mouseX, mouseY, layer);
        add.render(mouseX, mouseY, layer);
        remove.render(mouseX, mouseY, layer);
        up.render(mouseX, mouseY, layer);
        down.render(mouseX, mouseY, layer);

        condGuiList.render(mouseX, mouseY, layer);*/
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        //List<String> list = new ArrayList<String>();
    }

    @Override
    public void tick() {

    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        condList.clear();
        condList.addAll(data.getConditions());
        updateGuiCondList();
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setConditions(condList);
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    private void buttonEdit() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 0) {
            Minecraft.getMinecraft().displayGuiScreen(new EditCondGUI(this.screen, this, condList.get(condGuiList.getSelectionIndex())));
        }
    }

    private void buttonAdd() {
        addCond(new AlwaysTrueCond());
    }

    private void buttonRemove() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 0) {
            removeCond(condGuiList.getSelectionIndex());
        }
    }

    private void buttonUp() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() && condGuiList.getSelectionIndex() >= 1) {
            Collections.swap(condList, condGuiList.getSelectionIndex(), condGuiList.getSelectionIndex() - 1);
            updateGuiCondList();
            condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() - 1);
        }
    }

    private void buttonDown() {
        if(condGuiList.getSelectionIndex() < condGuiList.getAmountOfElements() - 1 && condGuiList.getSelectionIndex() >= 0) {
            Collections.swap(condList, condGuiList.getSelectionIndex(), condGuiList.getSelectionIndex() + 1);
            updateGuiCondList();
            condGuiList.setSelectionIndex(condGuiList.getSelectionIndex() + 1);
        }
    }

    public void addCond(AbstractCond cond) {
        condList.add(cond);
        updateGuiCondList();
        condGuiList.setSelectionIndexToLast();
    }

    public void removeCond(int index) {
        if(index >= condList.size()) return;

        condList.remove(index);
        updateGuiCondList();
        if(condGuiList.getSelectionIndex() >= condGuiList.getAmountOfElements())
            condGuiList.setSelectionIndexToLast();
    }

    public void replaceCond(int index, AbstractCond cond) {
        //System.out.println(cond.getListDescription());
        //System.out.println(condList.get(index).getListDescription());

        removeCond(index);
        condList.add(index, cond);
        updateGuiCondList();
        condGuiList.setSelectionIndex(index);
    }

    public void updateGuiCondList() {
        condGuiList.clearList();
        for(AbstractCond cond : condList) {
            condGuiList.addElement(cond.getListDescription());
        }
        //condGuiList.addElement("[player.test.axis] < Y 120");
    }

    @Override
    public void fetch(AbstractCond newCond, AbstractCond oldCond) {
        for (int i = 0; i < condList.size(); i++) {
            AbstractCond cond = condList.get(i);
            if (cond.equals(oldCond)) {
                replaceCond(i, newCond.copy());
                return;
            }
        }
    }
}
