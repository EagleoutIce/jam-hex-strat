package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.creatures.CreatureElf;
import de.flojo.jam.game.creature.creatures.CreatureGoblin;
import de.flojo.jam.game.creature.creatures.CreatureHalfling;
import de.flojo.jam.game.creature.creatures.CreatureImp;
import de.flojo.jam.game.creature.creatures.CreatureLizard;
import de.flojo.jam.game.creature.creatures.CreatureOger;
import de.flojo.jam.game.creature.creatures.CreaturePeasant;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.AnimationRenderer;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;

public class CreatureFactory {

    private static final Spritesheet S_CREATURE_P1_DIEING = Resources.spritesheets()
            .load("creatures/animations/creature_die_p1.png", 128, 128);
    private static final Spritesheet S_CREATURE_P2_DIEING = Resources.spritesheets()
            .load("creatures/animations/creature_die_p2.png", 128, 128);
    private final CreatureCollection creatures;
    private final TrapCollection traps;
    private final Board board;
    private Consumer<Creature> onSelectionChanged = null;
    private Creature selectedCreature = null;

    public CreatureFactory(final String screen, final Board board, final TrapCollection traps) {
        creatures = new CreatureCollection();
        this.board = board;
        this.traps = traps;
        InputController.get().onClicked(this::setActiveCreature, screen);
    }

    // new one for each
    private IRenderData getDieAnimation(final PlayerId playerId) {
        return new AnimationRenderer(playerId.ifOne(S_CREATURE_P1_DIEING, S_CREATURE_P2_DIEING),
                Creature.DIE_DURATION / 25, -128 / 2d, -128 / 1.325d);
    }

    // maybe allow more?
    public void setOnSelectionChanged(final Consumer<Creature> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }

    private void setActiveCreature(final MouseEvent c) {
        if (c.getButton() != MouseEvent.BUTTON1 || !board.doesHover())
            return;

        final Creature oldCreature = selectedCreature;
        this.selectedCreature = creatures.getHighlighted().orElse(null);
        if (oldCreature != selectedCreature) {
            HexStratLogger.log().log(Level.INFO, "Selected Creature: {0}.", this.selectedCreature);
            if (onSelectionChanged != null) {
                onSelectionChanged.accept(selectedCreature);
            }
        }
    }

    public Creature getSelectedCreature() {
        return selectedCreature;
    }

    public ISummonPlayerCreature getSpell(final CreatureId id) {
        switch (id) {
            case PEASANT:
                return this::summonPeasant;
            case IMP:
                return this::summonImp;
            case ELF:
                return this::summonElf;
            case HALFLING:
                return this::summonHalfling;
            case GOBLIN:
                return this::summonGoblin;
            case LIZARD:
                return this::summonLizard;
            case OGER:
                return this::summonOger;
            default:
            case NONE:
                return null;
        }
    }

    public Image getScaledImage(final CreatureId id, final boolean p1) {
        if (id == CreatureId.NONE)
            return null;
        return p1 ? id.getP1Image().getImageScaled() : id.getP2Image().getImageScaled();
    }

    public Creature summonPeasant(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreaturePeasant(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.PEASANT.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonImp(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureImp(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.IMP.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonElf(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureElf(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.ELF.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonHalfling(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureHalfling(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.HALFLING.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonGoblin(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureGoblin(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.GOBLIN.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonLizard(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureLizard(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.LIZARD.getRenderer(pId),
                getDieAnimation(pId));
    }

    public Creature summonOger(final String uniqueName, final Tile startBase, final PlayerId pId, final boolean isOur) {
        return new CreatureOger(uniqueName, startBase, pId, isOur, creatures, traps, CreatureId.OGER.getRenderer(pId),
                getDieAnimation(pId));
    }

    public void removeCreature(final Tile onBase) {
        creatures.removeIf(c -> Objects.equals(c.getCoordinate(), onBase.getCoordinate()));
    }

    public CreatureCollection getCreatures() {
        return creatures;
    }

    public Optional<Creature> get(final BoardCoordinate coordinate) {
        return creatures.get(coordinate);
    }

    public Optional<Creature> get(final Set<Tile> tiles) {
        return creatures.get(tiles);
    }

    public Creature get(final String name) {
        return creatures.get(name);
    }

    public boolean playerOneOwns() {
        return creatures.playerOneOwns();
    }

    public boolean playerTwoOwns() {
        return creatures.playerTwoOwns();
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

    public boolean noneCanDoSomething() {
        return creatures.noneCanDoSomething();
    }

    public void updateCreatures(List<CreatureJson> c, PlayerId ourId) {
        // we cannot reassign to avoid the loss of references
        this.creatures.clear();
        for (CreatureJson cJ : c) {
            getSpell(cJ.getId()).summon(cJ.getName(), board.getTile(cJ.getPos()),
                    cJ.getOwner(), cJ.getOwner() == ourId);
        }
        this.selectedCreature = null;
    }

}
