package de.flojo.jam.game.board.terrain.management;

import java.awt.Point;

import de.flojo.jam.game.board.terrain.TerrainType;

@FunctionalInterface
public interface TerrainTypeSupplier {
    
    TerrainType getTerrainAt(Point point);

}
