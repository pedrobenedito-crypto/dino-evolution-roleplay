package com.dinoevolution.network;

import com.dinoevolution.capability.DinoCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Cliente → servidor: "apertei a tecla de Dash". O servidor decide se pode. */
public class DashPacket {

    public static void handle(DashPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer p = ctx.get().getSender();
        if (p == null) return;

        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
            if (d.getMorph() == null) return;                    // precisa estar transformado
            if (d.dashCooldown > 0 || d.dashTicks > 0) return;   // em recarga
            if (p.getFoodData().getFoodLevel() <= 6) return;     // custo em fome ("stamina")

            p.causeFoodExhaustion(3.0f);
            d.dashTicks = 8;      // 0,4 s de investida
            d.dashCooldown = 60;  // 3 s de recarga
        });
    }
}
