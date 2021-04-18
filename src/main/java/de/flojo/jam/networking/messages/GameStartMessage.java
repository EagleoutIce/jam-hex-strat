package de.flojo.jam.networking.messages;

import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.board.traps.TrapJson;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.creature.CreatureJson;

import java.util.List;
import java.util.UUID;

public class GameStartMessage extends MessageContainer {

    private static final long serialVersionUID = -952666793933075961L;

    private final TerrainMap terrain;
    private final List<CreatureJson> creatures;
    private final List<TrapJson> traps;

    public GameStartMessage(UUID clientId, TerrainMap terrain, CreatureCollection creatures, TrapCollection traps) {
        super(MessageTypeEnum.GAME_START, clientId, "");
        this.terrain = terrain;
        this.creatures = creatures.getJsonData();
        this.traps = traps.getJsonData();
    }

    public TerrainMap getTerrain() {
        return terrain;
    }

    public List<CreatureJson> getCreatures() {
        return creatures;
    }

    public List<TrapJson> getTraps() {
        return traps;
    }
}
