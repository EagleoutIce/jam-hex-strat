package de.flojo.jam.game.terrain.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public interface IRenderData {
    void render(Graphics2D g, Point2D pos, boolean highlight);

    boolean hasImage();
}
