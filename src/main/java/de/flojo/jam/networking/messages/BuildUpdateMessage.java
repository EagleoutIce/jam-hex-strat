package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.terrain.TerrainMap;

import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BuildUpdateMessage that = (BuildUpdateMessage) o;
        return Objects.equals(getMap(), that.getMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMap());
    }
}
