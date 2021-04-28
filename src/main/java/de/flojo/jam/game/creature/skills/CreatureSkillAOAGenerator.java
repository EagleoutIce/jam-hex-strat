package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.util.HexMaths;

import java.util.HashSet;
import java.util.Set;

public class CreatureSkillAOAGenerator {
    private CreatureSkillAOAGenerator() {
        throw new UnsupportedOperationException();
    }

    public static Set<Tile> getAOA(ICreatureSkill skill, Tile start, Board board, CreatureCollection creatures) {
        int maxRange = skill.getMaxRange();
        if (start.getTerrainType().isRaised()) {
            maxRange += skill.bonusOnRaised();
        }

        switch (skill.getAOA()) {
            case LINE:
                return getAOALine(start, board, creatures, skill.getMinRange(), maxRange,
                                  skill.getTarget().equals(TargetOfSkill.TILE));
            case CIRCLE:
                return getAOACircle(start, skill.getMinRange(), maxRange,
                                    skill.getTarget().equals(TargetOfSkill.TILE));
            default:
            case SINGLE:
                return Set.of(start);
        }
    }

    private static Set<Tile> getAOACircle(final Tile start, final int minRange, final int maxRange,
                                          final boolean emptyOnly) {
        // kill me
        Set<Tile> tiles = new HashSet<>();
        tiles.add(start);
        for (var i = 0; i < maxRange; i++) {
            final Set<Tile> newTiles = new HashSet<>();
            for (Tile t : tiles)
                newTiles.addAll(t.getNeighbours());
            tiles.addAll(newTiles);
        }
        Set<Tile> killTiles = new HashSet<>();
        killTiles.add(start);
        for (var i = 0; i < minRange; i++) {
            final Set<Tile> newTiles = new HashSet<>();
            for (Tile t : killTiles)
                newTiles.addAll(t.getNeighbours());
            killTiles.addAll(newTiles);
        }
        tiles.removeIf(killTiles::contains);
        if (emptyOnly)
            tiles.removeIf(t -> !t.getTerrainType().equals(TerrainTile.EMPTY) && !t.getTerrainType().equals(
                    TerrainTile.GRASS_HILL));
        return tiles;
    }

    private static Set<Tile> getAOALine(Tile start, Board board, CreatureCollection creatures, int minRange,
                                        int maxRange, boolean emptyOnly) {
        Set<Tile> tiles = new HashSet<>();
        Set<Tile> neighbours = start.getNeighbours();
        for (var tile : neighbours) {
            BoardCoordinate delta = HexMaths.decodeDelta(start.getCoordinate(), tile.getCoordinate());
            getAOASingleLine(start, board, creatures, minRange, maxRange, delta.x, delta.y, tiles);
        }
        tiles.remove(start);
        if (emptyOnly)
            tiles.removeIf(t -> !t.getTerrainType().equals(TerrainTile.EMPTY));
        return tiles;
    }

    // TODO: make more efficient / elegant?
    private static void getAOASingleLine(Tile start, Board board, CreatureCollection creatures, int minRange,
                                         int maxRange, int dx, int dy, Set<Tile> tiles) {
        // start will never be included :D
        var current = start.getCoordinate();
        for (var i = 0; i < maxRange; i++) {
            current = current.translateRelativeX(dx, dy);
            final var currentTile = board.getTile(current);
            // line ended
            if (currentTile == null || currentTile.getTerrainType().blocksLineOfSight())
                return;
            if (i >= minRange)
                tiles.add(currentTile);
            // End on person
            if (creatures.get(current).isPresent())
                return;
        }
    }
}
