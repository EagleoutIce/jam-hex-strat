package de.flojo.jam.game.board.terrain;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.management.TerrainData;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.gurkenlabs.litiengine.Game;

// Plants and removes terrains from a board and checks for validity
public class Architect {

    private final Board board;

    public Architect(Board board) {
        this.board = board;
    }

    public void placeImprint(BoardCoordinate at, TerrainImprint imprint) {
        // collect all important points
        final Map<Tile, TerrainType> targetTiles = new HashMap<>();
        TerrainData data = imprint.getData();
        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).size(); x++) {
                TerrainType type = data.get(y).get(x);
                if (type != null && !processSingleTileForImprint(at, imprint, targetTiles, x, y, type))
                    return;
            }
        }
        // no highlight if invalid
        TerrainMap terrainMap = board.getTerrainMap();
        targetTiles.forEach((tile, type) -> terrainMap.updateTerrainAt(tile.getCoordinate(), type));
    }

    private boolean processSingleTileForImprint(BoardCoordinate at, TerrainImprint imprint,
            final Map<Tile, TerrainType> targetTiles, int x, int y, TerrainType type) {
        // transform target in boardCoordinates
        BoardCoordinate effectiveCoordinate = at.translateRelative(x - imprint.getAnchor().x,
                y - imprint.getAnchor().y);
        final Tile targetTile = board.getTile(effectiveCoordinate);
        final TerrainType targetTileType = targetTile == null ? null : targetTile.getTerrainType();
        if (targetTile != null && targetTileType.equals(TerrainType.EMPTY)) {
            targetTiles.put(targetTile, type);
        } else {// invalid as too close to border
            Game.log().log(Level.WARNING, "Tried to place {0} on field {1} for {2} but was: {3}",
                    new Object[] { type, effectiveCoordinate, imprint.getData(), targetTile });
            return false;
        }
        return true;
    }

    public void deleteImprint(BoardCoordinate at, TerrainImprint imprint) {
        final Set<Tile> tiles = new HashSet<>(imprint.getData().getWidth() * imprint.getData().getHeight());
        if (deleteImprintRecursion(at, imprint, imprint.getAnchor(), tiles)) {
            TerrainMap terrainMap = board.getTerrainMap();
            tiles.forEach(tile -> terrainMap.updateTerrainAt(tile.getCoordinate(), TerrainType.EMPTY));
        }
    }

    protected boolean deleteImprintRecursion(BoardCoordinate at, TerrainImprint imprint, Point anchor,
            final Set<Tile> targetTiles) {
        // collect all important points
        TerrainData data = imprint.getData();
        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).size(); x++) {
                TerrainType type = data.get(y).get(x);
                if (type != null && !processSingleTileForImprintRemoval(at, imprint, targetTiles, x, y, type, anchor))
                    return false;
            }
        }
        return true;
    }

    private boolean processSingleTileForImprintRemoval(BoardCoordinate at, TerrainImprint imprint,
            final Set<Tile> targetTiles, int x, int y, TerrainType type, Point anchor) {
        BoardCoordinate effectiveCoordinate = at.translateRelative(x - anchor.x, y - anchor.y);
        final Tile targetTile = board.getTile(effectiveCoordinate);
        final TerrainType targetTileType = targetTile == null ? null : targetTile.getTerrainType();
        if (targetTileType != null) {
            if (targetTiles.add(targetTile) && targetTileType != TerrainType.EMPTY) {
                // recursive delete
                if (!deleteImprintRecursion(targetTile.getCoordinate(),
                        targetTile.getTerrainType().getNode().getImprint(),
                        targetTile.getTerrainType().getNode().getPos(), targetTiles)) {
                    return false;
                }
            }
        } else {// invalid as too close to border
            Game.log().log(Level.WARNING, "Tried to remove {0} on field {1} for {2} but expected: {3}",
                    new Object[] { targetTile, effectiveCoordinate, imprint.getData(), type });
            return false;
        }
        return true;
    }

    public void clearField() {
        TerrainMap terrainMap = board.getTerrainMap();
        for (int y = 0; y < Main.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Main.BOARD_WIDTH; x++) {
                terrainMap.updateTerrainAt(x, y, TerrainType.EMPTY);
            }
        }
    }

}
