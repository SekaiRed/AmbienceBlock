package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.client.ambiencecontroller.AmbienceController;
import com.sekai.ambienceblocks.packets.PacketTargeting;
import com.sekai.ambienceblocks.packets.PacketUpdateAmbienceTE;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.DistExecutor;

public class ClientPacketHandler {
    public static DistExecutor.SafeRunnable handlePacketTargeting(final PacketTargeting pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.world == null)
                    return;

                //I know this is overkill but I'm scared
                if (!mc.world.isRemote())
                    return;

                Entity source = mc.world.getEntityByID(pkt.source);

                if (source == null)
                    return;

                if(source instanceof MobEntity) {
                    MobEntity mob = (MobEntity) source;

                    if(!pkt.removeTarget)
                        mob.setAttackTarget(mc.player);
                    else
                        mob.setAttackTarget(null);
                }
            }
        };
    }

    public static DistExecutor.SafeRunnable handlePacketAmbienceTE(final PacketUpdateAmbienceTE pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.world == null)
                    return;

                if (!mc.world.isRemote())
                    return;

                if (!mc.world.isBlockPresent(pkt.pos))
                    return;

                TileEntity tile = mc.world.getTileEntity(pkt.pos);

                if (tile == null)
                    return;

                if (!(tile instanceof AmbienceTileEntity))
                    return;

                AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                finalTile.data = pkt.data;
                AmbienceController.instance.stopFromTile(finalTile);
            }
        };
    }
}
