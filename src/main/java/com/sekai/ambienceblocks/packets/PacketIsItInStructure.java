package com.sekai.ambienceblocks.packets;

import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketIsItInStructure {
    public String structure;
    public double range;
    public boolean full;

    public PacketIsItInStructure(String structure, double range, boolean full) {
        this.structure = structure;
        this.range = range;
        this.full = full;
    }

    public static PacketIsItInStructure decode(FriendlyByteBuf buf) {
        String structure = buf.readUtf(30);
        double range = buf.readDouble();
        boolean full = buf.readBoolean();
        return new PacketIsItInStructure(structure, range, full);
    }

    public static void encode(PacketIsItInStructure msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.structure);
        buf.writeDouble(msg.range);
        buf.writeBoolean(msg.full);
    }

    public static void handle(final PacketIsItInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                ServerPlayer player = ctx.get().getSender();
                ServerLevel world = ctx.get().getSender().getLevel();
                StructureFeature<?> nmsType = getStructureType(pkt.structure);
                BlockPos pos = world.findNearestMapFeature(getStructureType(pkt.structure), player.blockPosition(), 100, false); // (location, radius, findUnexplored)
                boolean isIn = false;

                BoundingBox playerBB = new BoundingBox(player.blockPosition());
                if(pkt.range != 0)
                    playerBB = StaticUtil.growBoundingBox(playerBB, pkt.range);

                if(pos != null) {
                    LevelChunk chunk = world.getChunkAt(pos);
                    if (chunk.getStartForFeature(nmsType) != null) { //Checking if this chunk is the starting point of the structure
                        if(pkt.full) {
                            if(chunk.getStartForFeature(nmsType).getBoundingBox().intersects(playerBB))
                                isIn = true;
                        } else {
                            for (StructurePiece piece : chunk.getStartForFeature(nmsType).getPieces()) { //Iterating through every piece of the structure
                                if (piece.getBoundingBox().intersects(playerBB)) { //Getting the piece's bounding box and then checking if the player is inside
                                    isIn = true;
                                    //If all this is true, then the player is standing inside the structure
                                }
                            }
                        }
                    }
                }

                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, isIn));

                /*if(isIn) {
                    PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, true));
                } else {
                    PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, false));
                }*/

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

    private static StructureFeature<?> getStructureType(String structure) {
        return net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structure));
        /*for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
            return
            String name = structureFeature.getRegistryName().toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> func_241053_a_(ctx.getSource(), structureFeature)));
        }*/
    }
}
