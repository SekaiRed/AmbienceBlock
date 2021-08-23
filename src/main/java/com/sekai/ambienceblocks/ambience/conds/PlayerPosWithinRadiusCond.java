package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerPosWithinRadiusCond extends AbstractCond {
    private double x;
    private double y;
    private double z;
    private double radius;
    private AmbienceTest test;
    private AmbienceWorldSpace space;

    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String RADIUS = "radius";
    private static final String TEST = "test";
    private static final String SPACE = "space";

    //nbt only
    private static final String POSITION = "pos";

    public PlayerPosWithinRadiusCond(double x, double y, double z, double radius, AmbienceTest test, AmbienceWorldSpace space) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.test = test;
        this.space = space;
    }

    @Override
    public AbstractCond clone() {
        return new PlayerPosWithinRadiusCond(x, y, z, radius, test, space);
    }

    @Override
    public String getName() {
        return "player.pos.within.radius";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + space.getName() + " " + getPos() + " " + radius;
    }

    @Override
    public boolean isTrue(PlayerEntity player, World worldIn, IAmbienceSource sourceIn) {
        Vector3d pos = getPos();
        if(AmbienceWorldSpace.ABSOLUTE.equals(space))
            return test.testForDouble(pos.distanceTo(getPlayerPos(player)), radius);
        else
            return test.testForDouble(sourceIn.getOrigin().add(pos).distanceTo(getPlayerPos(player)), radius);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetString(X, "X :", 50, Double.toString(x), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(Y, "Y :", 50, Double.toString(y), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(Z, "Z :", 50, Double.toString(z), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(RADIUS, "Radius :", 40, Double.toString(radius), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetEnum<>(SPACE, "",20, space));
        list.add(new AmbienceWidgetEnum<>(TEST, "", 20, test));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        allWidgets.forEach(widget -> {
            if (X.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                x = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (Y.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                y = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (Z.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                z = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (RADIUS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                radius = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (SPACE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                space = (AmbienceWorldSpace) ((AmbienceWidgetEnum) widget).getValue();
            if (TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
        });
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(POSITION, NBTHelper.writeVec3d(getPos()));
        nbt.putDouble(RADIUS, radius);
        nbt.putInt(SPACE, space.ordinal());
        nbt.putInt(TEST, test.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        Vector3d startPos = NBTHelper.readVec3d(nbt.getCompound(POSITION));
        x = startPos.x;
        y = startPos.y;
        z = startPos.z;
        radius = nbt.getDouble(RADIUS);

        space = StaticUtil.getEnumValue(nbt.getInt(SPACE), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values());
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(radius);
        buf.writeInt(space.ordinal());
        buf.writeInt(test.ordinal());
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        radius = buf.readDouble();

        space = StaticUtil.getEnumValue(buf.readInt(), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(X, x);
        json.addProperty(Y, y);
        json.addProperty(Z, z);
        json.addProperty(RADIUS, radius);
        json.addProperty(SPACE, space.name());
        json.addProperty(TEST, test.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        x = json.get(X).getAsDouble();
        y = json.get(Y).getAsDouble();
        z = json.get(Z).getAsDouble();
        radius = json.get(RADIUS).getAsDouble();
        space = StaticUtil.getEnumValue(json.get(SPACE).getAsString(), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
    }

    private Vector3d getPos() {
        return new Vector3d(x, y, z);
    }
}
