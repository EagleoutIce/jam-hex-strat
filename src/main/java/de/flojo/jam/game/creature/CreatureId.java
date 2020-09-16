package de.flojo.jam.game.creature;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.CreatureImageRenderer;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.VoidRenderer;

public enum CreatureId {
    // do not map directly here in case of multiple summons
    NONE("Nichts", 0, VoidRenderer.get(), VoidRenderer.get()),
    PEASANT("Bauer", 6, //
            new CreatureImageRenderer("creatures/bauer_blau.png", "creatures/bauer_blau_glow.png", -93d / 2.9,
    -99 / 1.27d), //
            new CreatureImageRenderer("creatures/bauer_lila.png", "creatures/bauer_lila_glow.png",
    -93d / 1.53, -99 / 1.27d)
    ),
    GOBLIN("Kobold", 8, //
        new CreatureImageRenderer("creatures/kobold_blau.png",  "creatures/kobold_blau_glow.png",
    -69 / 1.83d, -95 / 1.31d),//
        new CreatureImageRenderer("creatures/kobold_lila.png", "creatures/kobold_lila_glow.png",
    -69 / 2.22d, -95 / 1.31d)),
    ELF("Elf", 10, //
        new CreatureImageRenderer("creatures/elf_blau.png", "creatures/elf_blau_glow.png", -76 / 2.34d,
    -92 / 1.27d), //
        new CreatureImageRenderer("creatures/elf_lila.png", "creatures/elf_lila_glow.png", -76 / 1.78d,
    -92 / 1.27d)),
    HALFLING("Halbling", 8, //
        new CreatureImageRenderer("creatures/halbling_blau.png", "creatures/halbling_blau_glow.png",
    -73 / 2.3d, -102 / 1.24d), //
        new CreatureImageRenderer("creatures/halbling_lila.png", "creatures/halbling_lila_glow.png",
    -73 / 1.65d, -102 / 1.29d));
    
    private final String name;
    private final int cost;
    private final IRenderData p1Image;
    private final IRenderData p2Image;

    CreatureId(final String name, int cost, IRenderData p1Image, IRenderData p2Image) {
        this.name = name;
        this.cost = cost;
        this.p1Image = p1Image;
        this.p2Image = p2Image;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public IRenderData getP1Image() {
        return p1Image;
    }

    public IRenderData getP2Image() {
        return p2Image;
    }

    public IRenderData getRenderer(PlayerId id) {
        return id == null ? p1Image : id.ifOne(p1Image, p2Image);
    }

}
