package com.sekai.ambienceblocks.packets;

//import com.sekai.ambienceblocks.client.gui.AmbienceTileGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenAmbienceGui {
    public BlockPos pos;

    public PacketOpenAmbienceGui(BlockPos pos) {
        this.pos = pos;
    }

    public static PacketOpenAmbienceGui decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        PacketOpenAmbienceGui data = new PacketOpenAmbienceGui(pos);
        return data;
    }

    public static void encode(PacketOpenAmbienceGui msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static void handle(final PacketOpenAmbienceGui pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //if (!ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT))
            //    return;

            Minecraft mc = Minecraft.getInstance();

            if(mc.world == null)
                return;

            if(mc.currentScreen != null)
                return;

            if(!mc.world.isAreaLoaded(pkt.pos, 1))
                return;

            TileEntity tile = mc.world.getTileEntity(pkt.pos);

            if(tile == null) return;
            if(!AmbienceTileEntity.class.isInstance(tile)) return;

            //DistExecutor.runWhenOn(Dist.CLIENT, () -> Minecraft.getInstance()::displayGuiScreen(new AmbienceTileGUI((AmbienceTileEntity)tile));

            //mc.displayGuiScreen(new AmbienceTileGUI((AmbienceTileEntity)tile));
        });
        ctx.get().setPacketHandled(true);
    }
}
