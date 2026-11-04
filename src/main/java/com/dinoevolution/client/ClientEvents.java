package com.dinoevolution.client;

import com.dinoevolution.DinoEvolutionMod;
import com.dinoevolution.capability.DinoCapabilities;
import com.dinoevolution.entity.DinoDummyEntity;
import com.dinoevolution.entity.ModEntities;
import com.dinoevolution.network.DashPacket;
import com.dinoevolution.network.ModNetwork;
import com.dinoevolution.stats.DinoStats;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEvents {

    public static final KeyMapping DASH_KEY = new KeyMapping(
            "key.dinoevolution.dash", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.dinoevolution");

    /** Eventos do barramento do mod: registros. */
    @Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(DASH_KEY);
        }

        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerEntityRenderer(ModEntities.DINO_DUMMY.get(), DinoRenderer::new);
        }
    }

    /** Eventos do jogo: tecla de Dash e troca do modelo do jogador. */
    @Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID, value = Dist.CLIENT)
    public static class ForgeBus {
        // um "corpo" GeckoLib por jogador, para as animações não se misturarem
        private static final Map<UUID, DinoDummyEntity> DUMMIES = new HashMap<>();

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            while (DASH_KEY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new DashPacket());
            }
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            DUMMIES.clear();
        }

        /** Esconde o braço do Steve na primeira pessoa quando o jogador é um dino. */
        @SubscribeEvent
        public static void hand(RenderHandEvent e) {
            Player p = Minecraft.getInstance().player;
            if (p == null) return;
            p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
                if (d.getMorph() != null) e.setCanceled(true);
            });
        }

        @SubscribeEvent
        @SuppressWarnings("unchecked")
        public static void renderPlayer(RenderPlayerEvent.Pre e) {
            Player p = e.getEntity();
            p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
                if (d.getMorph() == null) return;   // Steve normal
                e.setCanceled(true);                 // não desenha o Steve

                DinoDummyEntity dummy = DUMMIES.get(p.getUUID());
                if (dummy == null || dummy.level() != p.level()) {
                    dummy = new DinoDummyEntity(ModEntities.DINO_DUMMY.get(), p.level());
                    DUMMIES.put(p.getUUID(), dummy);
                }
                dummy.setup(p, d.getMorph());

                // copia o estado do jogador para o corpo GeckoLib
                dummy.tickCount = p.tickCount;
                dummy.yBodyRot = p.yBodyRot;
                dummy.yBodyRotO = p.yBodyRotO;
                dummy.yHeadRot = p.yHeadRot;
                dummy.yHeadRotO = p.yHeadRotO;
                dummy.setYRot(p.getYRot());
                dummy.yRotO = p.yRotO;
                dummy.setXRot(p.getXRot());
                dummy.xRotO = p.xRotO;

                float s = DinoStats.scale(d.getLevel());
                PoseStack pose = e.getPoseStack();
                pose.pushPose();
                pose.scale(s, s, s); // escala visual do nível
                EntityRenderer<DinoDummyEntity> renderer = (EntityRenderer<DinoDummyEntity>)
                        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dummy);
                renderer.render(dummy, p.getYRot(), e.getPartialTick(), pose,
                        e.getMultiBufferSource(), e.getPackedLight());
                pose.popPose();
            });
        }
    }
}
