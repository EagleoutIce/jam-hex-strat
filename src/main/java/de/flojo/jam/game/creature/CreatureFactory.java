package de.flojo.jam.game.creature;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.creatures.CreatureElf;
import de.flojo.jam.game.creature.creatures.CreatureGoblin;
import de.flojo.jam.game.creature.creatures.CreatureHalfling;
import de.flojo.jam.game.creature.creatures.CreaturePeasant;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;

public class CreatureFactory {
    private static final IRenderData PEASANT_P1_NORMAL = new SimpleImageRenderer("creatures/bauer_blau.png", -93d / 2.9,
            -99 / 1.27d);
    private static final IRenderData PEASANT_P2_NORMAL = new SimpleImageRenderer("creatures/bauer_lila.png",
            -93d / 1.53, -99 / 1.27d);

    private static final IRenderData GOBLIN_P1_NORMAL = new SimpleImageRenderer("creatures/kobold_blau.png",
            -69 / 1.83d, -95 / 1.31d);
    private static final IRenderData GOBLIN_P2_NORMAL = new SimpleImageRenderer("creatures/kobold_lila.png",
            -69 / 2.22d, -95 / 1.31d);

    private static final IRenderData ELF_P1_NORMAL = new SimpleImageRenderer("creatures/elf_blau.png", -76 / 2.34d,
            -92 / 1.27d);
    private static final IRenderData ELF_P2_NORMAL = new SimpleImageRenderer("creatures/elf_lila.png", -76 / 1.78d,
            -92 / 1.27d);

    private static final IRenderData HALFLING_P1_NORMAL = new SimpleImageRenderer("creatures/halbling_blau.png", -73 / 2.3d,
            -102 / 1.24d);
    private static final IRenderData HALFLING_P2_NORMAL = new SimpleImageRenderer("creatures/halbling_lila.png", -73 / 1.65d,
            -102 / 1.29d);

    private CreatureCollection creatures;

    public CreatureFactory() {
        creatures = new CreatureCollection();
        Input.mouse().onClicked(this::setActiveCreature);
    }

    private Creature selectedCreature = null;

    private void setActiveCreature(MouseEvent c) {
        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        this.selectedCreature = creatures.getHighlighted().orElse(null);
        Game.log().log(Level.INFO, "Selected Creature: {0}.", this.selectedCreature);
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
        return new CreaturePeasant(uniqueName, startBase, pId, creatures,
                pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL), pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL));
    }

    public Creature summonGoblin(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureGoblin(uniqueName, startBase, pId, creatures, pId.ifOne(GOBLIN_P1_NORMAL, GOBLIN_P2_NORMAL),
                pId.ifOne(GOBLIN_P1_NORMAL, GOBLIN_P2_NORMAL));
    }

    public Creature summonElf(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureElf(uniqueName, startBase, pId, creatures, pId.ifOne(ELF_P1_NORMAL, ELF_P2_NORMAL),
                pId.ifOne(ELF_P1_NORMAL, ELF_P2_NORMAL));
    }

    public Creature summonHalfling(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreatureHalfling(uniqueName, startBase, pId, creatures, pId.ifOne(HALFLING_P1_NORMAL, HALFLING_P2_NORMAL),
                pId.ifOne(HALFLING_P1_NORMAL, HALFLING_P2_NORMAL));
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

}
