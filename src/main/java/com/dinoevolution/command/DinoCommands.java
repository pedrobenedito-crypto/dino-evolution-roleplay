package com.dinoevolution.command;

import com.dinoevolution.DinoEvolutionMod;
import com.dinoevolution.capability.DinoCapabilities;
import com.dinoevolution.event.CommonEvents;
import com.dinoevolution.morph.DinoMorph;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

/** Comandos de teste enquanto a tela de escolha dos dinos não existe. Precisa de cheats ligados. */
@Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID)
public class DinoCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("dino")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("morph")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(DinoMorph.values()).map(m -> m.id), builder))
                                        .executes(ctx -> setMorph(ctx.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(ctx, "id")))))
                        .then(Commands.literal("level")
                                .then(Commands.argument("n", IntegerArgumentType.integer(1, 5))
                                        .executes(ctx -> setLevel(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "n")))))
                        .then(Commands.literal("reset")
                                .executes(ctx -> reset(ctx.getSource().getPlayerOrException()))));
    }

    private static int setMorph(ServerPlayer p, String id) {
        DinoMorph m = DinoMorph.byId(id);
        if (m == null) {
            p.sendSystemMessage(Component.literal("Dino desconhecido: " + id));
            return 0;
        }
        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> d.setMorph(m));
        CommonEvents.refresh(p);
        return 1;
    }

    private static int setLevel(ServerPlayer p, int level) {
        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> d.setLevel(level));
        CommonEvents.refresh(p);
        return 1;
    }

    private static int reset(ServerPlayer p) {
        p.getCapability(DinoCapabilities.DINO_DATA).ifPresent(d -> {
            d.setMorph(null);
            d.setLevel(1);
        });
        CommonEvents.refresh(p);
        return 1;
    }
}
