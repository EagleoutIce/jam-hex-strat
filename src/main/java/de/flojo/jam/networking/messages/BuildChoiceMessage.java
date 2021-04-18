package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.CreatureId;

import java.util.UUID;

public class BuildChoiceMessage extends MessageContainer {

    private static final long serialVersionUID = -849919061587539244L;

    private final TerrainId terrain;
    private final CreatureId creature;
    private final TrapId trap;
    private final BoardCoordinate position;

    public BuildChoiceMessage(UUID clientId, BoardCoordinate position, TerrainId terrain, CreatureId creature, TrapId trap, String debugMessage) {
        super(MessageTypeEnum.BUILD_CHOICE, clientId, debugMessage);
        this.terrain = terrain;
        this.creature = creature;
        this.trap = trap;
        this.position = position;
    }

    public CreatureId getCreature() {
        return creature;
    }

    public TerrainId getTerrain() {
        return terrain;
    }

    public BoardCoordinate getPosition() {
        return position;
    }

    public TrapId getTrap() {
        return trap;
    }

}
