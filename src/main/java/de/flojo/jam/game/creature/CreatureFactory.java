package de.flojo.jam.game.creature;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.creatures.CreatureElf;
import de.flojo.jam.game.creature.creatures.CreatureGoblin;
import de.flojo.jam.game.creature.creatures.CreatureHalfling;
import de.flojo.jam.game.creature.creatures.CreaturePeasant;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.AnimationRenderer;
import de.flojo.jam.graphics.renderer.CreatureImageRenderer;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.resources.Resources;

public class CreatureFactory {
    private static final IRenderData PEASANT_P1_NORMAL = new CreatureImageRenderer("creatures/bauer_blau.png", "creatures/bauer_blau_glow.png", -93d / 2.9,
            -99 / 1.27d);
    private static final IRenderData PEASANT_P2_NORMAL = new CreatureImageRenderer("creatures/bauer_lila.png", "creatures/bauer_lila_glow.png",
            -93d / 1.53, -99 / 1.27d);

    private static final IRenderData GOBLIN_P1_NORMAL = new CreatureImageRenderer("creatures/kobold_blau.png",  "creatures/kobold_blau_glow.png",
            -69 / 1.83d, -95 / 1.31d);
    private static final IRenderData GOBLIN_P2_NORMAL = new CreatureImageRenderer("creatures/kobold_lila.png", "creatures/kobold_lila_glow.png",
            -69 / 2.22d, -95 / 1.31d);

    private static final IRenderData ELF_P1_NORMAL = new CreatureImageRenderer("creatures/elf_blau.png", "creatures/elf_blau_glow.png", -76 / 2.34d,
            -92 / 1.27d);
    private static final IRenderData ELF_P2_NORMAL = new CreatureImageRenderer("creatures/elf_lila.png", "creatures/elf_lila_glow.png", -76 / 1.78d,
            -92 / 1.27d);

    private static final IRenderData HALFLING_P1_NORMAL = new CreatureImageRenderer("creatures/halbling_blau.png", "creatures/halbling_blau_glow.png",
            -73 / 2.3d, -102 / 1.24d);
    private static final IRenderData HALFLING_P2_NORMAL = new CreatureImageRenderer("creatures/halbling_lila.png", "creatures/halbling_lila_glow.png",
            -73 / 1.65d, -102 / 1.29d);

    private static final Spritesheet S_CREATURE_P1_DIEING = Resources.spritesheets()
            .load("creatures/animations/creature_die_p1.png", 128, 128);
    private static final Spritesheet S_CREATURE_P2_DIEING = Resources.spritesheets()
            .load("creatures/animations/creature_die_p2.png", 128, 128);

    // new one for each
    private IRenderData getDieAnimation(PlayerId playerId) {
        return new AnimationRenderer(playerId.ifOne(S_CREATURE_P1_DIEING, S_CREATURE_P2_DIEING),
                Creature.DIE_DURATION / 25, -128 / 2d, -128 / 1.325d);
    }

    private Consumer<Creature> onSelectionChanged = null;
    private CreatureCollection creatures;
    private TrapCollection traps;
    private Board board;

    public CreatureFactory(final String screen, final Board board, TrapCollection traps) {
        creatures = new CreatureCollection();
        this.board = board;
        this.traps = traps;
        InputController.get().onClicked(this::setActiveCreature, screen);
    }

    // maybe allow more?
    public void setOnSelectionChanged(Consumer<Creature> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }

    private Creature selectedCreature = null;

    private void setActiveCreature(MouseEvent c) {
        if (c.getButton() != MouseEvent.BUTTON1 || !board.doesHover())
            return;

        Creature oldCreature = selectedCreature;
        this.selectedCreature = creatures.getHighlighted().orElse(null);
        if (oldCreature != selectedCreature) {
            Game.log().log(Level.INFO, "Selected Creature: {0}.", this.selectedCreature);
            if(onSelectionChanged != null) {
                onSelectionChanged.accept(selectedCreature);
            }
        }
    }

    public Creature getSelectedCreature() {
        return selectedCreature;
    }

    public ISummonPlayerCreature getSpell(CreatureId id) {
        switch (id) {
            case PEASANT:
                return this::summonPeasant;
            case GOBLIN:
                return this::summonGoblin;
            case ELF:
                return this::summonElf;
            case HALFLING:
                return this::summonHalfling;
            default:
            case NONE:
                return null;
        }
    }

    public BufferedImage getBufferedImage(CreatureId id, boolean p1) {
        switch (id) {
            case PEASANT:
                return p1 ? PEASANT_P1_NORMAL.getImage() : PEASANT_P2_NORMAL.getImage();
            case GOBLIN:
                return p1 ? GOBLIN_P1_NORMAL.getImage() : GOBLIN_P2_NORMAL.getImage();
            case ELF:
                return p1 ? ELF_P1_NORMAL.getImage() : ELF_P2_NORMAL.getImage();
            case HALFLING:
                return p1 ? HALFLING_P1_NORMAL.getImage() : HALFLING_P2_NORMAL.getImage();
            default:
            case NONE:
                return null;
        }
    }

    public Creature summonPeasant(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreaturePeasant(uniqueName, startBase, pId, creatures, traps,
                pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL), getDieAnimation(pId));
    }

    public Creature summonGoblin(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureGoblin(uniqueName, startBase, pId, creatures, traps,
                pId.ifOne(GOBLIN_P1_NORMAL, GOBLIN_P2_NORMAL), getDieAnimation(pId));
    }

    public Creature summonElf(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureElf(uniqueName, startBase, pId, creatures, traps, pId.ifOne(ELF_P1_NORMAL, ELF_P2_NORMAL),
                getDieAnimation(pId));
    }

    public Creature summonHalfling(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureHalfling(uniqueName, startBase, pId, creatures, traps,
                pId.ifOne(HALFLING_P1_NORMAL, HALFLING_P2_NORMAL), getDieAnimation(pId));
    }

    public void removeCreature(Tile onBase) {
        creatures.removeIf(c -> Objects.equals(c.getCoordinate(), onBase.getCoordinate()));
    }

    public CreatureCollection getCreatures() {
        return creatures;
    }

    public Optional<Creature> get(BoardCoordinate coordinate) {
        return creatures.get(coordinate);
    }

    public Creature get(String name) {
        return creatures.get(name);
    }

    public boolean isEmpty() {
        return creatures.isEmpty();
    }

    public int size() {
        return creatures.size();
    }

    public void removeAll() {
        this.creatures.clear();
    }

	public void resetAll() {
        creatures.resetAll();
	}

}
