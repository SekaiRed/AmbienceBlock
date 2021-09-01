package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetCond;
import com.sekai.ambienceblocks.util.CondsUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AndCond extends AbstractCond {
    private AbstractCond leftCond;
    private AbstractCond rightCond;

    private static final String COND1 = "left_cond";
    private static final String COND2 = "right_cond";

    public AndCond(AbstractCond leftCond, AbstractCond rightCond) {
        this.leftCond = leftCond;
        this.rightCond = rightCond;
    }

    @Override
    public AbstractCond clone() {
        AndCond cond = new AndCond(leftCond, rightCond);
        return cond;
    }

    @Override
    public String getName() {
        return "and";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + leftCond.getName() + " && " + rightCond.getName();
    }

    @Override
    public boolean isTrue(EntityPlayer player, World worldIn, IAmbienceSource sourceIn) {
        return leftCond.isTrue(player, worldIn, sourceIn) && rightCond.isTrue(player, worldIn, sourceIn);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetCond(COND1, "", 220, leftCond));
        list.add(new AmbienceWidgetCond(COND2, "", 220, rightCond));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(COND1.equals(widget.getKey()) && widget instanceof AmbienceWidgetCond)
                leftCond = ((AmbienceWidgetCond) widget).getCond();
            if(COND2.equals(widget.getKey()) && widget instanceof AmbienceWidgetCond)
                rightCond = ((AmbienceWidgetCond) widget).getCond();
        }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(COND1, CondsUtil.toNBT(leftCond));
        nbt.setTag(COND2, CondsUtil.toNBT(rightCond));
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey(COND1))
            leftCond = CondsUtil.fromNBT((NBTTagCompound) Objects.requireNonNull(nbt.getTag(COND1)));
        if(nbt.hasKey(COND2))
            rightCond = CondsUtil.fromNBT((NBTTagCompound) Objects.requireNonNull(nbt.getTag(COND2)));
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        CondsUtil.toBuff(leftCond, buf);
        CondsUtil.toBuff(rightCond, buf);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        leftCond = CondsUtil.fromBuff(buf);
        rightCond = CondsUtil.fromBuff(buf);
    }

    @Override
    public void toJson(JsonObject json) {
        json.add(COND1, CondsUtil.toJson(leftCond));
        json.add(COND2, CondsUtil.toJson(rightCond));
    }

    @Override
    public void fromJson(JsonObject json) {
        leftCond = CondsUtil.fromJson(json.get(COND1).getAsJsonObject());
        rightCond = CondsUtil.fromJson(json.get(COND2).getAsJsonObject());
    }
}
