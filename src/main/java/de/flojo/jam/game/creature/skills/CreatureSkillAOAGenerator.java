package de.flojo.jam.game.creature.skills;

import java.util.HashSet;
import java.util.Set;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.util.HexMaths;

public class CreatureSkillAOAGenerator {
    
    private CreatureSkillAOAGenerator() {
        throw new UnsupportedOperationException();
    }

    public static Set<Tile> getAOA(ICreatureSkill skill, Tile start, Board board, CreatureCollection creatures) {
        int maxRange = skill.getMaxRange();
        if(start.getTerrainType().isRaised()) {
            maxRange += skill.bonusOnRaised();
        }

        switch(skill.getAOA()) {
            case LINE:
                return getAOALine(start, board, creatures, skill.getMinRange(), maxRange);
            default:
            case SINGLE:
                return Set.of(start);
        }
    }

    private static Set<Tile> getAOALine(Tile start, Board board, CreatureCollection creatures, int minRange, int maxRange) {
        Set<Tile> tiles = new HashSet<>();
        Set<Tile> neighbours = start.getNeighbours();
        for (Tile tile : neighbours) {
            BoardCoordinate delta = HexMaths.decodeDelta(start.getCoordinate(), tile.getCoordinate());
            getAOASingleLine(start, board, creatures, minRange, maxRange, delta.x, delta.y, tiles);
        }
        tiles.remove(start);
        return tiles;
    }

    // TODO: make more efficient / elegant?
    private static void getAOASingleLine(Tile start, Board board, CreatureCollection creatures, int minRange, int maxRange, int dx, int dy, Set<Tile> tiles) {
        // start will never be included :D
        BoardCoordinate current = start.getCoordinate();
        for (int i = 0; i < maxRange; i++) {
            current = current.translateRelativeX(dx, dy);
            Tile currentTile = board.getTile(current);
            // line ended
            if(currentTile == null || currentTile.getTerrainType().blocksLineOfSight())
                return;
            if(i >= minRange)
                tiles.add(currentTile);
            // End on person
            if(creatures.get(current).isPresent())
                return;
        }

    }

}
