package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceEquality;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.json.Hidden;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerPosWithinRegionCond extends AbstractCond {
    private double xS;
    private double yS;
    private double zS;
    private double xF;
    private double yF;
    private double zF;
    private AmbienceEquality equal;
    private AmbienceWorldSpace space;

    private static final String XS = "xS";
    private static final String YS = "yS";
    private static final String ZS = "zS";
    private static final String XF = "xF";
    private static final String YF = "yF";
    private static final String ZF = "zF";
    private static final String EQUAL = "equal";
    private static final String SPACE = "space";

    //nbt only
    private static final String START = "startPos";
    private static final String FINAL = "finalPos";

    //only generated with cond to not have to create a new one every time I need to check the condition
    @Hidden
    private AxisAlignedBB boundingBox;

    public PlayerPosWithinRegionCond(double xS, double yS, double zS, double xF, double yF, double zF, AmbienceEquality equal, AmbienceWorldSpace space) {
        this.xS = xS;
        this.yS = yS;
        this.zS = zS;
        this.xF = xF;
        this.yF = yF;
        this.zF = zF;
        this.equal = equal;
        this.space = space;

        updateBoundingBox();
    }

    @Override
    public AbstractCond clone() {
        return new PlayerPosWithinRegionCond(xS, yS, zS, xF, yF, zF, equal, space);
    }

    @Override
    public String getName() {
        return "player.pos.within.region";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName() + " " + space.getName() + " " + getStartPos() + " to " + getFinalPos();
    }

    @Override
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        if(AmbienceWorldSpace.ABSOLUTE.equals(space))
            return equal.testFor(boundingBox.contains(getPlayerPos(player)));
        else
            return equal.testFor(boundingBox.contains(getPlayerPos(player).subtract(sourceIn.getOrigin())));
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetString(XS, "X1 :", 45, Double.toString(xS), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(YS, "Y1 :", 45, Double.toString(yS), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(ZS, "Z1 :", 45, Double.toString(zS), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(XF, "X2 :", 45, Double.toString(xF), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(YF, "Y2 :", 45, Double.toString(yF), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(ZF, "Z2 :", 45, Double.toString(zF), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetEnum<>(SPACE, "",20, space));
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        allWidgets.forEach(widget -> {
            if (XS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                xS = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (YS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                yS = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (ZS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                zS = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (XF.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                xF = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (YF.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                yF = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (ZF.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                zF = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (SPACE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                space = (AmbienceWorldSpace) ((AmbienceWidgetEnum) widget).getValue();
            if (EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
        });

        updateBoundingBox();
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        //NBTHelper.readVec3d(compound.getCompound("offset"));
        nbt.put(START, NBTHelper.writeVec3d(getStartPos()));
        nbt.put(FINAL, NBTHelper.writeVec3d(getFinalPos()));
        nbt.putInt(SPACE, space.ordinal());
        nbt.putInt(EQUAL, equal.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        Vector3d startPos = NBTHelper.readVec3d(nbt.getCompound(START));
        Vector3d finalPos = NBTHelper.readVec3d(nbt.getCompound(FINAL));
        xS = startPos.x;
        yS = startPos.y;
        zS = startPos.z;
        xF = finalPos.x;
        yF = finalPos.y;
        zF = finalPos.z;
        space = StaticUtil.getEnumValue(nbt.getInt(SPACE), AmbienceWorldSpace.values());
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());

        updateBoundingBox();
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(xS);
        buf.writeDouble(yS);
        buf.writeDouble(zS);
        buf.writeDouble(xF);
        buf.writeDouble(yF);
        buf.writeDouble(zF);
        buf.writeInt(space.ordinal());
        buf.writeInt(equal.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        xS = buf.readDouble();
        yS = buf.readDouble();
        zS = buf.readDouble();
        xF = buf.readDouble();
        yF = buf.readDouble();
        zF = buf.readDouble();

        space = StaticUtil.getEnumValue(buf.readInt(), AmbienceWorldSpace.values());
        equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());

        updateBoundingBox();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(XS, xS);
        json.addProperty(YS, yS);
        json.addProperty(ZS, zS);
        json.addProperty(XF, xF);
        json.addProperty(YF, yF);
        json.addProperty(ZF, zF);
        json.addProperty(SPACE, space.name());
        json.addProperty(EQUAL, equal.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        xS = json.get(XS).getAsDouble();
        yS = json.get(YS).getAsDouble();
        zS = json.get(ZS).getAsDouble();
        xF = json.get(XF).getAsDouble();
        yF = json.get(YF).getAsDouble();
        zF = json.get(ZF).getAsDouble();
        space = StaticUtil.getEnumValue(json.get(SPACE).getAsString(), AmbienceWorldSpace.values());
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());

        updateBoundingBox();
    }

    private void updateBoundingBox() {
        boundingBox = new AxisAlignedBB(getStartPos(), getFinalPos());
    }

    private Vector3d getStartPos() {
        return new Vector3d(xS, yS, zS);
    }

    private Vector3d getFinalPos() {
        return new Vector3d(xF, yF, zF);
    }
}
