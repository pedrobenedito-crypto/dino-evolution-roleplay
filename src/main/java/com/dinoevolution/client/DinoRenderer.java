package com.dinoevolution.client;

import com.dinoevolution.entity.DinoDummyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DinoRenderer extends GeoEntityRenderer<DinoDummyEntity> {
    public DinoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DinoModel());
        this.shadowRadius = 0.6f;
    }
}
