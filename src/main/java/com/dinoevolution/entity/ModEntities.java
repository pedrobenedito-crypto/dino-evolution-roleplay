package com.dinoevolution.entity;

import com.dinoevolution.DinoEvolutionMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DinoEvolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DinoEvolutionMod.MOD_ID);

    /** "Corpo" GeckoLib usado só no cliente para desenhar o jogador-dino. Nunca entra no mundo. */
    public static final RegistryObject<EntityType<DinoDummyEntity>> DINO_DUMMY = ENTITY_TYPES.register("dino_dummy",
            () -> EntityType.Builder.<DinoDummyEntity>of(DinoDummyEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .noSave()
                    .noSummon()
                    .build("dino_dummy"));

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(DINO_DUMMY.get(), Mob.createMobAttributes().build());
    }
}
