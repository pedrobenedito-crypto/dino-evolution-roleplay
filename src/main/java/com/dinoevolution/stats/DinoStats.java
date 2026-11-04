package com.dinoevolution.stats;

import com.dinoevolution.capability.DinoData;
import com.dinoevolution.morph.DinoMorph;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class DinoStats {
    // índice 0 = nível 1 ... índice 4 = nível 5
    public static final float[] SCALE = {0.5f, 0.8f, 1.2f, 1.8f, 2.6f};
    private static final double[] SPEED = {-0.35, -0.20, 0.0, 0.15, 0.30}; // multiplicador
    private static final double[] DAMAGE = {-0.5, 0.5, 2.0, 5.0, 9.0};      // soma
    private static final double[] HEALTH = {-10.0, -6.0, 0.0, 6.0, 14.0};   // soma

    private static final UUID SPEED_ID = UUID.fromString("a3d6b5c0-1f2e-4c7a-9b11-5d0e0a7f0001");
    private static final UUID DAMAGE_ID = UUID.fromString("a3d6b5c0-1f2e-4c7a-9b11-5d0e0a7f0002");
    private static final UUID HEALTH_ID = UUID.fromString("a3d6b5c0-1f2e-4c7a-9b11-5d0e0a7f0003");
    private static final UUID REACH_ID = UUID.fromString("a3d6b5c0-1f2e-4c7a-9b11-5d0e0a7f0004");

    public static float scale(int level) {
        return SCALE[level - 1];
    }

    /** Aplica velocidade, dano, vida, alcance e hitbox do dino atual. Chamar só no servidor. */
    public static void apply(Player p, DinoData d) {
        DinoMorph m = d.getMorph();
        int i = d.getLevel() - 1;

        if (m == null) {
            set(p, Attributes.MOVEMENT_SPEED, SPEED_ID, 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
            set(p, Attributes.ATTACK_DAMAGE, DAMAGE_ID, 0, AttributeModifier.Operation.ADDITION);
            set(p, Attributes.MAX_HEALTH, HEALTH_ID, 0, AttributeModifier.Operation.ADDITION);
            set(p, ForgeMod.ENTITY_REACH.get(), REACH_ID, 0, AttributeModifier.Operation.ADDITION);
        } else {
            set(p, Attributes.MOVEMENT_SPEED, SPEED_ID, SPEED[i] + m.speed, AttributeModifier.Operation.MULTIPLY_TOTAL);
            set(p, Attributes.ATTACK_DAMAGE, DAMAGE_ID, DAMAGE[i] + m.damage, AttributeModifier.Operation.ADDITION);
            set(p, Attributes.MAX_HEALTH, HEALTH_ID, HEALTH[i] + m.health, AttributeModifier.Operation.ADDITION);
            set(p, ForgeMod.ENTITY_REACH.get(), REACH_ID, m.reach, AttributeModifier.Operation.ADDITION);
        }

        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
        p.refreshDimensions(); // dispara EntityEvent.Size (hitbox)
    }

    private static void set(Player p, Attribute attr, UUID id, double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (amount != 0) {
            inst.addTransientModifier(new AttributeModifier(id, "dino_stats", amount, op));
        }
    }
}
