package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.ClientPacketHandler;
import com.sekai.ambienceblocks.util.PacketHandler;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketIsItInStructure {
    public String source;

    public PacketIsItInStructure(String source) {
        this.source = source;
    }

    public static PacketIsItInStructure decode(PacketBuffer buf) {
        return new PacketIsItInStructure(buf.readString(30));
    }

    public static void encode(PacketIsItInStructure msg, PacketBuffer buf) {
        buf.writeString(msg.source);
    }

    public static void handle(final PacketIsItInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                ServerPlayerEntity player = ctx.get().getSender();
                ServerWorld world = ctx.get().getSender().getServerWorld();
                Structure<?> nmsType = getStructureType(pkt.source);
                BlockPos pos = world.func_241117_a_(getStructureType(pkt.source), player.getPosition(), 100, false); // (location, radius, findUnexplored)
                boolean isIn = false;

                if(pos != null) {
                    Chunk chunk = world.getChunkAt(pos);
                    if (chunk.func_230342_a_(nmsType) != null) { //Checking if this chunk is the starting point of the structure
                        for (StructurePiece piece : chunk.func_230342_a_(nmsType).getComponents()) { //Iterating through every piece of the structure
                            if (piece.getBoundingBox().isVecInside(player.getPosition())) { //Getting the piece's bounding box and then checking if the player is inside
                                isIn = true;
                                 //If all this is true, then the player is standing inside the structure
                            }
                        }
                    }
                }

                if(isIn) {
                    PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.source, true));
                } else {
                    PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.source, false));
                }

                /*for (StructurePiece piece : ctx.get().getSender().getServerWorld().func_241112_a_().getStructureStart().getComponents()) {
                    piece.getBoundingBox().isVecInside(ctx.get().getSender().getPosition());
                }*/

                //Used to protect from accessing Minecraft class
                //DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketIsInStructure(pkt));

                //do server stuff
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static Structure<?> getStructureType(String structure) {
        return net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structure));
        /*for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
            return
            String name = structureFeature.getRegistryName().toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> func_241053_a_(ctx.getSource(), structureFeature)));
        }*/
    }
}
