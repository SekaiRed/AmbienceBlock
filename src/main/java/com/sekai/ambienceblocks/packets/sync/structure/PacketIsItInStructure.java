package com.sekai.ambienceblocks.packets.sync.structure;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketIsItInStructure implements IMessage {
    public String structure;
    public double range;
    public boolean full;

    public PacketIsItInStructure() {}

    public PacketIsItInStructure(String structure, double range, boolean full) {
        this.structure = structure;
        this.range = range;
        this.full = full;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        structure = pkt.readString(30);
        range = pkt.readDouble();
        full = pkt.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        pkt.writeString(structure);
        pkt.writeDouble(range);
        pkt.writeBoolean(full);
    }

    /*public static PacketIsItInStructure decode(PacketBuffer buf) {
        String structure = buf.readString(30);
        double range = buf.readDouble();
        boolean full = buf.readBoolean();
        return new PacketIsItInStructure(structure, range, full);
    }

    public static void encode(PacketIsItInStructure msg, PacketBuffer buf) {
        buf.writeString(msg.structure);
        buf.writeDouble(msg.range);
        buf.writeBoolean(msg.full);
    }

    public static void handle(final PacketIsItInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                ServerPlayerEntity player = ctx.get().getSender();
                ServerWorld world = ctx.get().getSender().getServerWorld();
                Structure<?> nmsType = getStructureType(pkt.structure);
                BlockPos pos = world.func_241117_a_(getStructureType(pkt.structure), player.getPosition(), 100, false); // (location, radius, findUnexplored)
                boolean isIn = false;

                MutableBoundingBox playerBB = new MutableBoundingBox(player.getPosition(), player.getPosition().add(1, 1, 1));
                if(pkt.range != 0)
                    playerBB = StaticUtil.growBoundingBox(playerBB, pkt.range);

                if(pos != null) {
                    Chunk chunk = world.getChunkAt(pos);
                    if (chunk.func_230342_a_(nmsType) != null) { //Checking if this chunk is the starting point of the structure
                        if(pkt.full) {
                            if(chunk.func_230342_a_(nmsType).getBoundingBox().intersectsWith(playerBB))
                                isIn = true;
                        } else {
                            for (StructurePiece piece : chunk.func_230342_a_(nmsType).getComponents()) { //Iterating through every piece of the structure
                                if (piece.getBoundingBox().intersectsWith(playerBB)) { //Getting the piece's bounding box and then checking if the player is inside
                                    isIn = true;
                                    //If all this is true, then the player is standing inside the structure
                                }
                            }
                        }
                    }
                }

                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, isIn));

                //Used to protect from accessing Minecraft class
                //DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.handlePacketIsInStructure(pkt));

                //do server stuff
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static Structure<?> getStructureType(String structure) {
        return net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structure));
    }*/
}
