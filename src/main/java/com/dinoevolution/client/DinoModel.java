package com.dinoevolution.client;

import com.dinoevolution.DinoEvolutionMod;
import com.dinoevolution.entity.DinoDummyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DinoModel extends GeoModel<DinoDummyEntity> {
    @Override
    public ResourceLocation getModelResource(DinoDummyEntity e) {
        return new ResourceLocation(DinoEvolutionMod.MOD_ID, "geo/" + e.getMorph().id + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DinoDummyEntity e) {
        return new ResourceLocation(DinoEvolutionMod.MOD_ID, "textures/entity/" + e.getMorph().id + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(DinoDummyEntity e) {
        return new ResourceLocation(DinoEvolutionMod.MOD_ID, "animations/" + e.getMorph().id + ".animation.json");
    }
}
