package com.sekai.ambienceblocks.packets;

import com.mojang.datafixers.util.Pair;
import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.StaticUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
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
                TagKey<ConfiguredStructureFeature<?, ?>> nmsType = getStructureType(pkt.structure);

                Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair;

                Optional<HolderSet.Named<ConfiguredStructureFeature<?, ?>>> tag = world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getTag(nmsType);
                //Optional<HolderSet.Named<ConfiguredStructureFeature<?, ?>>> optional = this.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getTag(p_207562_);
                if (!tag.isPresent()) {
                    return;
                } else {
                    pair = world.getChunkSource().getGenerator().findNearestMapFeature(world, tag.get(), player.blockPosition(), 100, false);
                    //return pair != null ? pair.getFirst() : null;
                    //pos = pair.getFirst();
                }



                //BlockPos pos = world.findNearestMapFeature(getStructureType(pkt.structure), player.blockPosition(), 100, false); // (location, radius, findUnexplored)
                boolean isIn = false;

                BoundingBox playerBB = new BoundingBox(player.blockPosition());
                if(pkt.range != 0)
                    playerBB = StaticUtil.growBoundingBox(playerBB, pkt.range);

                if(pair.getFirst() != null) {
                    LevelChunk chunk = world.getChunkAt(pair.getFirst());
                    if (chunk.getStartForFeature(pair.getSecond().value()) != null) { //Checking if this chunk is the starting point of the structure
                        if(pkt.full) {
                            if(chunk.getStartForFeature(pair.getSecond().value()).getBoundingBox().intersects(playerBB))
                                isIn = true;
                        } else {
                            for (StructurePiece piece : chunk.getStartForFeature(pair.getSecond().value()).getPieces()) { //Iterating through every piece of the structure
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

    private static TagKey<ConfiguredStructureFeature<?, ?>> getStructureType(String structure) {
        return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(structure));
    }

    /*private static StructureFeature<?> getStructureType(String structure) {
        return net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structure));
    }*/
}
