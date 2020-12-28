package de.flojo.jam.networking.messages;

import java.util.UUID;

import de.flojo.jam.game.board.terrain.TerrainMap;

// Just to be sure we do not send relative but spread like a broadcast
public class BuildUpdateMessage extends MessageContainer {

	private static final long serialVersionUID = 2258696926082699427L;

	// Da abseits von charakteren nichts verteilt wird, wird auch nur
	// die Karte verteilt
	private TerrainMap map;


	public BuildUpdateMessage(UUID clientId, final TerrainMap map) {
		super(MessageTypeEnum.BUILD_UPDATE, clientId);
		this.map = map;
	}

	public TerrainMap getMap() {
		return map;
	}

}
