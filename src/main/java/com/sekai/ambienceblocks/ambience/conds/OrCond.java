package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetCond;
import com.sekai.ambienceblocks.util.CondsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrCond extends AbstractCond {
    private AbstractCond leftCond;
    private AbstractCond rightCond;

    private static final String COND1 = "left_cond";
    private static final String COND2 = "right_cond";

    public OrCond(AbstractCond leftCond, AbstractCond rightCond) {
        this.leftCond = leftCond;
        this.rightCond = rightCond;
    }

    @Override
    public AbstractCond clone() {
        OrCond cond = new OrCond(leftCond, rightCond);
        return cond;
    }

    @Override
    public String getName() {
        return "or";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + leftCond.getName() + " || " + rightCond.getName();
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        return leftCond.isTrue(player, worldIn, sourceIn) || rightCond.isTrue(player, worldIn, sourceIn);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        //list.add(new AmbienceWidgetCond(COND1, "", 180, CondsUtil.CondList.ALWAYS_TRUE.getDefault()));
        //list.add(new AmbienceWidgetCond(COND2, "", 180, CondsUtil.CondList.ALWAYS_TRUE.getDefault()));
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
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(COND1, CondsUtil.toNBT(leftCond));
        nbt.put(COND2, CondsUtil.toNBT(rightCond));
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        if(nbt.get(COND1) != null)
            leftCond = CondsUtil.fromNBT((CompoundTag) Objects.requireNonNull(nbt.get(COND1)));
        if(nbt.get(COND2) != null)
            rightCond = CondsUtil.fromNBT((CompoundTag) Objects.requireNonNull(nbt.get(COND2)));
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        CondsUtil.toBuff(leftCond, buf);
        CondsUtil.toBuff(rightCond, buf);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        leftCond = CondsUtil.fromBuff(buf);
        rightCond = CondsUtil.fromBuff(buf);
    }

    @Override
    public void toJson(JsonObject json) {
        /*json.addProperty(EQUAL, equal.name());
        json.addProperty(SOUND, sound);*/
        //JsonUtil.GSON.toJsonTree(leftCond);

        //json.add(COND1, JsonUtil.GSON.toJsonTree(leftCond));
        //json.add(COND2, JsonUtil.GSON.toJsonTree(rightCond));

        json.add(COND1, CondsUtil.toJson(leftCond));
        json.add(COND2, CondsUtil.toJson(rightCond));
    }

    @Override
    public void fromJson(JsonObject json) {
        /*equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
        sound = json.get(SOUND).getAsString();*/

        //leftCond = JsonUtil.GSON.fromJson(json.get(COND1), AbstractCond.class);
        //rightCond = JsonUtil.GSON.fromJson(json.get(COND2), AbstractCond.class);

        leftCond = CondsUtil.fromJson(json.get(COND1).getAsJsonObject());
        rightCond = CondsUtil.fromJson(json.get(COND2).getAsJsonObject());
    }
}
