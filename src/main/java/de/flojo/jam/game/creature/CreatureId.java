package de.flojo.jam.game.creature;

import de.flojo.jam.Main;
import de.flojo.jam.audio.NoSoundPool;
import de.flojo.jam.audio.RandomSoundPool;
import de.flojo.jam.audio.SoundPool;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.CreatureImageRenderer;
import de.flojo.jam.graphics.renderer.CreatureImagesHost;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.VoidRenderer;
import de.gurkenlabs.litiengine.sound.Sound;

public enum CreatureId {
    // do not map directly here in case of multiple summons
    NONE("Nichts", 0, VoidRenderer.get(), VoidRenderer.get(), new NoSoundPool(), 0, 0),
    PEASANT("Bauer", 10, //
            new CreatureImageRenderer("creatures/bauer_blau.png", "creatures/bauer_blau_glow.png",
                                      CreatureImagesHost.FLY_LILA, -93d / 2.9,
                                      -99 / 1.27d), //
            new CreatureImageRenderer("creatures/bauer_lila.png", "creatures/bauer_lila_glow.png",
                                      CreatureImagesHost.FLY_BLAU,
                                      -93d / 1.53, -99 / 1.27d),
            new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/bauer_ton_", ".ogg", 1, 6),
            4, 1),
    IMP("Kobold", 8, //
        new CreatureImageRenderer("creatures/kobold_blau.png", "creatures/kobold_blau_glow.png",
                                  CreatureImagesHost.FLY_BLAU,
                                  -69 / 1.83d, -95 / 1.31d),//
        new CreatureImageRenderer("creatures/kobold_lila.png", "creatures/kobold_lila_glow.png",
                                  CreatureImagesHost.FLY_LILA,
                                  -69 / 2.22d, -95 / 1.31d),
        new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/kobold_ton_", ".ogg", 1, 6),
        3, 1),
    ELF("Elf", 15, //
        new CreatureImageRenderer("creatures/elf_blau.png", "creatures/elf_blau_glow.png",
                                  CreatureImagesHost.FLY_BLAU, -76 / 2.34d,
                                  -92 / 1.27d), //
        new CreatureImageRenderer("creatures/elf_lila.png", "creatures/elf_lila_glow.png",
                                  CreatureImagesHost.FLY_LILA, -76 / 1.78d,
                                  -92 / 1.27d),
        new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/elf_ton_", ".ogg", 1, 6),
        4, 2),
    HALFLING("Halbling", 13, //
             new CreatureImageRenderer("creatures/halbling_blau.png", "creatures/halbling_blau_glow.png",
                                       CreatureImagesHost.FLY_BLAU,
                                       -73 / 2.3d, -102 / 1.24d), //
             new CreatureImageRenderer("creatures/halbling_lila.png", "creatures/halbling_lila_glow.png",
                                       CreatureImagesHost.FLY_LILA,
                                       -73 / 1.65d, -102 / 1.29d),
             new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/halbling_ton_", ".ogg", 1, 6),
             3, 4),
    GOBLIN("Goblin", 15, //
           new CreatureImageRenderer("creatures/goblin_blau.png", "creatures/goblin_blau_glow.png",
                                     CreatureImagesHost.FLY_BLAU,
                                     -76 / 2.1d, -97 / 1.30d), //
           new CreatureImageRenderer("creatures/goblin_lila.png", "creatures/goblin_lila_glow.png",
                                     CreatureImagesHost.FLY_LILA,
                                     -74 / 1.95d, -97 / 1.32d),
           new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/goblin_ton_", ".ogg", 1, 5),
           3, 1),
    LIZARD("Lizard", 8, //
           new CreatureImageRenderer("creatures/echse_blau.png", "creatures/echse_blau_glow.png",
                                     CreatureImagesHost.FLY_BLAU, -314d / 1.7,
                                     -369 / 1.27d, Main.DEFAULT_INTERNAL_SCALE), //
           new CreatureImageRenderer("creatures/echse_lila.png", "creatures/echse_lila_glow.png",
                                     CreatureImagesHost.FLY_LILA,
                                     -314d / 2.3, -369 / 1.27d, Main.DEFAULT_INTERNAL_SCALE),
           new NoSoundPool(),
           // new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/echse_ton_", ".ogg", 1, 6)
           10, 1),
    OGER("Oger", 11, //
         new CreatureImageRenderer("creatures/oger_blau.png", "creatures/oger_blau_glow.png",
                                   CreatureImagesHost.FLY_BLAU, -329d / 1.8,
                                   -421 / 1.27d, Main.DEFAULT_INTERNAL_SCALE), //
         new CreatureImageRenderer("creatures/oger_lila.png", "creatures/oger_lila_glow.png",
                                   CreatureImagesHost.FLY_LILA,
                                   -329d / 2.3, -421 / 1.27d, Main.DEFAULT_INTERNAL_SCALE),
         new RandomSoundPool(Creature.soundPlayGroup, "audio/creatures/oger_ton_", ".ogg", 1, 6),
         3, 3);

    private final String name;
    private final int cost;
    private final IRenderData p1Image;
    private final IRenderData p2Image;
    private final SoundPool<Sound> pool;
    private final int defaultMp;
    private final int defaultAp;

    CreatureId(final String name, int cost, IRenderData p1Image, IRenderData p2Image, SoundPool<Sound> pool,
               final int defaultMp, final int defaultAp) {
        this.name = name;
        this.cost = cost;
        this.p1Image = p1Image;
        this.p2Image = p2Image;
        this.pool = pool;
        this.defaultMp = defaultMp;
        this.defaultAp = defaultAp;
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

    public SoundPool<Sound> getSoundPool() {
        return pool;
    }

    public int getDefaultAp() {
        return defaultAp;
    }

    public int getDefaultMp() {
        return defaultMp;
    }
}
