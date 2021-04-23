package de.flojo.jam.game.board.terrain.management;

import de.flojo.jam.game.board.terrain.TerrainTile;

import java.awt.Point;

@FunctionalInterface
public interface TerrainTypeSupplier {
    TerrainTile getTerrainAt(Point point);
}
