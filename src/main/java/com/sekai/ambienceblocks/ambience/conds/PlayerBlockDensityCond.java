package com.sekai.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.sekai.ambienceblocks.ambience.IAmbienceSource;
import com.sekai.ambienceblocks.ambience.util.AmbienceTest;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetLabel;
import com.sekai.ambienceblocks.ambience.util.messenger.AmbienceWidgetString;
import com.sekai.ambienceblocks.config.AmbienceConfig;
import com.sekai.ambienceblocks.util.NBTHelper;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.json.Hidden;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PlayerBlockDensityCond extends AbstractCond {
    private double xSize;
    private double ySize;
    private double zSize;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private AmbienceWorldSpace space;
    private AmbienceTest test;
    private int count;
    private String block;

    private static final String XS = "xS";
    private static final String YS = "yS";
    private static final String ZS = "zS";
    private static final String XO = "xO";
    private static final String YO = "yO";
    private static final String ZO = "zO";
    private static final String SPACE = "space";
    private static final String TEST = "test";
    private static final String COUNT = "count";
    private static final String BLOCK = "block";

    //nbt only
    private static final String SIZE = "startPos";
    private static final String OFFSET = "finalPos";

    @Hidden
    private AABB boundingBox;

    public PlayerBlockDensityCond(double xSize, double ySize, double zSize, double xOffset, double yOffset, double zOffset, AmbienceWorldSpace space, AmbienceTest test, int count, String block) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.space = space;
        this.test = test;
        this.count = count;
        this.block = block;
    }

    @Override
    public AbstractCond clone() {
        PlayerBlockDensityCond cond = new PlayerBlockDensityCond(xSize, ySize, zSize, xOffset, yOffset, zOffset, space, test, count, block);
        return cond;
    }

    @Override
    public String getName() {
        return "player.block.density";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + test.getName() + " " + count + " " + block + " " + space.getName() + " " + getSize() + " at " + getOffset();
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        Vec3 offset = getOffset();

        if(AmbienceWorldSpace.RELATIVE.equals(space))
            offset = offset.add(getPlayerPos(player));

        boundingBox = new AABB(offset.add(getSize().scale(-0.5D)), offset.add(getSize().scale(0.5D)));

        return test.testForInt(countBlocksWithinAABB(boundingBox, worldIn), count);
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetLabel("Size"));
        list.add(new AmbienceWidgetString(XS, "X", 40, Double.toString(xSize), 8, ParsingUtil.decimalNumberFilter));
        list.add(new AmbienceWidgetString(YS, "Y", 40, Double.toString(ySize), 8, ParsingUtil.decimalNumberFilter));
        list.add(new AmbienceWidgetString(ZS, "Z", 40, Double.toString(zSize), 8, ParsingUtil.decimalNumberFilter));
        list.add(new AmbienceWidgetLabel("Offset"));
        list.add(new AmbienceWidgetString(XO, "X", 40, Double.toString(xOffset), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(YO, "Y", 40, Double.toString(yOffset), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetString(ZO, "Z", 40, Double.toString(zOffset), 8, ParsingUtil.negativeDecimalNumberFilter));
        list.add(new AmbienceWidgetEnum<>(SPACE, "",20, space));
        list.add(new AmbienceWidgetEnum<>(TEST, "",20, test));
        list.add(new AmbienceWidgetString(COUNT, "Count :", 100, Integer.toString(count), 5, ParsingUtil.numberFilter));
        list.add(new AmbienceWidgetString(BLOCK, "Block :", 140, block, StaticUtil.LENGTH_COND_INPUT));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        allWidgets.forEach(widget -> {
            if (XS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                xSize = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (YS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                ySize = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (ZS.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                zSize = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (XO.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                xOffset = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (YO.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                yOffset = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (ZO.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                zOffset = ParsingUtil.tryParseDouble(((AmbienceWidgetString) widget).getValue());
            if (SPACE.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                space = (AmbienceWorldSpace) ((AmbienceWidgetEnum) widget).getValue();
            if (TEST.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                test = (AmbienceTest) ((AmbienceWidgetEnum) widget).getValue();
            if (COUNT.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                count = ParsingUtil.tryParseInt(((AmbienceWidgetString) widget).getValue());
            if (BLOCK.equals(widget.getKey()) && widget instanceof AmbienceWidgetString)
                block = ((AmbienceWidgetString) widget).getValue();
        });
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        //NBTHelper.readVec3d(compound.getCompound("offset"));
        nbt.put(SIZE, NBTHelper.writeVec3d(getSize()));
        nbt.put(OFFSET, NBTHelper.writeVec3d(getOffset()));
        nbt.putInt(SPACE, space.ordinal());
        nbt.putInt(TEST, test.ordinal());
        nbt.putInt(COUNT, count);
        nbt.putString(BLOCK, block);
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        Vec3 startPos = NBTHelper.readVec3d(nbt.getCompound(SIZE));
        Vec3 finalPos = NBTHelper.readVec3d(nbt.getCompound(OFFSET));
        xSize = startPos.x;
        ySize = startPos.y;
        zSize = startPos.z;
        xOffset = finalPos.x;
        yOffset = finalPos.y;
        zOffset = finalPos.z;
        space = StaticUtil.getEnumValue(nbt.getInt(SPACE), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(nbt.getInt(TEST), AmbienceTest.values());
        count = nbt.getInt(COUNT);
        block = nbt.getString(BLOCK);

        //updateBoundingBox();
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeDouble(xSize);
        buf.writeDouble(ySize);
        buf.writeDouble(zSize);
        buf.writeDouble(xOffset);
        buf.writeDouble(yOffset);
        buf.writeDouble(zOffset);
        buf.writeInt(space.ordinal());
        buf.writeInt(test.ordinal());
        buf.writeInt(count);
        buf.writeUtf(block, StaticUtil.LENGTH_COND_INPUT);
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        xSize = buf.readDouble();
        ySize = buf.readDouble();
        zSize = buf.readDouble();
        xOffset = buf.readDouble();
        yOffset = buf.readDouble();
        zOffset = buf.readDouble();

        space = StaticUtil.getEnumValue(buf.readInt(), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(buf.readInt(), AmbienceTest.values());
        count = buf.readInt();
        block = buf.readUtf(StaticUtil.LENGTH_COND_INPUT);

        //updateBoundingBox();
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(XS, xSize);
        json.addProperty(YS, ySize);
        json.addProperty(ZS, zSize);
        json.addProperty(XO, xOffset);
        json.addProperty(YO, yOffset);
        json.addProperty(ZO, zOffset);
        json.addProperty(SPACE, space.name());
        json.addProperty(TEST, test.name());
        json.addProperty(COUNT, count);
        json.addProperty(BLOCK, block);
    }

    @Override
    public void fromJson(JsonObject json) {
        xSize = json.get(XS).getAsDouble();
        ySize = json.get(YS).getAsDouble();
        zSize = json.get(ZS).getAsDouble();
        xOffset = json.get(XO).getAsDouble();
        yOffset = json.get(YO).getAsDouble();
        zOffset = json.get(ZO).getAsDouble();
        space = StaticUtil.getEnumValue(json.get(SPACE).getAsString(), AmbienceWorldSpace.values());
        test = StaticUtil.getEnumValue(json.get(TEST).getAsString(), AmbienceTest.values());
        count = json.get(COUNT).getAsInt();
        block = json.get(BLOCK).getAsString();

        //updateBoundingBox();
    }

    private int countBlocksWithinAABB(AABB bb, Level world) {
        int i = Mth.floor(bb.minX);
        int j = Mth.ceil(bb.maxX);
        int k = Mth.floor(bb.minY);
        int l = Mth.ceil(bb.maxY);
        int i1 = Mth.floor(bb.minZ);
        int j1 = Mth.ceil(bb.maxZ);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        int counter = 0;

        for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
                for(int i2 = i1; i2 < j1; ++i2) {
                    BlockState blockstate = world.getBlockState(blockpos$mutable.set(k1, l1, i2));
                    if (stringValidation(blockstate.getBlock().getRegistryName().toString(), block))
                        counter++;
                }
            }
        }

        return counter;
    }

    private Vec3 getSize() {
        return new Vec3(getClamped(xSize), getClamped(ySize), getClamped(zSize));
    }

    private double getClamped(double value) {
        return Math.min(value, AmbienceConfig.maxBlockSearchRange);
    }

    private Vec3 getOffset() {
        return new Vec3(xOffset, yOffset, zOffset);
    }
}
