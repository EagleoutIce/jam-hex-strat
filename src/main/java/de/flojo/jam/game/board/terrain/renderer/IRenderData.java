package de.flojo.jam.game.board.terrain.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public interface IRenderData {
    void render(Graphics2D g, Point2D pos, boolean highlight);

    boolean hasImage();

    BufferedImage getImage();
}
