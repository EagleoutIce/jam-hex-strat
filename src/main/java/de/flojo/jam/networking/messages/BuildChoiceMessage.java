package de.flojo.jam.networking.messages;

import java.util.UUID;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.CreatureId;

public class BuildChoiceMessage extends MessageContainer {

	private static final long serialVersionUID = -849919061587539244L;

	private TerrainId terrain = null;
	private CreatureId creature = null;
	private TrapId trap = null;
	private BoardCoordinate position;

	public BuildChoiceMessage(UUID clientId, BoardCoordinate position, TerrainId terrain) {
		this(clientId, position, terrain, null, null, "");
	}

	public BuildChoiceMessage(UUID clientId, BoardCoordinate position, CreatureId creature) {
		this(clientId, position, null, creature, null, "");
	}

	public BuildChoiceMessage(UUID clientId, BoardCoordinate position, TrapId trap) {
		this(clientId, position, null, null, trap, "");
	}

	public BuildChoiceMessage(UUID clientId, BoardCoordinate position,TerrainId terrain, CreatureId creature, TrapId trap,  String debugMessage) {
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
