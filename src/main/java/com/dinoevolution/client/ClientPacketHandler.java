package com.dinoevolution.client;

import com.dinoevolution.capability.DinoCapabilities;
import com.dinoevolution.morph.DinoMorph;
import com.dinoevolution.network.SyncDinoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandler {
    public static void handleSync(SyncDinoPacket m) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        if (level.getEntity(m.entityId()) instanceof Player p) {
            p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
                d.setLevel(m.level());
                d.setMorph(DinoMorph.byId(m.morph()));
            });
            p.refreshDimensions();
        }
    }
}
