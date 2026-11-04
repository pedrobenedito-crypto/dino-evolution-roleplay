package com.dinoevolution.capability;

import com.dinoevolution.DinoEvolutionMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DinoCapabilities {
    public static final Capability<DinoData> DINO_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(DinoData.class);
    }
}
