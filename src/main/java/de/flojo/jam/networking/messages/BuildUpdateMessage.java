package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.terrain.TerrainMap;

import java.util.UUID;

// Just to be sure we do not send relative but spread like a broadcast
public class BuildUpdateMessage extends MessageContainer {

    private static final long serialVersionUID = 2258696926082699427L;

    // Da abseits von charakteren nichts verteilt wird, wird auch nur
    // die Karte verteilt
    private final TerrainMap map;


    public BuildUpdateMessage(UUID clientId, final TerrainMap map) {
        super(MessageTypeEnum.BUILD_UPDATE, clientId);
        this.map = map;
    }

    public TerrainMap getMap() {
        return map;
    }

}
