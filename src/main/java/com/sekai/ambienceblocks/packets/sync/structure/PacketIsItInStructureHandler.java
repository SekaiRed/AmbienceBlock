package com.sekai.ambienceblocks.packets.sync.structure;

import com.sekai.ambienceblocks.util.PacketHandler;
import com.sekai.ambienceblocks.util.StaticUtil;
import com.sekai.ambienceblocks.util.StructureUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketIsItInStructureHandler  implements IMessageHandler<PacketIsItInStructure, IMessage> {
    @Override
    public IMessage onMessage(final PacketIsItInStructure msg, final MessageContext ctx)
    {
        if(ctx.side != Side.SERVER)
            return null;

        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = (WorldServer) ctx.getServerHandler().player.world;
        BlockPos nearestStructure = world.findNearestStructure(msg.structure, player.getPosition(), false);
        boolean isIn = world.getChunkProvider().isInsideStructure(world, msg.structure, player.getPosition());

        StructureBoundingBox playerBB = new StructureBoundingBox(player.getPosition(), player.getPosition().add(1, 1, 1));
        if(msg.range != 0)
            playerBB = StaticUtil.growBoundingBox(playerBB, msg.range);

        if(nearestStructure != null) {
            StructureStart struct = StructureUtil.getStructureStart(world, nearestStructure, msg.structure);
            if (struct != null) { //Checking if this chunk is the starting point of the structure
                if(msg.full) {
                    if(struct.getBoundingBox().intersectsWith(playerBB))
                        isIn = true;
                } else {
                    for (StructureComponent piece : struct.getComponents()) { //Iterating through every piece of the structure
                        if (piece.getBoundingBox().intersectsWith(playerBB)) { //Getting the piece's bounding box and then checking if the player is inside
                            isIn = true;
                            //If all this is true, then the player is standing inside the structure
                        }
                    }
                }
            }
        }

        PacketHandler.NETWORK.sendTo(new PacketItIsInStructure(msg.structure, msg.range, msg.full, isIn),player);

        /*PacketHandler.NETWORK.sendTo(new PacketItIsInStructure(msg.structure, msg.range, msg.full, isIn),player);

        BlockPos nearestStructure = world.findNearestStructure(msg.structure, player.getPosition(), false);

        StructureStart struct = StructureUtil.getStructureStart(world, nearestStructure, msg.structure);
        if(struct != null)
            System.out.println(struct.getBoundingBox());*/

        /*EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = (WorldServer) ctx.getServerHandler().player.world;
        BlockPos nearestStructure = world.findNearestStructure(msg.structure, player.getPosition(), false);
        boolean isIn = false;

        if(nearestStructure != null) {
            Chunk chunk = world.getChunkFromBlockCoords(nearestStructure);
            world.getChunkProvider().isInsideStructure(world, msg.structure, player.getPosition());
        }*/

        /*ServerPlayerEntity player = ctx.get().getSender();
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

        PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, isIn));*/


        /*if(msg.result)
            AmbienceController.instance.structure.playerIsInStructure(msg.structure, msg.range, msg.full);
        else
            AmbienceController.instance.structure.playerIsntInStructure(msg.structure, msg.range, msg.full);*/

        return null;
    }
}