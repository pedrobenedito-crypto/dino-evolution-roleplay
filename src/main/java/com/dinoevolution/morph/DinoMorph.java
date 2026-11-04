package com.dinoevolution.morph;

public enum DinoMorph {
    //           id               largura altura  velocidade dano  vida  alcance
    STEGOSAURUS("stegosaurus",     1.2f,  1.1f,   0.00,      0.0,  6.0,  0.0),
    BRONTOSAURUS("brontosaurus",   1.4f,  2.0f,  -0.05,      0.0,  4.0,  1.5),
    RAPTOR_PINK("raptor_pink",     0.7f,  1.2f,   0.15,      0.0,  0.0,  0.0),
    RAPTOR_YELLOW("raptor_yellow", 0.7f,  1.2f,   0.30,      0.0, -2.0,  0.0),
    ANKYLOSAURUS("ankylosaurus",   1.5f,  1.2f,  -0.05,      2.0, 10.0,  0.0);

    public final String id;
    public final float width, height;
    public final double speed, damage, health, reach;

    DinoMorph(String id, float w, float h, double speed, double damage, double health, double reach) {
        this.id = id;
        this.width = w;
        this.height = h;
        this.speed = speed;
        this.damage = damage;
        this.health = health;
        this.reach = reach;
    }

    public static DinoMorph byId(String id) {
        for (DinoMorph m : values()) {
            if (m.id.equals(id)) return m;
        }
        return null;
    }
}
