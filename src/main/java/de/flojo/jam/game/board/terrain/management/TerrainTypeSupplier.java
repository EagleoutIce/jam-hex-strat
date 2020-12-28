package de.flojo.jam.game.board.terrain.management;

import java.awt.Point;

import de.flojo.jam.game.board.terrain.TerrainTile;

@FunctionalInterface
public interface TerrainTypeSupplier {
	TerrainTile getTerrainAt(Point point);
}
