package com.dinoevolution.network;

import com.dinoevolution.DinoEvolutionMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DinoEvolutionMod.MOD_ID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        int id = 0;

        CHANNEL.messageBuilder(DashPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder((msg, buf) -> {})
                .decoder(buf -> new DashPacket())
                .consumerMainThread(DashPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncDinoPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncDinoPacket::encode)
                .decoder(SyncDinoPacket::decode)
                .consumerMainThread(SyncDinoPacket::handle)
                .add();
    }
}
