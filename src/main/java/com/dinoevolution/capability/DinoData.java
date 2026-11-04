package com.dinoevolution.capability;

import com.dinoevolution.morph.DinoMorph;
import net.minecraft.nbt.CompoundTag;

public class DinoData {
    public static final int MIN_LEVEL = 1, MAX_LEVEL = 5;

    private int level = MIN_LEVEL;
    private DinoMorph morph = null; // null = jogador normal (Steve)

    // estado de runtime (não é salvo no NBT)
    public int dashTicks = 0;
    public int dashCooldown = 0;

    public int getLevel() { return level; }

    public void setLevel(int l) { level = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, l)); }

    public DinoMorph getMorph() { return morph; }

    public void setMorph(DinoMorph m) { morph = m; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", level);
        tag.putString("morph", morph == null ? "" : morph.id);
        return tag;
    }

    public void load(CompoundTag tag) {
        setLevel(tag.contains("level") ? tag.getInt("level") : MIN_LEVEL);
        morph = DinoMorph.byId(tag.getString("morph"));
    }

    public void copyFrom(DinoData o) {
        level = o.level;
        morph = o.morph;
    }
}
