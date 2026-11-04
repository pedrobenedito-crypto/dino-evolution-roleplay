package com.dinoevolution.entity;

import com.dinoevolution.morph.DinoMorph;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DinoDummyEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Player owner;
    private DinoMorph morph = DinoMorph.RAPTOR_PINK;

    public DinoDummyEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public void setup(Player owner, DinoMorph morph) {
        this.owner = owner;
        this.morph = morph;
    }

    public DinoMorph getMorph() {
        return morph;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (owner != null) {
                if (owner.isSwimming() || owner.isInWater()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("swim"));
                }
                if (owner.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop(owner.isSprinting() ? "run" : "walk"));
                }
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
