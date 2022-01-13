package com.sekai.ambienceblocks.client.rendering;

import com.sekai.ambienceblocks.Main;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalDouble;

//Might be obligatory to have my outlines appear even with optifine, pain tako
//Thank you so much TheGreyGhost for MinecraftByExample, it's a godsend
public class RenderTypeHelper {
    public static final RenderState.TransparencyState NO_TRANSPARENCY;
    public static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY;

    public static final RenderState.LayerState VIEW_OFFSET_Z_LAYERING;

    public static final RenderState.TargetState ITEM_ENTITY_TARGET;

    public static final RenderType LINE_NO_DEPTH_TEST;

    public static final RenderType BOUNDS;

    static {
        NO_TRANSPARENCY = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228510_b_");
        ADDITIVE_TRANSPARENCY = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228511_c_");
        //field_228511_c_

//    PROJECTION_LAYERING = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228500_J_");
        VIEW_OFFSET_Z_LAYERING = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_239235_M_");

        ITEM_ENTITY_TARGET = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_241712_U_");

        final boolean ENABLE_DEPTH_WRITING = true;
        final boolean ENABLE_COLOUR_COMPONENTS_WRITING = true;
        final RenderState.WriteMaskState WRITE_TO_DEPTH_AND_COLOR
                = new RenderState.WriteMaskState(ENABLE_DEPTH_WRITING, ENABLE_COLOUR_COMPONENTS_WRITING);
        final RenderState.WriteMaskState COLOR_WRITE
                = new RenderState.WriteMaskState(true, false);

        final RenderState.DepthTestState NO_DEPTH_TEST = new RenderState.DepthTestState("always",GL11.GL_ALWAYS);

        final int INITIAL_BUFFER_SIZE = 128;
        final boolean AFFECTS_OUTLINE = false;
        RenderType.State renderState;
        renderState = RenderType.State.getBuilder()
                .line(new RenderState.LineState(OptionalDouble.of(2)))
                .layer(VIEW_OFFSET_Z_LAYERING)
                .transparency(NO_TRANSPARENCY)
                .target(ITEM_ENTITY_TARGET)
                .writeMask(WRITE_TO_DEPTH_AND_COLOR)
                .depthTest(NO_DEPTH_TEST)
                .build(AFFECTS_OUTLINE);
        LINE_NO_DEPTH_TEST = RenderType.makeType(Main.MODID + ":line",
                DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, INITIAL_BUFFER_SIZE, renderState);

        RenderType.State renderState1;
        renderState1 = RenderType.State.getBuilder()
                .line(new RenderState.LineState(OptionalDouble.of(2)))
                .transparency(ADDITIVE_TRANSPARENCY)
                .target(ITEM_ENTITY_TARGET)
                .writeMask(COLOR_WRITE)
                .depthTest(NO_DEPTH_TEST)
                .build(AFFECTS_OUTLINE);
        BOUNDS = RenderType.makeType(Main.MODID + ":bounds",
                DefaultVertexFormats.POSITION_COLOR, 7, INITIAL_BUFFER_SIZE, renderState1);
    }

    public static final IRenderTypeBuffer.Impl LINE_BUFFERS = IRenderTypeBuffer.getImpl(Util.make(() -> {
        Map<RenderType, BufferBuilder> ret = new IdentityHashMap<>();
        ret.put(RenderTypeHelper.LINE_NO_DEPTH_TEST, new BufferBuilder(RenderTypeHelper.LINE_NO_DEPTH_TEST.getBufferSize()));
        return ret;
    }), Tessellator.getInstance().getBuffer());

    public static final IRenderTypeBuffer.Impl BOUNDS_BUFFERS = IRenderTypeBuffer.getImpl(Util.make(() -> {
        Map<RenderType, BufferBuilder> ret = new IdentityHashMap<>();
        ret.put(RenderTypeHelper.BOUNDS, new BufferBuilder(RenderTypeHelper.BOUNDS.getBufferSize()));
        return ret;
    }), Tessellator.getInstance().getBuffer());
}
