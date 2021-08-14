package com.sekai.ambienceblocks.tileentity.ambiencetilecond;

import com.sekai.ambienceblocks.tileentity.IAmbienceSource;
import com.sekai.ambienceblocks.tileentity.util.AmbienceEquality;
import com.sekai.ambienceblocks.tileentity.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.tileentity.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.tileentity.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerBlockCond extends AbstractCond {
    private double x;
    private double y;
    private double z;
    private AmbienceEquality equal;
    private AmbienceWorldSpace space;
    private String block;

    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String EQUAL = "equal";
    private static final String SPACE = "space";
    private static final String BLOCK = "block";

    //nbt only
    private static final String POSITION = "pos";

    //internal reference
    private BlockPos pos;

    public PlayerBlockCond(double x, double y, double z, AmbienceEquality equal, AmbienceWorldSpace space, String block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.equal = equal;
        this.space = space;
        this.block = block;
    }

    @Override
    public AbstractCond clone() {
        PlayerBlockCond cond = new PlayerBlockCond(x, y, z, equal, space, block);
        return cond;
    }

    @Override
    public String getName() {
        return "player.block";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + space.getName() + " " + equal.getName() + " " + block + " " + getPos();
    }

    @Override
    public boolean isTrue(Vector3d playerPos, World worldIn, IAmbienceSource sourceIn) {
        if(AmbienceWorldSpace.ABSOLUTE.equals(space))
            pos = new BlockPos(getPos());
        else
            pos = new BlockPos(playerPos.add(getPos()));

        return equal.testFor(doesBlockRegistryNameHasString(worldIn, pos, block));
    }

    private boolean doesBlockRegistryNameHasString(World world, BlockPos pos, String contain) {
        return world.getBlockState(pos).getBlock().getRegistryName().toString().contains(contain);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetString(X, "X :", 50, Double.toString(x), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(Y, "Y :", 50, Double.toString(y), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(Z, "Z :", 50, Double.toString(z), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        list.add(new AmbienceWidgetEnum<>(SPACE, "",20, space));
        list.add(new AmbienceWidgetString(BLOCK, "Block :", 120, block));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if (X.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                x = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (Y.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                y = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (Z.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                z = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
            if (SPACE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                space = (AmbienceWorldSpace) ((AmbienceWidgetEnum) widget).getValue();
            if(BLOCK.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                block = ((AmbienceWidgetString) widget).getValue();
        }
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(POSITION, NBTHelper.writeVec3d(getPos()));
        nbt.putInt(EQUAL, equal.ordinal());
        nbt.putInt(SPACE, space.ordinal());
        nbt.putString(BLOCK, block);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundNBT nbt) {
        Vector3d startPos = NBTHelper.readVec3d(nbt.getCompound(POSITION));
        x = startPos.x;
        y = startPos.y;
        z = startPos.z;
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
        space = StaticUtil.getEnumValue(nbt.getInt(SPACE), AmbienceWorldSpace.values());
        block = nbt.getString(BLOCK);
    }

    @Override
    public void toBuff(PacketBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(equal.ordinal());
        buf.writeInt(space.ordinal());
        buf.writeString(block, 50);
    }

    @Override
    public void fromBuff(PacketBuffer buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
        space = StaticUtil.getEnumValue(buf.readInt(), AmbienceWorldSpace.values());
        block = buf.readString(50);
    }

    private Vector3d getPos() {
        return new Vector3d(x, y, z);
    }
}
