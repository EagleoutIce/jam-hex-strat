package de.flojo.jam.game.board.terrain;

import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.management.TerrainData;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.util.HexMaths;
import de.flojo.jam.util.HexStratLogger;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

// Plants and removes terrains from a board and checks for validity
public class Architect {

    private final Board board;
    private final CreatureFactory factory;
    private final TrapSpawner spawner;

    private PlayerId playerId = null;

    public Architect(Board board, CreatureFactory factory, TrapSpawner spawner) {
        this.board = board;
        this.factory = factory;
        this.spawner = spawner;
    }

    public void setPlayerId(PlayerId playerId) {
        this.playerId = playerId;
    }

    public boolean placeImprint(BoardCoordinate at, TerrainImprint imprint) {
        // collect all important points
        final Map<Tile, TerrainTile> targetTiles = new HashMap<>();
        TerrainData data = imprint.getData();
        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).size(); x++) {
                TerrainTile type = data.get(y).get(x);
                if (type != null && !processSingleTileForImprint(at, imprint, targetTiles, x, y, type))
                    return false;
            }
        }
        // no highlight if invalid
        TerrainMap terrainMap = board.getTerrainMap();
        targetTiles.forEach((tile, type) -> terrainMap.updateTerrainAt(tile.getCoordinate(), type));
        return true;
    }

    private boolean processSingleTileForImprint(BoardCoordinate at, TerrainImprint imprint,
                                                final Map<Tile, TerrainTile> targetTiles, int x, int y,
                                                TerrainTile type) {
        // transform target in boardCoordinates
        BoardCoordinate effectiveCoordinate = at.translateRelativeX(x - imprint.getAnchor().x,
                                                                    y - imprint.getAnchor().y);
        final Tile targetTile = board.getTile(effectiveCoordinate);
        final TerrainTile targetTileType = targetTile == null ? null : targetTile.getTerrainType();
        if (targetTile != null && targetTileType.equals(TerrainTile.EMPTY) && notAgainstOtherPlacements(targetTile)) {
            targetTiles.put(targetTile, type);
        } else {// invalid as too close to border
            HexStratLogger.log().log(Level.WARNING, "Tried to place {0} on field {1} for {2} but was: {3}",
                                     new Object[]{type, effectiveCoordinate, imprint.getData(), targetTile});
            return false;
        }
        return true;
    }

    private boolean notAgainstOtherPlacements(Tile tile) {
        return notAgainstPlayer(tile) && factory.get(tile.getCoordinate()).isEmpty() && spawner.get(
                tile.getCoordinate()).isEmpty();
    }

    private boolean notAgainstPlayer(Tile tile) {
        return playerId == null || playerId == tile.getPlacementOwner();
    }

    public void deleteImprint(BoardCoordinate at, TerrainImprint imprint) {
        final Set<Tile> tiles = new HashSet<>(imprint.getData().getWidth() * imprint.getData().getHeight());
        if (deleteImprintRecursion(at, imprint, imprint.getAnchor(), tiles)) {
            TerrainMap terrainMap = board.getTerrainMap();
            tiles.forEach(tile -> terrainMap.updateTerrainAt(tile.getCoordinate(), TerrainTile.EMPTY));
        }
    }

    protected boolean deleteImprintRecursion(BoardCoordinate at, TerrainImprint imprint, Point anchor,
                                             final Set<Tile> targetTiles) {
        // collect all important points
        TerrainData data = imprint.getData();
        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).size(); x++) {
                TerrainTile type = data.get(y).get(x);
                if (type != null && !processSingleTileForImprintRemoval(at, imprint, targetTiles, x, y, type, anchor))
                    return false;
            }
        }
        return true;
    }

    private boolean processSingleTileForImprintRemoval(BoardCoordinate at, TerrainImprint imprint,
                                                       final Set<Tile> targetTiles, int x, int y, TerrainTile type,
                                                       Point anchor) {
        BoardCoordinate effectiveCoordinate = at.translateRelativeX(x - anchor.x, y - anchor.y);
        final Tile targetTile = board.getTile(effectiveCoordinate);
        final TerrainTile targetTileType = targetTile == null ? null : targetTile.getTerrainType();
        if (targetTileType != null) {
            if (targetTiles.add(targetTile) && targetTileType != TerrainTile.EMPTY) {
                // recursive delete
                return deleteImprintRecursion(targetTile.getCoordinate(),
                                              targetTile.getTerrainType().getNode().getImprint(),
                                              targetTile.getTerrainType().getNode().getPos(), targetTiles);
            }
        } else {// invalid as too close to border
            HexStratLogger.log().log(Level.WARNING, "Tried to remove {0} on field {1} for {2} but expected: {3}",
                                     new Object[]{targetTile, effectiveCoordinate, imprint.getData(), type});
            return false;
        }
        return true;
    }

    public void clearField() {
        TerrainMap terrainMap = board.getTerrainMap();
        for (int y = 0; y < GameField.BOARD_HEIGHT; y++) {
            for (int x = 0; x < HexMaths.effectiveWidth(GameField.BOARD_WIDTH); x++) {
                terrainMap.updateTerrainAt(x, y, TerrainTile.EMPTY);
            }
        }
    }

}
