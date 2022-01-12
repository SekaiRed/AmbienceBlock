package com.sekai.ambienceblocks.client.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sekai.ambienceblocks.Main;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalDouble;

//Might be obligatory to have my outlines appear even with optifine, pain tako
//Thank you so much TheGreyGhost for MinecraftByExample, it's a godsend
public class RenderTypeHelper extends RenderStateShard {
    private static ShaderInstance rendertypeLinesNoDepthShader;

    @Nullable
    public static ShaderInstance getRendertypeLinesNoDepthShader() {
        return rendertypeLinesNoDepthShader;
    }
    //public static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY;

    //public static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING;

    //public static final RenderStateShard.OutputStateShard ITEM_ENTITY_TARGET;

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_LINES_NO_DEPTH_SHADER = new RenderStateShard.ShaderStateShard(RenderTypeHelper::getRendertypeLinesNoDepthShader);

    public static final RenderType LINE_NO_DEPTH_TEST;

    static {
        //NO_TRANSPARENCY = ObfuscationReflectionHelper.getPrivateValue(RenderStateShard.class, null, "f_110134_");

        //VIEW_OFFSET_Z_LAYERING = ObfuscationReflectionHelper.getPrivateValue(RenderStateShard.class, null, "f_110119_");

        //ITEM_ENTITY_TARGET = ObfuscationReflectionHelper.getPrivateValue(RenderStateShard.class, null, "f_110129_");

        /*final boolean ENABLE_DEPTH_WRITING = true;
        final boolean ENABLE_COLOUR_COMPONENTS_WRITING = true;
        final RenderStateShard.WriteMaskStateShard WRITE_TO_DEPTH_AND_COLOR
                = new RenderStateShard.WriteMaskStateShard(ENABLE_DEPTH_WRITING, ENABLE_COLOUR_COMPONENTS_WRITING);

        final RenderStateShard.DepthTestStateShard NO_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS);*/

        final int INITIAL_BUFFER_SIZE = 256;
        final boolean AFFECTS_OUTLINE = false;
        RenderType.CompositeState renderState;
        renderState = RenderType.CompositeState.builder()
                //.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
                .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                //.setShaderState(RENDERTYPE_LINES_SHADER) //???
                .setShaderState(RENDERTYPE_LINES_NO_DEPTH_SHADER)
                .setCullState(NO_CULL)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(NO_TRANSPARENCY)
                //.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(ITEM_ENTITY_TARGET)
                //.setWriteMaskState(WRITE_TO_DEPTH_AND_COLOR)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                //.setDepthTestState(NO_DEPTH_TEST)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .createCompositeState(AFFECTS_OUTLINE);
        LINE_NO_DEPTH_TEST = RenderType.create(Main.MODID + ":line",
                DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, INITIAL_BUFFER_SIZE, false, false, renderState);
    }

    public static final MultiBufferSource.BufferSource LINE_BUFFERS = MultiBufferSource.immediateWithBuffers(Util.make(() -> {
        Map<RenderType, BufferBuilder> ret = new IdentityHashMap<>();
        ret.put(RenderTypeHelper.LINE_NO_DEPTH_TEST, new BufferBuilder(RenderTypeHelper.LINE_NO_DEPTH_TEST.bufferSize()));
        return ret;
    }), Tesselator.getInstance().getBuilder());

    //Ok this is definitely not the encouraged way to do this but am I really gonna whip out reflection when that works???
    public RenderTypeHelper(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        super(p_110161_, p_110162_, p_110163_);
    }

    @SubscribeEvent
    public static void registerShadersEvent(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Main.MODID, "rendertype_lines_nodepth"), DefaultVertexFormat.POSITION_COLOR_NORMAL), (lol) -> {
                rendertypeLinesNoDepthShader = lol;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
