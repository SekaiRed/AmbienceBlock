package com.sekai.ambienceblocks.packets.ambiencedata;

import com.sekai.ambienceblocks.client.ambience.AmbienceController;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.util.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAmbienceData implements IMessage {
    public BlockPos pos;
    public AmbienceData data;

    //needed
    public PacketAmbienceData() {}

    public PacketAmbienceData(BlockPos pos, AmbienceData data) {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        pos = pkt.readBlockPos();
        data = new AmbienceData();
        data.readBuff(pkt);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pkt = new PacketBuffer(buf);
        pkt.writeBlockPos(pos);
        data.writeBuff(pkt);
        /*pos = buf.read
        data.writeBuff(buf);*/
    }

    public static class Handler implements IMessageHandler<PacketAmbienceData, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketAmbienceData msg, final MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT) {
                IThreadListener mainThread = Minecraft.getMinecraft();
                mainThread.addScheduledTask(() -> {
                    Minecraft mc = Minecraft.getMinecraft();

                    if (mc.world == null)
                        return;

                    if (!mc.world.isBlockLoaded(msg.pos))
                        return;

                    TileEntity tile = mc.world.getTileEntity(msg.pos);

                    if (tile == null)
                        return;

                    if (!(tile instanceof AmbienceTileEntity))
                        return;

                    AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                    finalTile.data = msg.data;
                    AmbienceController.instance.stopFromTile(finalTile);
                });
            }

            if(ctx.side == Side.SERVER) {
                // This is the player the packet was sent to the server from
                WorldServer serverWorld = ctx.getServerHandler().player.getServerWorld();
                serverWorld.addScheduledTask(() -> {
                    //serverPlayer.inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND, amount));
                    if(serverWorld == null)
                        return;

                    if(serverWorld.isRemote)
                        return;

                    if(!serverWorld.isBlockLoaded(msg.pos))
                        return;

                    TileEntity tile = serverWorld.getTileEntity(msg.pos);

                    if (tile == null)
                        return;

                    if (!(tile instanceof AmbienceTileEntity))
                        return;

                    AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                    finalTile.data = msg.data;

                    //sends an update back to update all clients within range
                    NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(serverWorld.provider.getDimension(), msg.pos.getX() + 0.5D, msg.pos.getY() + 0.5D, msg.pos.getZ() + 0.5D, 512);
                    PacketHandler.NETWORK.sendToAllAround(new PacketAmbienceData(msg.pos, msg.data), point);
                    //PacketHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ctx.get().getSender()), new PacketUpdateAmbienceTE(finalTile.getPos(), finalTile.data));
                });
            }
            /*IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {

                /*EntityPlayer player = Minecraft.getMinecraft().player;
                Stats stats = (Stats) player.getCapability(StatsProvider.STAT_CAP, null);

                if (stats != null)
                {
                    stats.setMaxHP(message.maxHP);
                    stats.setMaxSP(message.maxSP);
                    stats.setHP(message.HP);
                    stats.setSP(message.SP);

                    stats.setLevel(message.level);

                    for(int i = 0; i < StatsType.values().length; i++)
                        stats.setStat(StatsType.values()[i], message.statArray[i]);

                    for(int i = 0; i < message.skillsCount; i++)
                        stats.addSkill(message.skills.get(i));
                }
            });*/

            //no return packet
            return null;
        }
    }
}
