package com.dinoevolution.event;

import com.dinoevolution.DinoEvolutionMod;
import com.dinoevolution.capability.DinoCapabilities;
import com.dinoevolution.capability.DinoData;
import com.dinoevolution.capability.DinoDataProvider;
import com.dinoevolution.network.ModNetwork;
import com.dinoevolution.network.SyncDinoPacket;
import com.dinoevolution.stats.DinoStats;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID)
public class CommonEvents {

    // ---------- Capability ----------

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player) {
            e.addCapability(new ResourceLocation(DinoEvolutionMod.MOD_ID, "dino_data"), new DinoDataProvider());
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone e) {
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(DinoCapabilities.DINO_DATA).ifPresent(old ->
                e.getEntity().getCapability(DinoCapabilities.DINO_DATA).ifPresent(nw -> nw.copyFrom(old)));
        e.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) { refresh(e.getEntity()); }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent e) { refresh(e.getEntity()); }

    @SubscribeEvent
    public static void changeDim(PlayerEvent.PlayerChangedDimensionEvent e) { refresh(e.getEntity()); }

    /** Quando alguém passa a ver outro jogador, manda o estado dele para quem está vendo. */
    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking e) {
        if (e.getTarget() instanceof ServerPlayer target && e.getEntity() instanceof ServerPlayer viewer) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), SyncDinoPacket.of(target));
        }
    }

    /** Reaplica stats (modificadores transient não são salvos) e sincroniza com os clientes. */
    public static void refresh(Player p) {
        if (p instanceof ServerPlayer sp) {
            sp.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> DinoStats.apply(sp, d));
            SyncDinoPacket.sync(sp);
        }
    }

    // ---------- Hitbox ----------

    /** Roda no cliente e no servidor sempre que refreshDimensions() é chamado. */
    @SubscribeEvent
    public static void size(EntityEvent.Size e) {
        if (e.getEntity() instanceof Player p) {
            p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
                if (d.getMorph() == null) return;
                float s = DinoStats.scale(d.getLevel());
                if (e.getPose() == Pose.STANDING) {
                    e.setNewSize(EntityDimensions.scalable(d.getMorph().width * s, d.getMorph().height * s), true);
                } else {
                    e.setNewSize(e.getNewSize().scale(s), true); // agachado, nadando etc.
                }
            });
        }
    }

    // ---------- Evolução ----------

    /** Chame quando o jogador concluir um desafio. */
    public static void levelUp(ServerPlayer p) {
        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
            if (d.getLevel() >= DinoData.MAX_LEVEL) return;
            d.setLevel(d.getLevel() + 1);
            DinoStats.apply(p, d);
            SyncDinoPacket.sync(p);
        });
    }

    // ---------- Dash (servidor) ----------

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;

        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
            if (d.dashCooldown > 0) d.dashCooldown--;
            if (d.dashTicks <= 0) return;
            d.dashTicks--;

            Vec3 dir = p.getLookAngle().multiply(1, 0, 1).normalize();
            double power = 0.9 + d.getLevel() * 0.15;
            p.setDeltaMovement(dir.x * power, p.getDeltaMovement().y, dir.z * power);
            p.hurtMarked = true; // força o envio da velocidade ao cliente

            AABB box = p.getBoundingBox().inflate(0.6).move(dir.scale(0.8));

            // dano de impacto
            float dmg = 2.0f + d.getLevel() * 1.5f;
            for (LivingEntity target : p.level().getEntitiesOfClass(LivingEntity.class, box, t -> t != p)) {
                if (target.hurt(p.damageSources().playerAttack(p), dmg)) {
                    target.knockback(1.0, -dir.x, -dir.z);
                }
            }

            // quebra blocos fracos (folhas, terra, areia, lã...)
            if (p.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
                BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
                for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                    BlockPos immutable = pos.immutable();
                    BlockState st = p.level().getBlockState(immutable);
                    float hardness = st.getDestroySpeed(p.level(), immutable);
                    if (!st.isAir() && hardness >= 0 && hardness <= 0.6f) {
                        p.level().destroyBlock(immutable, true, p);
                    }
                }
            }
        });
    }
}
