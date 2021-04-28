package de.flojo.jam.graphics.renderer;

import de.flojo.jam.game.board.Tile;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public interface IRenderTileData extends IRenderData {
    default void render(Graphics2D g, Point2D pos, RenderHint... hints) {
        render(g, pos, null, hints);
    }

    void render(Graphics2D g, Point2D pos, Tile tile, RenderHint... hints);
}
