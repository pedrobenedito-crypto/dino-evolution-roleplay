package com.dinoevolution.network;

import com.dinoevolution.capability.DinoCapabilities;
import com.dinoevolution.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/** Servidor → clientes: nível e dino de um jogador. */
public record SyncDinoPacket(int entityId, int level, String morph) {

    public static void encode(SyncDinoPacket m, FriendlyByteBuf b) {
        b.writeVarInt(m.entityId);
        b.writeVarInt(m.level);
        b.writeUtf(m.morph);
    }

    public static SyncDinoPacket decode(FriendlyByteBuf b) {
        return new SyncDinoPacket(b.readVarInt(), b.readVarInt(), b.readUtf());
    }

    public static void handle(SyncDinoPacket m, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSync(m));
    }

    public static SyncDinoPacket of(ServerPlayer p) {
        return p.getCapability(DinoCapabilities.DINO_DATA)
                .map(d -> new SyncDinoPacket(p.getId(), d.getLevel(), d.getMorph() == null ? "" : d.getMorph().id))
                .orElse(new SyncDinoPacket(p.getId(), 1, ""));
    }

    /** Envia o estado do jogador para ele mesmo e para quem está vendo ele. */
    public static void sync(ServerPlayer p) {
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> p), of(p));
    }
}
